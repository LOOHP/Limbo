package com.loohp.limbo;

import java.io.BufferedReader;
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
        
		System.setIn(in);
		this.in = in;
		System.setOut(new ConsoleOutputStream(out, this.logs));
		this.out = System.out;
		System.setErr(new ConsoleErrorStream(err, this.logs));
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
	
	public void run() {
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
			logs.printf(l, "[" + date + " INFO]" + format, args);
			return super.printf(l, "[" + date + " INFO]" + format, args);
		}

		@Override
		public PrintStream printf(String format, Object... args) {
			String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
			logs.printf("[" + date + " INFO]" + format, args);
			return super.printf("[" + date + " INFO]" + format, args);
		}

		@Override
		public void println() {
			String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
			logs.println("[" + date + " INFO]");
	        super.println("[" + date + " INFO]");
		}

		@Override
		public void println(boolean x) {
			String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
			logs.println("[" + date + " INFO]" + x);
	        super.println("[" + date + " INFO]" + x);
		}

		@Override
		public void println(char x) {
			String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
			logs.println("[" + date + " INFO]" + x);
	        super.println("[" + date + " INFO]" + x);
		}

		@Override
		public void println(char[] x) {
			String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
			logs.println("[" + date + " INFO]" + String.valueOf(x));
	        super.println("[" + date + " INFO]" + String.valueOf(x));
		}

		@Override
		public void println(double x) {
			String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
			logs.println("[" + date + " INFO]" + x);
	        super.println("[" + date + " INFO]" + x);
		}

		@Override
		public void println(float x) {
			String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
			logs.println("[" + date + " INFO]" + x);
	        super.println("[" + date + " INFO]" + x);
		}

		@Override
		public void println(int x) {
			String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
			logs.println("[" + date + " INFO]" + x);
	        super.println("[" + date + " INFO]" + x);
		}

		@Override
		public void println(long x) {
			String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
			logs.println("[" + date + " INFO]" + x);
	        super.println("[" + date + " INFO]" + x);
		}

		@Override
		public void println(Object x) {
			String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
			logs.println("[" + date + " INFO]" + x);
	        super.println("[" + date + " INFO]" + x);
		}

	    @Override
	    public void println(String string) {
	    	String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
	    	logs.println("[" + date + " INFO] " + string);
	        super.println("[" + date + " INFO] " + string);
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
			logs.printf(l, "[" + date + " Error]" + format, args);
			return super.printf(l, "[" + date + " Error]" + format, args);
		}

		@Override
		public PrintStream printf(String format, Object... args) {
			String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
			logs.printf("[" + date + " Error]" + format, args);
			return super.printf("[" + date + " Error]" + format, args);
		}

		@Override
		public void println() {
			String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
			logs.println("[" + date + " Error]");
	        super.println("[" + date + " Error]");
		}

		@Override
		public void println(boolean x) {
			String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
			logs.println("[" + date + " Error]" + x);
	        super.println("[" + date + " Error]" + x);
		}

		@Override
		public void println(char x) {
			String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
			logs.println("[" + date + " Error]" + x);
	        super.println("[" + date + " Error]" + x);
		}

		@Override
		public void println(char[] x) {
			String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
			logs.println("[" + date + " Error]" + String.valueOf(x));
	        super.println("[" + date + " Error]" + String.valueOf(x));
		}

		@Override
		public void println(double x) {
			String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
			logs.println("[" + date + " Error]" + x);
	        super.println("[" + date + " Error]" + x);
		}

		@Override
		public void println(float x) {
			String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
			logs.println("[" + date + " Error]" + x);
	        super.println("[" + date + " Error]" + x);
		}

		@Override
		public void println(int x) {
			String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
			logs.println("[" + date + " Error]" + x);
	        super.println("[" + date + " Error]" + x);
		}

		@Override
		public void println(long x) {
			String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
			logs.println("[" + date + " Error]" + x);
	        super.println("[" + date + " Error]" + x);
		}

		@Override
		public void println(Object x) {
			String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
			logs.println("[" + date + " Error]" + x);
	        super.println("[" + date + " Error]" + x);
		}

	    @Override
	    public void println(String string) {
	    	String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
	    	logs.println("[" + date + " Error] " + string);
	        super.println("[" + date + " Error] " + string);
	    }
	}

}
