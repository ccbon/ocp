package org.ocpteam.component;

import java.io.EOFException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.ocpteam.core.Container;
import org.ocpteam.interfaces.IProtocol;
import org.ocpteam.misc.JLG;

public class TCPServer extends Container {

	private int port;
	private ServerSocket serverSocket;

	private ExecutorService pool;
	private Set<Socket> socketSet;
	private IProtocol protocol;

	@Override
	public void init() throws Exception {
		super.init();
		socketSet = Collections.synchronizedSet(new HashSet<Socket>());
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void start() {
		pool = Executors.newCachedThreadPool();
		JLG.debug("pool class=" + pool);

		pool.execute(new Runnable() {

			@Override
			public void run() {
				JLG.debug("starting a TCP server thread on port:" + port);
				try {
					if (serverSocket != null) {
						try {
							serverSocket.close();
						} catch (Exception e) {
						}
					}
					serverSocket = new ServerSocket();
					serverSocket.setReuseAddress(true);
					serverSocket.bind(new InetSocketAddress(port));
					while (true) {
						JLG.debug("waiting for a client connection");
						final Socket socket = serverSocket.accept();
						pool.execute(new Runnable() {

							@Override
							public void run() {
								TCPServer.this.register(socket);
								
								try {
									int i = 0;
									while (i < 1000) {
										JLG.debug("wait for message");
										protocol.process(socket);
										i++;
									}
								} catch (SocketException e) {
								} catch (EOFException e) {
								} catch (Exception e) {
									e.printStackTrace();
								} finally {
									try {
										socket.close();
									} catch (Exception e) {
									}
									TCPServer.this.unregister(socket);
									JLG.debug("end");
								}
							}
						});
					}
				} catch (Exception e) {
				}
				JLG.debug("TCP server thread finished");
			}
		});
	}

	public void stop() {
		JLG.debug("stopping a TCP server with port: " + port);
		try {
			if (serverSocket != null) {
				serverSocket.close();
				serverSocket = null;
			}
		} catch (Exception e) {
		}

		// make sure we don't accept new tasks
		pool.shutdown();
		// for all registered handler, make sure their clientSocket is closed.
		Socket[] array = socketSet.toArray(new Socket[socketSet.size()]);
		for (Socket socket : array) {
			try {
				socket.close();
				unregister(socket);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		pool.shutdownNow();
		JLG.debug("pool shutdownNow=" + pool);
		JLG.debug("end stopping a TCP server with port: " + port);
	}

	public void register(Socket socket) {
		socketSet.add(socket);
	}

	public void unregister(Socket socket) {
		socketSet.remove(socket);
	}

	public void setProtocol(IProtocol protocol) {
		this.protocol = protocol;
	}

}
