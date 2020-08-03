package com.loohp.limbo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.loohp.limbo.Server.ClientConnection;
import com.loohp.limbo.Server.ClientConnection.ClientState;
import com.loohp.limbo.Utils.CustomStringUtils;

public class Console {
	
	private InputStream in;
	private PrintStream out;
	
	public Console(InputStream in, PrintStream out) {
		this.in = in;
		System.setOut(new ConsoleOutputStream(out));
		this.out = System.out;
	}
	
	public void sendMessage(String message) {
		out.println(message);
	}
	
	public void run() {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		while (true) {
			try {
				String[] input = CustomStringUtils.splitStringToArgs(reader.readLine());
				
				if (input[0].equalsIgnoreCase("stop")) {
					for (ClientConnection client : Limbo.getInstance().getServerConnection().getClients()) {
						client.getSocket().close();
						while (client.getSocket().isConnected()) {
							try {
								TimeUnit.MILLISECONDS.sleep(500);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
					System.exit(0);
				} else if (input[0].equalsIgnoreCase("say")) {
					if (input.length > 1) {
						String message = "[Server] " + String.join(" ", Arrays.copyOfRange(input, 1, input.length));
						sendMessage(message);
						for (ClientConnection client : Limbo.getInstance().getServerConnection().getClients()) {
							if (client.getClientState().equals(ClientState.PLAY)) {
								client.sendMessage(message);
							}
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public class ConsoleOutputStream extends PrintStream {

	    public ConsoleOutputStream(OutputStream out) {
	        super(out);
	    }

	    @Override
	    public void println(String string) {
	    	String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
	        super.println("[" + date + "] " + string);
	    }
	}

}
