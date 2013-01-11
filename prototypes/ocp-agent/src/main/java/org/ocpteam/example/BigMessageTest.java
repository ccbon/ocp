package org.ocpteam.example;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.URI;

import org.ocpteam.component.Protocol;
import org.ocpteam.component.TCPListener;
import org.ocpteam.interfaces.IProtocol;
import org.ocpteam.misc.JLG;
import org.ocpteam.misc.LOG;
import org.ocpteam.network.TCPClient;

public class BigMessageTest {
	public static void main(String[] args) {
		LOG.debug_on();
		try {
			IProtocol p = new MyProtocol();
			TCPListener l = new TCPListener();
			l.init();
			l.setProtocol(p);
			l.setUrl(new URI("tcp://localhost:12345"));
			l.start();

			TCPClient c = new TCPClient("localhost", 12345, p);

			Socket socket = c.borrowSocket();
			int bufferSize = socket.getSendBufferSize();
			LOG.debug("bufferSize=" + bufferSize);
			DataInputStream in = new DataInputStream(socket.getInputStream());
			DataOutputStream out = new DataOutputStream(
					socket.getOutputStream());

			// send a big message.
			int n = 10000;
			out.writeInt(n);
			out.flush();
			LOG.debug("big message sent.");
			int nbr = in.readInt();
			LOG.debug("new length received: " + nbr);

			if (n != nbr) {
				throw new Exception("length differs");
			}
			for (int i = 0; i < nbr; i++) {
				out.writeInt(i);
				out.flush();

				int size = in.readInt();
				LOG.debug("object length: " + size);
				byte[] buffer = new byte[size];
				in.read(buffer, 0, size);
				Serializable s = JLG.deserialize(buffer);
				LOG.debug("serializable=" + s);
			}
			l.stop();
			c.returnSocket(socket);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

class MyProtocol extends Protocol {

	@Override
	public void process(Socket clientSocket) throws Exception {
		DataInputStream in = new DataInputStream(clientSocket.getInputStream());
		DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
		// read the first object
		LOG.debug("about to read object");
		int n = in.readInt();
		LOG.debug("n = " + n);
		out.writeInt(n);
		out.flush();
		LOG.debug("length sent");
		for (int i = 0; i < n; i++) {
			int x = in.readInt();
			byte[] s = JLG.serialize(new MyObject(x));
			out.writeInt(s.length);
			out.write(s);
			out.flush();
			LOG.debug("object sent: " + x);
		}
		LOG.debug("end process");
	}
}

class MyObject implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int a;

	public MyObject(int a) {
		this.a = a;
	}

	@Override
	public String toString() {
		return getClass() + ": " + a;
	}
}