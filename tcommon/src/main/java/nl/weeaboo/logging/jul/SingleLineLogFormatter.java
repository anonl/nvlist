package nl.weeaboo.logging.jul;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/** java.util.logging formatter */
public class SingleLineLogFormatter extends Formatter {

    @Override
    public String format(LogRecord record) {
        long time = record.getMillis();
        Level level = record.getLevel();
        String message = formatMessage(record);
        String clazz = record.getSourceClassName();
        String method = record.getSourceMethodName();
        String stacktrace = getStackTrace(record.getThrown());

        return String.format(Locale.ROOT,
            "%tF %tT.%tL | %s | %s | %s.%s%n%s",
            time, time, time, level, message, clazz, method, stacktrace);
    }

    protected String getStackTrace(Throwable thrown) {
        if (thrown == null) {
            return "";
        }

        StringWriter sw = new StringWriter();
        PrintWriter printWriter = new PrintWriter(sw);
        try {
            thrown.printStackTrace(printWriter);
        } finally {
            printWriter.close();
        }
        return sw.toString();
    }

}
