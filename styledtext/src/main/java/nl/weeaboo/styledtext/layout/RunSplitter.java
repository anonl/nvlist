package nl.weeaboo.styledtext.layout;

import java.text.Bidi;
import java.text.BreakIterator;
import java.util.Locale;

import nl.weeaboo.styledtext.StyledText;
import nl.weeaboo.styledtext.TextStyle;

public class RunSplitter {

    private final BreakIterator lineBreaker;
    private final boolean isBaseRightToLeft;
    private final RunHandler runHandler;

    private StyledText stext;
    private Bidi bidi;

	private int index;
    private int nextBreakBoundary;

    private final RunState currentRun = new RunState();
    private final RunState nextRun = new RunState();

    private RunSplitter(BreakIterator lineBreaker, boolean isRightToLeft, RunHandler runHandler) {
        this.lineBreaker = lineBreaker;
        this.isBaseRightToLeft = isRightToLeft;
        this.runHandler = runHandler;
	}

    public static void run(StyledText stext, boolean isRightToLeft, RunHandler runHandler) {
        BreakIterator breakIterator = BreakIterator.getLineInstance(Locale.ROOT);
        run(stext, isRightToLeft, breakIterator, runHandler);
    }

    public static void run(StyledText stext, boolean isRightToLeft, BreakIterator wordIterator,
            RunHandler runHandler) {

        RunSplitter ss = new RunSplitter(wordIterator, isRightToLeft, runHandler);
        ss.setText(stext);
		while (!ss.isDone()) {
            ss.processCodepoint();
		}
	}

    private void setText(StyledText str) {
        stext = str;
        index = 0;
        nextBreakBoundary = 0;

        lineBreaker.setText(str.getCharacterIterator());

        if (str.isBidi() || isBaseRightToLeft) {
            bidi = str.getBidi(isBaseRightToLeft);
        } else {
            bidi = null;
        }

        currentRun.reset();
    }

    private int readCodepoint() {
        char c0 = stext.charAt(index++);
        if (Character.isHighSurrogate(c0) && !isDone()) {
            char c1 = stext.charAt(index++);
            return Character.toCodePoint(c0, c1);
        } else {
            return c0;
        }
    }

    private void startRun(RunState rs) {
        final int i = index;
        final int c = readCodepoint(); // Increments 'index'

        rs.startIndex = i;
        rs.endIndex = index;
        rs.style = stext.getStyle(i);
        rs.bidiLevel = (bidi != null ? bidi.getLevelAt(i) : 0);
        rs.isWhitespace = Character.isWhitespace(c) && !LayoutUtil.isNonBreakingSpace(c);
        rs.containsLineBreak = (c == '\n');
    }

    private boolean isBoundary(RunState cur, RunState next) {
        return cur.containsLineBreak
            || next.containsLineBreak
            || (nextBreakBoundary >= next.startIndex && nextBreakBoundary < next.endIndex)
            || (cur.style != next.style)
            || (cur.bidiLevel != next.bidiLevel)
            || (cur.isWhitespace != next.isWhitespace)
            || (cur.containsLineBreak != next.containsLineBreak);
    }

    public void processCodepoint() {
        startRun(nextRun);

        if (isBoundary(currentRun, nextRun)) {
            if (currentRun.shouldProcess()) {
                processRun(currentRun);
			}
            currentRun.set(nextRun);
        } else {
            currentRun.append(nextRun);
		}

        // Go look for the next line break boundary if needed
        while (nextBreakBoundary != BreakIterator.DONE && index > nextBreakBoundary) {
            nextBreakBoundary = lineBreaker.next();
        }

		//Process the final pending run if otherwise finished
        if (isDone() && currentRun.shouldProcess()) {
            processRun(currentRun);
            currentRun.reset();
		}
	}

    private void processRun(RunState rs) {
        runHandler.processRun(stext.substring(rs.startIndex, rs.endIndex), rs);
    }

    public boolean isDone() {
        return index >= stext.length();
    }

    public static class RunState {

        public int startIndex;
        public int endIndex;
        public TextStyle style;
        public int bidiLevel;
        public boolean containsLineBreak;
        public boolean isWhitespace;

        void reset() {
            startIndex = 0;
            endIndex = 0;
            style = null;
            bidiLevel = 0;
            containsLineBreak = false;
            isWhitespace = true;
        }

        public boolean shouldProcess() {
            return endIndex > startIndex || containsLineBreak;
        }

        void set(RunState rs) {
            startIndex = rs.startIndex;
            endIndex = rs.endIndex;
            style = rs.style;
            bidiLevel = rs.bidiLevel;
            containsLineBreak = rs.containsLineBreak;
            isWhitespace = rs.isWhitespace;
        }

        void append(RunState rs) {
            endIndex = rs.endIndex;
            if (rs.containsLineBreak) {
                containsLineBreak = true;
            }
            if (!rs.isWhitespace) {
                isWhitespace = false;
            }
        }

    }

    public interface RunHandler {

        void processRun(CharSequence text, RunState rs);

    }

}