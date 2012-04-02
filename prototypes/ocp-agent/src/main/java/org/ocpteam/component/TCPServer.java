package org.ocpteam.component;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.ocpteam.core.Container;
import org.ocpteam.interfaces.ITCPServerHandler;
import org.ocpteam.misc.JLG;

public class TCPServer extends Container {

	private int port;
	private ServerSocket serverSocket;
	private ITCPServerHandler handler;

	private ExecutorService pool;
	private Set<ITCPServerHandler> handlerSet;
	
	@Override
	public void init() throws Exception {
		super.init();
		handlerSet = Collections.synchronizedSet(new HashSet<ITCPServerHandler>());
	}

	public void setPort(int port) {
		this.port = port;
	}
	
	public void start() {
		pool = Executors.newCachedThreadPool();
		JLG.debug("pool class=" + pool);
		final Throwable t = new Throwable();
		
		pool.execute(new Runnable() {
			
			@Override
			public void run() {
				JLG.debug("starting a TCP server thread on port:" + port);
				JLG.debug("throwable = " + JLG.getStackTrace(t));
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
						Socket clientSocket = serverSocket.accept();
						ITCPServerHandler myHandler = handler.duplicate();
						myHandler.setTCPServer(TCPServer.this);
						myHandler.setSocket(clientSocket);
						pool.execute(myHandler);
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
		ITCPServerHandler[] array = handlerSet.toArray(new ITCPServerHandler[handlerSet.size()]);
		for (ITCPServerHandler h : array) {
			Socket clientSocket = h.getSocket();
			try {
				clientSocket.close();
				unregister(h);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		pool.shutdownNow();
		JLG.debug("pool shutdownNow=" + pool);
		JLG.debug("end stopping a TCP server with port: " + port);
	}

	public void setHandler(ITCPServerHandler handler) {
		this.handler = handler;
	}

	public void register(ITCPServerHandler h) {
		handlerSet.add(h);
	}

	public void unregister(ITCPServerHandler h) {
		handlerSet.remove(h);
	}

}
