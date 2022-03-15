package tk.antoine.roux.logs;

import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Color Console Handler for jdk: using ANSI sequences
 */
public class ColorConsoleHandler extends ConsoleHandler {

    private static final String COLOR_RESET = "\u001b[0m";

    private static final String COLOR_DARK_GREY = "\u001b[90m";
    private static final String COLOR_LIGHT_RED = "\u001b[91m";
    private static final String COLOR_LIGHT_GREEN = "\u001b[92m";
    private static final String COLOR_YELLOW = "\u001b[93m";
    private static final String COLOR_LIGHT_BLUE = "\u001b[94m";
    private static final String COLOR_LIGHT_PURPLE = "\u001b[95m";
    private static final String COLOR_LIGHT_CYAN = "\u001b[96m";
    private static final String COLOR_WHITE = "\u001b[97m";
    private static final String COLOR_BLACK = "\u001b[30m";
    private static final String COLOR_RED = "\u001b[31m";
    private static final String COLOR_GREEN = "\u001b[32m";
    private static final String COLOR_BLUE = "\u001b[34m";
    private static final String COLOR_ORANGE = "\u001b[33m";
    private static final String COLOR_DARK_PURPLE = "\u001b[35m";
    private static final String COLOR_DARK_CYAN = "\u001b[36m";
    private static final String COLOR_LIGHT_GREY = "\u001b[37m";

    String logRecordToString(LogRecord record) {
        Formatter f = getFormatter();
        String msg = f.format(record);

        String prefix;
        Level level = record.getLevel();
        if (level == Level.SEVERE)
            prefix = COLOR_LIGHT_RED;
        else if (level == Level.WARNING)
            prefix = COLOR_YELLOW;
        else if (level == Level.INFO)
            prefix = COLOR_WHITE;
        else if (level == Level.CONFIG)
            prefix = COLOR_LIGHT_BLUE;
        else if (level == Level.FINE)
            prefix = COLOR_DARK_CYAN;
        else if (level == Level.FINER)
            prefix = COLOR_DARK_PURPLE;
        else if (level == Level.FINEST)
            prefix = COLOR_DARK_GREY;
        else
            // Unknown level, probably not possible, but if it happens it means it's bad :-)
            prefix = COLOR_LIGHT_RED;

        return prefix + msg + COLOR_RESET;
    }

    @Override
    public void publish(LogRecord record) {
        System.err.print(logRecordToString(record));
        System.err.flush();
    }
}
