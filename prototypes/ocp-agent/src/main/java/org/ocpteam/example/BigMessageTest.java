package org.ocpteam.example;

import java.io.Serializable;
import java.net.URI;

import org.ocpteam.component.Protocol;
import org.ocpteam.component.TCPListener;
import org.ocpteam.interfaces.IProtocol;

public class BigMessageTest {
	public static void main(String[] args) {
		try {
			IProtocol p = new MyProtocol();
			TCPListener l = new TCPListener();
			l.init();
			l.setProtocol(p);
			l.setUrl(new URI("tcp://localhost:12345"));
			l.start();

			//TCPClient c = new TCPClient("localhost", 12345, p);

//			Socket socket = c.getSocket();
//			DataInputStream in = new DataInputStream(socket.getInputStream());
//			DataOutputStream out = new DataOutputStream(
//					socket.getOutputStream());
//
//			// send a big message.
//			int n = 10;
//			out.writeInt(n);
//			for (int i = 0; i < n; i++) {
//				out.writeInt(i);
//			}
//
//			int nbr = in.readInt();
//			for (int i = 0; i < nbr; i++) {
//				int size = in.readInt();
//				byte[] buffer = new byte[size];
//				in.read(buffer, 0, size);
//				Serializable s = JLG.deserialize(buffer);
//				JLG.debug("serializable=" + s);
//			}
//			l.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

class MyProtocol extends Protocol {
	
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