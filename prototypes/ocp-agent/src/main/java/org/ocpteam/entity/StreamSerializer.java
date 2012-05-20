package org.ocpteam.entity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.net.Socket;

import org.ocpteam.interfaces.IStreamSerializer;
import org.ocpteam.misc.JLG;
import org.ocpteam.serializable.EOMObject;

public class StreamSerializer implements IStreamSerializer {

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
			throw new StreamCorruptedException("Message length = " + length + ". Too big object for allocating space.");
		}
		byte[] input = new byte[length];
		int so_rcvbuf = 2048; //socket.getReceiveBufferSize() - 1192;
		int remaining = length;
		int start = 0;
		while (remaining > so_rcvbuf) {
			JLG.debug("reading " + so_rcvbuf + " byte. start=" + start);
			in.read(input, start, so_rcvbuf);
			remaining -= so_rcvbuf;
			start += so_rcvbuf;
			writeAck(socket);
		}
		JLG.debug("reading " + remaining + " byte. start=" + start);
		in.read(input, start, remaining);
		return JLG.deserialize(input);
	}

	@Override
	public void writeAck(Socket socket) throws Exception {
		socket.getOutputStream().write(0);
		socket.getOutputStream().flush();
	}

	@Override
	public void readAck(Socket socket) throws Exception {
		int ack = socket.getInputStream().read();
		if (ack != 0) {
			throw new Exception("ack should be 0. it is " + ack);
		}
	}

	@Override
	public void writeObject(Socket socket, Serializable o) throws Exception {
		DataOutputStream out = new DataOutputStream(socket.getOutputStream());
		JLG.debug("start");
		if (o == null) {
			JLG.debug("o is null");
			out.writeInt(0);
			out.flush();
		} else {
			JLG.debug("about to serialize");
			byte[] input = serialize(o);
			JLG.debug("about to write the length=" + input.length);
			out.writeInt(input.length);
			out.flush();
			JLG.debug("about to write the content");
			
			int so_rcvbuf = 2048; //socket.getReceiveBufferSize() - 1192;
			int remaining = input.length;
			int start = 0;
			while (remaining > so_rcvbuf) {
				JLG.debug("writing " + so_rcvbuf + " bytes. start=" + start);
				out.write(input, start, so_rcvbuf);
				out.flush();
				remaining -= so_rcvbuf;
				start += so_rcvbuf;
				readAck(socket);
			}
			JLG.debug("reading " + remaining + " byte. start=" + start);
			out.write(input, start, remaining);
			out.flush();
		}
		JLG.debug("write performed well");
	}

	@Override
	public void writeEOM(Socket socket) throws Exception {
		DataOutputStream out = new DataOutputStream(socket.getOutputStream());
		out.writeInt(-1);
	}

	@Override
	public byte[] serialize(Serializable o) throws Exception {
		return JLG.serialize(o);
	}

}
