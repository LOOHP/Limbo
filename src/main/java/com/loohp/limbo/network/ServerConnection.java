/*
 * This file is part of Limbo.
 *
 * Copyright (C) 2022. LoohpJames <jamesloohp@gmail.com>
 * Copyright (C) 2022. Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.loohp.limbo.network;

import com.loohp.limbo.Limbo;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerConnection extends Thread {

	private final String ip;
	private final int port;
	private final boolean silent;
    private final ExecutorService virtualThreadExecutor = Executors.newVirtualThreadPerTaskExecutor();
	private ServerSocket serverSocket;
	private List<ClientConnection> clients;

	public ServerConnection(String ip, int port, boolean silent) {
		this.clients = new ArrayList<>();
		this.ip = ip;
		this.port = port;
		this.silent = silent;
		start();
	}
	
	@Override
	public void run() {
		try {
			serverSocket = new ServerSocket(port, 50, InetAddress.getByName(ip));
			if (!silent) {
				Limbo.getInstance().getConsole().sendMessage("Limbo server listening on /" + serverSocket.getInetAddress().getHostName() + ":" + serverSocket.getLocalPort());
			}
	        while (true) {
	            Socket connection = serverSocket.accept();
	            ClientConnection clientTask  = new ClientConnection(connection);
	            clients.add(clientTask);
	            virtualThreadExecutor.submit(clientTask);
	        }
	    } catch(IOException e) {
	        e.printStackTrace();
	    }
	}

	public ServerSocket getServerSocket() {
		return serverSocket;
	}

	public List<ClientConnection> getClients() {
		return clients;
	}

}
