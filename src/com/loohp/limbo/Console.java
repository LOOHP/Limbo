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
import java.util.Locale;

import com.loohp.limbo.Player.Player;
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
					Limbo.getInstance().stopServer();
				} else if (input[0].equalsIgnoreCase("say")) {
					if (input.length > 1) {
						String message = "[Server] " + String.join(" ", Arrays.copyOfRange(input, 1, input.length));
						sendMessage(message);
						for (Player each : Limbo.getInstance().getPlayers()) {
							each.sendMessage(message);
						}
					}
				} else if (input[0].equalsIgnoreCase("kick")) {
					String reason = "Disconnected!";
					Player player = input.length > 1 ? Limbo.getInstance().getPlayer(input[1]) : null;
					if (player != null) {
						if (input.length < 2) {
							player.disconnect();
						} else {
							reason = String.join(" ", Arrays.copyOfRange(input, 2, input.length));
							player.disconnect(reason);
						}
						sendMessage("Kicked the player " + input[1] + " for the reason: " + reason);
					} else {
						sendMessage("Player is not online!");
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
		public PrintStream printf(Locale l, String format, Object... args) {
			String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
			return super.printf(l, "[" + date + "] " + format, args);
		}

		@Override
		public PrintStream printf(String format, Object... args) {
			String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
			return super.printf("[" + date + "] " + format, args);
		}

		@Override
		public void println() {
			String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
	        super.println("[" + date + "] ");
		}

		@Override
		public void println(boolean x) {
			String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
	        super.println("[" + date + "] " + x);
		}

		@Override
		public void println(char x) {
			String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
	        super.println("[" + date + "] " + x);
		}

		@Override
		public void println(char[] x) {
			String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
	        super.println("[" + date + "] " + String.valueOf(x));
		}

		@Override
		public void println(double x) {
			String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
	        super.println("[" + date + "] " + x);
		}

		@Override
		public void println(float x) {
			String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
	        super.println("[" + date + "] " + x);
		}

		@Override
		public void println(int x) {
			String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
	        super.println("[" + date + "] " + x);
		}

		@Override
		public void println(long x) {
			String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
	        super.println("[" + date + "] " + x);
		}

		@Override
		public void println(Object x) {
			String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
	        super.println("[" + date + "] " + x);
		}

	    @Override
	    public void println(String string) {
	    	String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
	        super.println("[" + date + "] " + string);
	    }
	}

}
