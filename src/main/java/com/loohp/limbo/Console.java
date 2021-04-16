package com.loohp.limbo;

import com.loohp.limbo.commands.CommandSender;
import com.loohp.limbo.consolegui.ConsoleTextOutput;
import com.loohp.limbo.utils.CustomStringUtils;
import jline.console.ConsoleReader;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.Ansi.Attribute;
import org.jline.reader.*;
import org.jline.reader.LineReader.SuggestionType;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class Console implements CommandSender {

    protected static final Map<ChatColor, String> REPLACEMENTS = new HashMap<>();
    protected final static String ERROR_RED = "\u001B[31;1m";
    protected final static String RESET_COLOR = "\u001B[0m";
    private final static String CONSOLE = "CONSOLE";
    private final static String PROMPT = "> ";

    static {
        REPLACEMENTS.put(ChatColor.BLACK, Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.BLACK).boldOff().toString());
        REPLACEMENTS.put(ChatColor.DARK_BLUE, Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.BLUE).boldOff().toString());
        REPLACEMENTS.put(ChatColor.DARK_GREEN, Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.GREEN).boldOff().toString());
        REPLACEMENTS.put(ChatColor.DARK_AQUA, Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.CYAN).boldOff().toString());
        REPLACEMENTS.put(ChatColor.DARK_RED, Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.RED).boldOff().toString());
        REPLACEMENTS.put(ChatColor.DARK_PURPLE, Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.MAGENTA).boldOff().toString());
        REPLACEMENTS.put(ChatColor.GOLD, Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.YELLOW).boldOff().toString());
        REPLACEMENTS.put(ChatColor.GRAY, Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.WHITE).boldOff().toString());
        REPLACEMENTS.put(ChatColor.DARK_GRAY, Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.BLACK).bold().toString());
        REPLACEMENTS.put(ChatColor.BLUE, Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.BLUE).bold().toString());
        REPLACEMENTS.put(ChatColor.GREEN, Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.GREEN).bold().toString());
        REPLACEMENTS.put(ChatColor.AQUA, Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.CYAN).bold().toString());
        REPLACEMENTS.put(ChatColor.RED, Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.RED).bold().toString());
        REPLACEMENTS.put(ChatColor.LIGHT_PURPLE, Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.MAGENTA).bold().toString());
        REPLACEMENTS.put(ChatColor.YELLOW, Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.YELLOW).bold().toString());
        REPLACEMENTS.put(ChatColor.WHITE, Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.WHITE).bold().toString());
        REPLACEMENTS.put(ChatColor.MAGIC, Ansi.ansi().a(Attribute.BLINK_SLOW).toString());
        REPLACEMENTS.put(ChatColor.BOLD, Ansi.ansi().a(Attribute.UNDERLINE_DOUBLE).toString());
        REPLACEMENTS.put(ChatColor.STRIKETHROUGH, Ansi.ansi().a(Attribute.STRIKETHROUGH_ON).toString());
        REPLACEMENTS.put(ChatColor.UNDERLINE, Ansi.ansi().a(Attribute.UNDERLINE).toString());
        REPLACEMENTS.put(ChatColor.ITALIC, Ansi.ansi().a(Attribute.ITALIC).toString());
        REPLACEMENTS.put(ChatColor.RESET, Ansi.ansi().a(Attribute.RESET).toString());
    }

    protected PrintStream logs;
    private final Terminal terminal;
    private final LineReader tabReader;
    private final ConsoleReader reader;
    private final InputStream in;
    @SuppressWarnings("unused")
    private final PrintStream out;
    @SuppressWarnings("unused")
    private final PrintStream err;

    public Console(InputStream in, PrintStream out, PrintStream err) throws IOException {
        String fileName = new SimpleDateFormat("yyyy'-'MM'-'dd'_'HH'-'mm'-'ss'_'zzz'.log'").format(new Date());
        File dir = new File("logs");
        dir.mkdirs();
        File logs = new File(dir, fileName);
        this.logs = new PrintStream(logs);

        if (in != null) {
            System.setIn(in);
            this.in = System.in;
        } else {
            this.in = null;
        }
        System.setOut(new ConsoleOutputStream(this, out == null ? new PrintStream(new OutputStream() {
            @Override
            public void write(int b) {
                //DO NOTHING
            }
        }) : out, this.logs));
        this.out = System.out;

        System.setErr(new ConsoleErrorStream(this, err == null ? new PrintStream(new OutputStream() {
            @Override
            public void write(int b) {
                //DO NOTHING
            }
        }) : err, this.logs));
        this.err = System.err;

        reader = new ConsoleReader(in, out);
        reader.setExpandEvents(false);
        reader.setHandleUserInterrupt(false);

        terminal = TerminalBuilder.builder().streams(in, out).system(true).jansi(true).build();
        tabReader = LineReaderBuilder.builder().terminal(terminal).completer(new Completer() {
            @Override
            public void complete(LineReader reader, ParsedLine line, List<Candidate> candidates) {
                String[] args = CustomStringUtils.splitStringToArgs(line.line());
                List<String> tab = Limbo.getInstance().getPluginManager().getTabOptions(Limbo.getInstance().getConsole(), args);
                for (String each : tab) {
                    candidates.add(new Candidate(each));
                }
            }
        }).build();
        tabReader.setAutosuggestion(SuggestionType.NONE);
    }

    protected static String translateToConsole(String str) {
        for (Entry<ChatColor, String> entry : REPLACEMENTS.entrySet()) {
            str = str.replace(entry.getKey().toString(), entry.getValue());
        }
        str = str.replaceAll("(?i)" + ChatColor.COLOR_CHAR + "x(" + ChatColor.COLOR_CHAR + "[0-9a-f]){6}", "");
        return str + RESET_COLOR;
    }

    @Override
    public String getName() {
        return CONSOLE;
    }

    @Override
    public boolean hasPermission(String permission) {
        return Limbo.getInstance().getPermissionsManager().hasPermission(this, permission);
    }

    @Override
    public void sendMessage(BaseComponent component, UUID uuid) {
        sendMessage(component);
    }

    @Override
    public void sendMessage(BaseComponent[] component, UUID uuid) {
        sendMessage(component);
    }

    @Override
    public void sendMessage(String message, UUID uuid) {
        sendMessage(message);
    }

    @Override
    public void sendMessage(BaseComponent component) {
        sendMessage(new BaseComponent[]{component});
    }

    @Override
    public void sendMessage(BaseComponent[] component) {
        sendMessage(String.join("", Arrays.asList(component).stream().map(each -> each.toLegacyText()).collect(Collectors.toList())));
    }

    @Override
    public void sendMessage(String message) {
        stashLine();
        String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
        ConsoleTextOutput.appendText(ChatColor.stripColor("[" + date + " Info] " + message), true);
        logs.println(ChatColor.stripColor("[" + date + " Info] " + message));
        try {
            reader.getOutput().append("[").append(date).append(" Info] ").append(translateToConsole(message)).append("\n");
            reader.getOutput().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        unstashLine();
    }

    protected void run() {
        if (in == null) {
            return;
        }
        while (true) {
            try {
                String command = tabReader.readLine(PROMPT).trim();
                if (command.length() > 0) {
                    String[] input = CustomStringUtils.splitStringToArgs(command);
                    new Thread(() -> Limbo.getInstance().dispatchCommand(this, input)).start();
                }
            } catch (UserInterruptException e) {
                System.exit(0);
            } catch (EndOfFileException e) {
                break;
            }
        }
    }

    protected void stashLine() {
        try {
            tabReader.callWidget(LineReader.CLEAR);
        } catch (Exception ignore) {
        }
    }

    protected void unstashLine() {
        try {
            tabReader.callWidget(LineReader.REDRAW_LINE);
            tabReader.callWidget(LineReader.REDISPLAY);
            tabReader.getTerminal().writer().flush();
        } catch (Exception ignore) {
        }
    }

    public static class ConsoleOutputStream extends PrintStream {

        private final PrintStream logs;
        private final Console console;

        public ConsoleOutputStream(Console console, OutputStream out, PrintStream logs) {
            super(out);
            this.logs = logs;
            this.console = console;
        }

        @SuppressWarnings("resource")
        @Override
        public PrintStream printf(Locale l, String format, Object... args) {
            console.stashLine();
            String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
            ConsoleTextOutput.appendText(ChatColor.stripColor(String.format(l, "[" + date + " Info]" + format, args)));
            logs.printf(l, ChatColor.stripColor("[" + date + " Info]" + format), args);
            PrintStream stream = super.printf(l, Console.translateToConsole("[" + date + " Info]" + format), args);
            console.unstashLine();
            return stream;
        }

        @SuppressWarnings("resource")
        @Override
        public PrintStream printf(String format, Object... args) {
            console.stashLine();
            String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
            ConsoleTextOutput.appendText(ChatColor.stripColor(String.format("[" + date + " Info]" + format, args)));
            logs.printf(ChatColor.stripColor("[" + date + " Info]" + format), args);
            PrintStream stream = super.printf(ChatColor.stripColor("[" + date + " Info]" + format), args);
            console.unstashLine();
            return stream;
        }

        @Override
        public void println() {
            console.stashLine();
            String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
            ConsoleTextOutput.appendText(ChatColor.stripColor("[" + date + " Info]"), true);
            logs.println(ChatColor.stripColor("[" + date + " Info]"));
            super.println(ChatColor.stripColor("[" + date + " Info]"));
            console.unstashLine();
        }

        @Override
        public void println(boolean x) {
            console.stashLine();
            String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
            ConsoleTextOutput.appendText(ChatColor.stripColor("[" + date + " Info] " + x), true);
            logs.println(ChatColor.stripColor("[" + date + " Info]" + x));
            super.println(ChatColor.stripColor("[" + date + " Info]" + x));
            console.unstashLine();
        }

        @Override
        public void println(char x) {
            console.stashLine();
            String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
            ConsoleTextOutput.appendText(ChatColor.stripColor("[" + date + " Info] " + x), true);
            logs.println(ChatColor.stripColor("[" + date + " Info]" + x));
            super.println(ChatColor.stripColor("[" + date + " Info]" + x));
            console.unstashLine();
        }

        @Override
        public void println(char[] x) {
            console.stashLine();
            String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
            ConsoleTextOutput.appendText(ChatColor.stripColor("[" + date + " Info] " + String.valueOf(x)), true);
            logs.println(ChatColor.stripColor("[" + date + " Info]" + String.valueOf(x)));
            super.println(ChatColor.stripColor("[" + date + " Info]" + String.valueOf(x)));
            console.unstashLine();
        }

        @Override
        public void println(double x) {
            console.stashLine();
            String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
            ConsoleTextOutput.appendText(ChatColor.stripColor("[" + date + " Info] " + x), true);
            logs.println(ChatColor.stripColor("[" + date + " Info]" + x));
            super.println(ChatColor.stripColor("[" + date + " Info]" + x));
            console.unstashLine();
        }

        @Override
        public void println(float x) {
            console.stashLine();
            String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
            ConsoleTextOutput.appendText(ChatColor.stripColor("[" + date + " Info] " + x), true);
            logs.println(ChatColor.stripColor("[" + date + " Info]" + x));
            super.println(ChatColor.stripColor("[" + date + " Info]" + x));
            console.unstashLine();
        }

        @Override
        public void println(int x) {
            console.stashLine();
            String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
            ConsoleTextOutput.appendText(ChatColor.stripColor("[" + date + " Info] " + x), true);
            logs.println(ChatColor.stripColor("[" + date + " Info]" + x));
            super.println(ChatColor.stripColor("[" + date + " Info]" + x));
            console.unstashLine();
        }

        @Override
        public void println(long x) {
            console.stashLine();
            String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
            ConsoleTextOutput.appendText(ChatColor.stripColor("[" + date + " Info] " + x), true);
            logs.println(ChatColor.stripColor("[" + date + " Info]" + x));
            super.println(ChatColor.stripColor("[" + date + " Info]" + x));
            console.unstashLine();
        }

        @Override
        public void println(Object x) {
            console.stashLine();
            String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
            ConsoleTextOutput.appendText(ChatColor.stripColor("[" + date + " Info] " + x), true);
            logs.println(ChatColor.stripColor("[" + date + " Info]" + x));
            super.println(ChatColor.stripColor("[" + date + " Info]" + x));
            console.unstashLine();
        }

        @Override
        public void println(String string) {
            console.stashLine();
            String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
            ConsoleTextOutput.appendText(ChatColor.stripColor("[" + date + " Info] " + string), true);
            logs.println(ChatColor.stripColor("[" + date + " Info] " + string));
            super.println(ChatColor.stripColor("[" + date + " Info] " + string));
            console.unstashLine();
        }
    }

    public static class ConsoleErrorStream extends PrintStream {

        private final PrintStream logs;
        private final Console console;

        public ConsoleErrorStream(Console console, OutputStream out, PrintStream logs) {
            super(out);
            this.logs = logs;
            this.console = console;
        }

        @SuppressWarnings("resource")
        @Override
        public PrintStream printf(Locale l, String format, Object... args) {
            console.stashLine();
            String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
            ConsoleTextOutput.appendText(ChatColor.stripColor(String.format(l, "[" + date + " Error]" + format, args)));
            logs.printf(l, ChatColor.stripColor("[" + date + " Error]" + format), args);
            PrintStream stream = super.printf(l, ERROR_RED + ChatColor.stripColor("[" + date + " Error]" + format + RESET_COLOR), args);
            console.unstashLine();
            return stream;
        }

        @SuppressWarnings("resource")
        @Override
        public PrintStream printf(String format, Object... args) {
            console.stashLine();
            String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
            ConsoleTextOutput.appendText(ChatColor.stripColor(String.format("[" + date + " Error]" + format, args)));
            logs.printf(ChatColor.stripColor("[" + date + " Error]" + format), args);
            PrintStream stream = super.printf(ERROR_RED + ChatColor.stripColor("[" + date + " Error]" + format + RESET_COLOR), args);
            console.unstashLine();
            return stream;
        }

        @Override
        public void println() {
            console.stashLine();
            String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
            ConsoleTextOutput.appendText(ChatColor.stripColor("[" + date + " Error]"), true);
            logs.println(ChatColor.stripColor("[" + date + " Error]"));
            super.println(ERROR_RED + ChatColor.stripColor("[" + date + " Error]") + RESET_COLOR);
            console.unstashLine();
        }

        @Override
        public void println(boolean x) {
            console.stashLine();
            String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
            ConsoleTextOutput.appendText(ChatColor.stripColor("[" + date + " Error] " + x), true);
            logs.println(ChatColor.stripColor("[" + date + " Error]" + x));
            super.println(ERROR_RED + ChatColor.stripColor("[" + date + " Error]" + x) + RESET_COLOR);
            console.unstashLine();
        }

        @Override
        public void println(char x) {
            console.stashLine();
            String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
            ConsoleTextOutput.appendText(ChatColor.stripColor("[" + date + " Error] " + x), true);
            logs.println(ChatColor.stripColor("[" + date + " Error]" + x));
            super.println(ERROR_RED + ChatColor.stripColor("[" + date + " Error]" + x) + RESET_COLOR);
            console.unstashLine();
        }

        @Override
        public void println(char[] x) {
            console.stashLine();
            String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
            ConsoleTextOutput.appendText(ChatColor.stripColor("[" + date + " Error] " + String.valueOf(x)), true);
            logs.println(ChatColor.stripColor("[" + date + " Error]" + String.valueOf(x)));
            super.println(ERROR_RED + ChatColor.stripColor("[" + date + " Error]" + String.valueOf(x)) + RESET_COLOR);
            console.unstashLine();
        }

        @Override
        public void println(double x) {
            console.stashLine();
            String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
            ConsoleTextOutput.appendText(ChatColor.stripColor("[" + date + " Error] " + x), true);
            logs.println(ChatColor.stripColor("[" + date + " Error]" + x));
            super.println(ERROR_RED + ChatColor.stripColor("[" + date + " Error]" + x) + RESET_COLOR);
            console.unstashLine();
        }

        @Override
        public void println(float x) {
            console.stashLine();
            String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
            ConsoleTextOutput.appendText(ChatColor.stripColor("[" + date + " Error] " + x), true);
            logs.println(ChatColor.stripColor("[" + date + " Error]" + x));
            super.println(ERROR_RED + ChatColor.stripColor("[" + date + " Error]" + x) + RESET_COLOR);
            console.unstashLine();
        }

        @Override
        public void println(int x) {
            console.stashLine();
            String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
            ConsoleTextOutput.appendText(ChatColor.stripColor("[" + date + " Error] " + x), true);
            logs.println(ChatColor.stripColor("[" + date + " Error]" + x));
            super.println(ERROR_RED + ChatColor.stripColor("[" + date + " Error]" + x) + RESET_COLOR);
            console.unstashLine();
        }

        @Override
        public void println(long x) {
            console.stashLine();
            String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
            ConsoleTextOutput.appendText(ChatColor.stripColor("[" + date + " Error] " + x), true);
            logs.println(ChatColor.stripColor("[" + date + " Error]" + x));
            super.println(ERROR_RED + ChatColor.stripColor("[" + date + " Error]" + x) + RESET_COLOR);
            console.unstashLine();
        }

        @Override
        public void println(Object x) {
            console.stashLine();
            String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
            ConsoleTextOutput.appendText(ChatColor.stripColor("[" + date + " Error] " + x), true);
            logs.println(ChatColor.stripColor("[" + date + " Error]" + x));
            super.println(ERROR_RED + ChatColor.stripColor("[" + date + " Error]" + x) + RESET_COLOR);
            console.unstashLine();
        }

        @Override
        public void println(String string) {
            console.stashLine();
            String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
            ConsoleTextOutput.appendText(ChatColor.stripColor("[" + date + " Error] " + string), true);
            logs.println(ChatColor.stripColor("[" + date + " Error] " + string));
            super.println(ERROR_RED + ChatColor.stripColor("[" + date + " Error] " + string) + RESET_COLOR);
            console.unstashLine();
        }
    }

}
