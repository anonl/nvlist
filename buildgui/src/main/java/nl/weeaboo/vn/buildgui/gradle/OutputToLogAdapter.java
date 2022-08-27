package nl.weeaboo.vn.buildgui.gradle;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import nl.weeaboo.vn.buildgui.IBuildLogListener;
import nl.weeaboo.vn.buildgui.Styles;
import nl.weeaboo.vn.buildgui.Theme;

final class OutputToLogAdapter extends OutputStream {

    private final IBuildLogListener lineHandler;

    private final ByteArrayOutputStream lineBuffer = new ByteArrayOutputStream();

    public OutputToLogAdapter(IBuildLogListener lineHandler) {
        this.lineHandler = lineHandler;
    }

    @Override
    public void write(int b) throws IOException {
        lineBuffer.write(b);

        if (b == '\n') {
            String line = consumeLine();
            lineHandler.onLogLine(line, getDefaultColor(line));
        }
    }

    private Color getDefaultColor(String line) {
        Theme.LogStyles logStyles = Styles.theme.logStyles;
        if (line.contains("| DEBUG |")) {
            return logStyles.debugColor;
        } else if (line.contains("| WARN  |")) {
            return logStyles.warningColor;
        } else if (line.contains("| ERROR |")) {
            return logStyles.errorColor;
        } else {
            return logStyles.infoColor;
        }
    }

    private String consumeLine() throws UnsupportedEncodingException {
        String line = lineBuffer.toString("UTF-8");
        lineBuffer.reset();

        // Strip trailing \r\n, \n
        line = line.replaceAll("\\r?\\n$", "");
        return line;
    }

}
