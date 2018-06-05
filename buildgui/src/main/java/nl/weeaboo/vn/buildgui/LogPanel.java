package nl.weeaboo.vn.buildgui;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
final class LogPanel extends JPanel implements IBuildLogListener {

    private static final Logger LOG = LoggerFactory.getLogger(LogPanel.class);

    private final JTextPane textPane;
    private final StyleContext styleContext = StyleContext.getDefaultStyleContext();

    public LogPanel() {
        textPane = new JTextPane();
        textPane.setEditable(false);

        DefaultCaret caret = new DefaultCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        textPane.setCaret(caret);

        JScrollPane scrollPane = new JScrollPane(textPane, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
    }

    @Override
    public void onLogLine(String message, Color color) {
        StyledDocument doc = textPane.getStyledDocument();

        AttributeSet as = styleContext.addAttribute(SimpleAttributeSet.EMPTY,
                StyleConstants.Foreground, color);

        try {
            doc.insertString(doc.getLength(), message + "\n", as);
        } catch (BadLocationException e) {
            LOG.error("Invalid text position", e);
        }
    }

}
