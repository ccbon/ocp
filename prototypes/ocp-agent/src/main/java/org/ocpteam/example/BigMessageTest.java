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
import org.ocpteam.network.TCPClient;

public class BigMessageTest {
	public static void main(String[] args) {
		JLG.debug_on();
		try {
			IProtocol p = new MyProtocol();
			TCPListener l = new TCPListener();
			l.init();
			l.setProtocol(p);
			l.setUrl(new URI("tcp://localhost:12345"));
			l.start();

			TCPClient c = new TCPClient("localhost", 12345, p);

			Socket socket = c.borrowSocket();
			DataInputStream in = new DataInputStream(socket.getInputStream());
			DataOutputStream out = new DataOutputStream(
					socket.getOutputStream());

			// send a big message.
			int n = 10;
			out.writeInt(n);
			for (int i = 0; i < n; i++) {
				out.writeInt(i);
			}

			int nbr = in.readInt();
			for (int i = 0; i < nbr; i++) {
				int size = in.readInt();
				byte[] buffer = new byte[size];
				in.read(buffer, 0, size);
				Serializable s = JLG.deserialize(buffer);
				JLG.debug("serializable=" + s);
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
	public void process(DataInputStream in, DataOutputStream out,
			Socket clientSocket) throws Exception {
		// read the first object
		JLG.debug("about to read object");
		int n = in.readInt();
		out.writeInt(n);
		for (int i = 0; i < n; i++) {
			byte[] s = JLG.serialize(new MyObject(i));
			out.writeInt(s.length);
			out.write(s);
		}		
		JLG.debug("end process");
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