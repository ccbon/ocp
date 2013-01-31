package org.ocpteam.entity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.net.Socket;

import org.ocpteam.component.JavaSerializer;
import org.ocpteam.interfaces.ISerializer;
import org.ocpteam.interfaces.IStreamSerializer;
import org.ocpteam.misc.LOG;
import org.ocpteam.serializable.EOMObject;

public class StreamSerializer implements IStreamSerializer {

	private ISerializer ser = new JavaSerializer();

	@Override
	public Serializable readObject(Socket socket) throws Exception {
		DataInputStream in = new DataInputStream(socket.getInputStream());
		int length = in.readInt();
		if (length == -1) {
			return new EOMObject();
		}
		if (length == 0) {
			return null;
		}
		if (length > 1000000 || length < 0) {
			throw new StreamCorruptedException("Message length = " + length
					+ ". Too big object for allocating space.");
		}
		byte[] input = new byte[length];
		int so_rcvbuf = 32768; // socket.getReceiveBufferSize() - 1192;
		int remaining = length;
		int start = 0;
		while (remaining > so_rcvbuf) {
			LOG.info("reading " + so_rcvbuf + " byte. start=" + start);
			read(in, input, start, so_rcvbuf);
			remaining -= so_rcvbuf;
			start += so_rcvbuf;
		}
		LOG.info("reading " + remaining + " byte. start=" + start);
		read(in, input, start, remaining);
		return ser.deserialize(input);
	}

	private void read(DataInputStream in, byte[] input, int start, int length)
			throws Exception {
		int total_readed = 0;
		while (total_readed < length) {
			int readed = in.read(input, start + total_readed, length
					- total_readed);
			total_readed += readed;
			LOG.info("total_readed " + total_readed);
		}
	}

	@Override
	public void writeObject(Socket socket, Serializable o) throws Exception {
		DataOutputStream out = new DataOutputStream(socket.getOutputStream());
		LOG.info("start");
		if (o == null) {
			LOG.info("o is null");
			out.writeInt(0);
			out.flush();
		} else {
			LOG.info("about to serialize");
			byte[] input = serialize(o);
			LOG.info("about to write the length=" + input.length);
			out.writeInt(input.length);
			out.flush();
			LOG.info("about to write the content");
			LOG.info("SO_RCVBUF=" + socket.getReceiveBufferSize()
					+ " and SO_SNDBUF=" + socket.getSendBufferSize());

			int so_rcvbuf = 32768; // socket.getReceiveBufferSize() - 1192;
			int remaining = input.length;
			int start = 0;
			while (remaining > so_rcvbuf) {
				LOG.info("writing " + so_rcvbuf + " bytes. start=" + start);
				out.write(input, start, so_rcvbuf);
				out.flush();
				remaining -= so_rcvbuf;
				start += so_rcvbuf;
			}
			LOG.info("reading " + remaining + " byte. start=" + start);
			out.write(input, start, remaining);
			out.flush();
		}
		LOG.info("write performed well");
	}

	@Override
	public void writeEOM(Socket socket) throws Exception {
		DataOutputStream out = new DataOutputStream(socket.getOutputStream());
		out.writeInt(-1);
	}

	@Override
	public byte[] serialize(Serializable o) throws Exception {
		byte[] result = ser.serialize(o);
		LOG.info("serialized=" + new String(result));
		return result;
	}

	public void setSerializer(ISerializer ser) {
		this.ser = ser;
	}

}
