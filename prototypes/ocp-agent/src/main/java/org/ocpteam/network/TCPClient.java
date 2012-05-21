package org.ocpteam.network;

import java.io.EOFException;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.ocpteam.interfaces.IProtocol;
import org.ocpteam.misc.JLG;

public class TCPClient {

	private String hostname;
	private int port;
	private IProtocol protocol;
	private List<Socket> socketPool;

	public TCPClient(String hostname, int port, IProtocol protocol) {
		this.hostname = hostname;
		this.port = port;
		this.protocol = protocol;
		this.socketPool = Collections
				.synchronizedList(new LinkedList<Socket>());
	}

	public Serializable request(Serializable input)
			throws Exception {
		Serializable output = null;
		Socket socket = null;
		try {
			socket = borrowSocket();
			output = request0(input, socket);
		} catch (Exception e) {
			if (e instanceof SocketException || e instanceof EOFException
					|| e instanceof SocketTimeoutException) {
				JLG.debug("try again (e=" + e + ")");
				destroy(socket);
				socket = createNewSocket();
				output = request0(input, socket);
			} else {
				throw e;
			}
		} finally {
			returnSocket(socket);
		}
		return output;
	}

	public Socket borrowSocket() throws Exception {
		if (socketPool.isEmpty()) {
			return createNewSocket();
		} else {
			Socket socket = socketPool.remove(0);
			return cleanSocket(socket);
		}
	}

	public void returnSocket(Socket socket) throws Exception {
		socketPool.add(socket);
	}

	private Socket cleanSocket(Socket socket) throws Exception {
		if (socket == null || socket.isClosed() || !socket.isBound()
				|| !socket.isConnected()) {
			JLG.debug("socket=" + socket);
			if (socket != null) {
				if (socket.isClosed()) {
					JLG.debug("clientSocket is closed.");
				}
				if (!socket.isBound()) {
					JLG.debug("clientSocket is unbound.");
				}
				if (!socket.isConnected()) {
					JLG.debug("clientSocket is not connected.");
				}
			}
			return createNewSocket();
		}
		return socket;
	}

	private Serializable request0(Serializable input, Socket socket) throws Exception {
		JLG.debug("about to write input on the socket. input=" + input);
		protocol.getStreamSerializer().writeObject(socket, input);
		JLG.debug("input flush");
		Serializable output = protocol.getStreamSerializer().readObject(socket);
		JLG.debug("output received");
		return output;
	}

	public Socket borrowSocket(Serializable input) throws Exception {
		Socket socket = null;
		try {
			socket = borrowSocket();
			send0(input, socket);
		} catch (Exception e) {
			destroy(socket);
			if (e instanceof SocketException || e instanceof EOFException
					|| e instanceof SocketTimeoutException) {
				JLG.debug("try again (e=" + e + ")");
				socket = createNewSocket();
				send0(input, socket);
			} else {
				throw e;
			}
		}
		return socket;
	}
	
	public void send(Serializable input) throws Exception {
		Socket socket = borrowSocket(input);
		returnSocket(socket);
	}

	private void destroy(Socket socket) {
		if (socket != null) {
			try {
				socket.close();
			} catch (Exception e) {
			}
		}
	}

	public void send0(Serializable input, Socket socket) throws Exception {
		JLG.debug("about to write input on the socket. input=" + input);
		protocol.getStreamSerializer().writeObject(socket, input);
		JLG.debug("input flush");
	}

	public Socket createNewSocket() throws Exception {
		JLG.debug("start new socket on " + hostname + ":" + port);
		Socket socket = new Socket();
		socket.setSendBufferSize(32768);
		socket.setReceiveBufferSize(32768);
		// socket.setSoTimeout(1000);
		// socket.setReuseAddress(true);
		socket.connect(new InetSocketAddress(hostname, port));
		JLG.debug("end new socket");
		return socket;
	}

	public void releaseSocket() {
		for (Socket socket : socketPool) {
			try {
				socket.close();
			} catch (Exception e) {
			}
		}
	}
}
