package nl.weeaboo.vn.impl.input;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import nl.weeaboo.common.Checks;
import nl.weeaboo.vn.input.KeyCode;
import nl.weeaboo.vn.render.IRenderEnv;

public final class InputAccumulator {

    private final List<Event> inputEvents = Lists.newArrayList();

    /** Add an event to the internal buffer. */
    public synchronized void addEvent(Event event) {
        inputEvents.add(Checks.checkNotNull(event));
    }

    /** Returns all buffered events, then clears the internal event buffer. */
    public synchronized List<Event> drainEvents() {
        ImmutableList<Event> result = ImmutableList.copyOf(inputEvents);
        inputEvents.clear();
        return result;
    }

    public enum PressState {
        PRESS, RELEASE;
    }

    public static class Event {

        public long timestampMs;

        public Event(long timestampMs) {
            this.timestampMs = timestampMs;
        }

    }

    public static class ButtonEvent extends Event {

        public final KeyCode key;
        public final PressState pressState;

        public ButtonEvent(long timestampMs, KeyCode key, PressState pressState) {
            super(timestampMs);

            this.key = Checks.checkNotNull(key);
            this.pressState = Checks.checkNotNull(pressState);
        }

    }

    public static class PointerPositionEvent extends Event {

        public final double x;
        public final double y;

        /**
         * The pointer position should be passed in virtual coordinates and not in physical screen
         * coordinates, see {@link IRenderEnv#getVirtualSize()}.
         *
         * @param x Pointer X-coordinate in virtual coordinates.
         * @param y Pointer Y-coordinate in virtual coordinates.
         */
        public PointerPositionEvent(long timestampMs, double x, double y) {
            super(timestampMs);

            this.x = x;
            this.y = y;
        }

    }

    public static class PointerScrollEvent extends Event {

        public final int scrollAmount;

        /**
         * @param scrollAmount The number of clicks scrolled by the scroll wheel. Positive values indicate a
         *        downward scroll, negative values an upward scroll.
         */
        public PointerScrollEvent(long timestampMs, int scrollAmount) {
            super(timestampMs);

            this.scrollAmount = scrollAmount;
        }

    }

}
