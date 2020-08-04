package com.loohp.limbo.Server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerConnection extends Thread {
	
	private ServerSocket serverSocket;
	private List<ClientConnection> clients;
	private String ip;
	private int port;
	private KeepAliveSender keepAliveSender;

	public ServerConnection(String ip, int port) {
		clients = new ArrayList<ClientConnection>();
		this.ip = ip;
		this.port = port;
		start();
		keepAliveSender = new KeepAliveSender();
	}
	
	@Override
	public void run() {
		try {
			serverSocket = new ServerSocket(port, 50, InetAddress.getByName(ip));
			System.out.println("Limbo server listening on /" + serverSocket.getInetAddress().getHostName() + ":" + serverSocket.getLocalPort());
	        while (true) {
	            Socket connection = serverSocket.accept();
	            //String str = connection.getInetAddress().getHostName() + ":" + connection.getPort();
				//System.out.println("[/127.0.0.1:57310] <-> InitialHandler has pinged);
	            ClientConnection sc = new ClientConnection(connection);
	            clients.add(sc);
	            sc.start();
	        }
	    } catch(IOException e) {
	        e.printStackTrace();
	    }
	}
	
	public KeepAliveSender getKeepAliveSender() {
		return keepAliveSender;
	}

	public ServerSocket getServerSocket() {
		return serverSocket;
	}

	public List<ClientConnection> getClients() {
		return clients;
	}

}
