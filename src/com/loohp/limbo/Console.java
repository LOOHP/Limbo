package com.loohp.limbo;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.stream.Collectors;

import com.loohp.limbo.Commands.CommandSender;
import com.loohp.limbo.GUI.ConsoleTextOutput;
import com.loohp.limbo.Utils.CustomStringUtils;

import net.md_5.bungee.api.chat.BaseComponent;

public class Console implements CommandSender {
	
	private InputStream in;
	private PrintStream out;
	@SuppressWarnings("unused")
	private PrintStream err;
	protected PrintStream logs;
	
	private final String CONSOLE = "CONSOLE";
	
	public Console(InputStream in, PrintStream out, PrintStream err) throws FileNotFoundException {
		String fileName = new SimpleDateFormat("yyyy'-'MM'-'dd'_'HH'-'mm'-'ss'_'zzz'.log'").format(new Date());
        File dir = new File("logs");
        dir.mkdirs();
        File logs = new File(dir, fileName);
        this.logs = new PrintStream(logs);
        
        System.setIn(in == null ? new ByteArrayInputStream(new byte[0]) : in);
        this.in = System.in;
		System.setOut(new ConsoleOutputStream(out == null ? new PrintStream(new PrintStream(new OutputStream() {
			@Override
            public void write(int b) {
                //DO NOTHING
            }
        })) : out, this.logs));
		this.out = System.out;
		System.setErr(new ConsoleErrorStream(err == null ? new PrintStream(new PrintStream(new OutputStream() {
			@Override
            public void write(int b) {
                //DO NOTHING
            }
        })) : err, this.logs));
		this.err = System.err;
	}
	
	public String getName() {
		return CONSOLE;
	}

	@Override
	public void sendMessage(BaseComponent component) {
		sendMessage(new BaseComponent[] {component});
	}
	
	@Override
	public void sendMessage(BaseComponent[] component) {
		sendMessage(String.join("", Arrays.asList(component).stream().map(each -> each.toLegacyText()).collect(Collectors.toList())));
	}
	
	public boolean hasPermission(String permission) {
		return Limbo.getInstance().getPermissionsManager().hasPermission(this, permission);
	}
	
	public void sendMessage(String message) {
		out.println(message);
	}
	
	protected void run() {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		while (true) {
			try {
				String[] input = CustomStringUtils.splitStringToArgs(reader.readLine());				
				Limbo.getInstance().dispatchCommand(this, input);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static class ConsoleOutputStream extends PrintStream {
		
		private PrintStream logs;
		
		public ConsoleOutputStream(OutputStream out, PrintStream logs) {
	        super(out);
	        this.logs = logs;
	    }

		@Override
		public PrintStream printf(Locale l, String format, Object... args) {
			String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
			ConsoleTextOutput.appendText(String.format(l, "[" + date + " Info]" + format, args));
			logs.printf(l, "[" + date + " Info]" + format, args);
			return super.printf(l, "[" + date + " Info]" + format, args);
		}

		@Override
		public PrintStream printf(String format, Object... args) {
			String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
			ConsoleTextOutput.appendText(String.format("[" + date + " Info]" + format, args));
			logs.printf("[" + date + " Info]" + format, args);
			return super.printf("[" + date + " Info]" + format, args);
		}

		@Override
		public void println() {
			String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
			ConsoleTextOutput.appendText("[" + date + " Info]", true);
			logs.println("[" + date + " Info]");
	        super.println("[" + date + " Info]");
		}

		@Override
		public void println(boolean x) {
			String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
			ConsoleTextOutput.appendText("[" + date + " Info] " + x, true);
			logs.println("[" + date + " Info]" + x);
	        super.println("[" + date + " Info]" + x);
		}

		@Override
		public void println(char x) {
			String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
			ConsoleTextOutput.appendText("[" + date + " Info] " + x, true);
			logs.println("[" + date + " Info]" + x);
	        super.println("[" + date + " Info]" + x);
		}

		@Override
		public void println(char[] x) {
			String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
			ConsoleTextOutput.appendText("[" + date + " Info] " + String.valueOf(x), true);
			logs.println("[" + date + " Info]" + String.valueOf(x));
	        super.println("[" + date + " Info]" + String.valueOf(x));
		}

		@Override
		public void println(double x) {
			String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
			ConsoleTextOutput.appendText("[" + date + " Info] " + x, true);
			logs.println("[" + date + " Info]" + x);
	        super.println("[" + date + " Info]" + x);
		}

		@Override
		public void println(float x) {
			String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
			ConsoleTextOutput.appendText("[" + date + " Info] " + x, true);
			logs.println("[" + date + " Info]" + x);
	        super.println("[" + date + " Info]" + x);
		}

		@Override
		public void println(int x) {
			String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
			ConsoleTextOutput.appendText("[" + date + " Info] " + x, true);
			logs.println("[" + date + " Info]" + x);
	        super.println("[" + date + " Info]" + x);
		}

		@Override
		public void println(long x) {
			String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
			ConsoleTextOutput.appendText("[" + date + " Info] " + x, true);
			logs.println("[" + date + " Info]" + x);
	        super.println("[" + date + " Info]" + x);
		}

		@Override
		public void println(Object x) {
			String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
			ConsoleTextOutput.appendText("[" + date + " Info] " + x, true);
			logs.println("[" + date + " Info]" + x);
	        super.println("[" + date + " Info]" + x);
		}

	    @Override
	    public void println(String string) {
	    	String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
	    	ConsoleTextOutput.appendText("[" + date + " Info] " + string, true);
	    	logs.println("[" + date + " Info] " + string);
	        super.println("[" + date + " Info] " + string);
	    }
	}
	
	public static class ConsoleErrorStream extends PrintStream {
		
		private PrintStream logs;
		
		public ConsoleErrorStream(OutputStream out, PrintStream logs) {
	        super(out);
	        this.logs = logs;
	    }

		@Override
		public PrintStream printf(Locale l, String format, Object... args) {
			String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
			ConsoleTextOutput.appendText(String.format(l, "[" + date + " Error]" + format, args));
			logs.printf(l, "[" + date + " Error]" + format, args);
			return super.printf(l, "[" + date + " Error]" + format, args);
		}

		@Override
		public PrintStream printf(String format, Object... args) {
			String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
			ConsoleTextOutput.appendText(String.format("[" + date + " Error]" + format, args));
			logs.printf("[" + date + " Error]" + format, args);
			return super.printf("[" + date + " Error]" + format, args);
		}

		@Override
		public void println() {
			String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
			ConsoleTextOutput.appendText("[" + date + " Error]", true);
			logs.println("[" + date + " Error]");
	        super.println("[" + date + " Error]");
		}

		@Override
		public void println(boolean x) {
			String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
			ConsoleTextOutput.appendText("[" + date + " Error] " + x, true);
			logs.println("[" + date + " Error]" + x);
	        super.println("[" + date + " Error]" + x);
		}

		@Override
		public void println(char x) {
			String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
			ConsoleTextOutput.appendText("[" + date + " Error] " + x, true);
			logs.println("[" + date + " Error]" + x);
	        super.println("[" + date + " Error]" + x);
		}

		@Override
		public void println(char[] x) {
			String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
			ConsoleTextOutput.appendText("[" + date + " Error] " + String.valueOf(x), true);
			logs.println("[" + date + " Error]" + String.valueOf(x));
	        super.println("[" + date + " Error]" + String.valueOf(x));
		}

		@Override
		public void println(double x) {
			String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
			ConsoleTextOutput.appendText("[" + date + " Error] " + x, true);
			logs.println("[" + date + " Error]" + x);
	        super.println("[" + date + " Error]" + x);
		}

		@Override
		public void println(float x) {
			String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
			ConsoleTextOutput.appendText("[" + date + " Error] " + x, true);
			logs.println("[" + date + " Error]" + x);
	        super.println("[" + date + " Error]" + x);
		}

		@Override
		public void println(int x) {
			String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
			ConsoleTextOutput.appendText("[" + date + " Error] " + x, true);
			logs.println("[" + date + " Error]" + x);
	        super.println("[" + date + " Error]" + x);
		}

		@Override
		public void println(long x) {
			String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
			ConsoleTextOutput.appendText("[" + date + " Error] " + x, true);
			logs.println("[" + date + " Error]" + x);
	        super.println("[" + date + " Error]" + x);
		}

		@Override
		public void println(Object x) {
			String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
			ConsoleTextOutput.appendText("[" + date + " Error] " + x, true);
			logs.println("[" + date + " Error]" + x);
	        super.println("[" + date + " Error]" + x);
		}

	    @Override
	    public void println(String string) {
	    	String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
	    	ConsoleTextOutput.appendText("[" + date + " Error] " + string, true);
	    	logs.println("[" + date + " Error] " + string);
	        super.println("[" + date + " Error] " + string);
	    }
	}

}
