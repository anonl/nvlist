package nl.weeaboo.vn.input.impl;

import java.util.EnumMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

import nl.weeaboo.vn.input.INativeInput;
import nl.weeaboo.vn.input.KeyCode;
import nl.weeaboo.vn.input.impl.InputAccumulator.ButtonEvent;
import nl.weeaboo.vn.input.impl.InputAccumulator.Event;
import nl.weeaboo.vn.input.impl.InputAccumulator.PointerPositionEvent;
import nl.weeaboo.vn.input.impl.InputAccumulator.PointerScrollEvent;
import nl.weeaboo.vn.math.Matrix;
import nl.weeaboo.vn.math.Vec2;

public final class NativeInput implements INativeInput {

    private static final Logger LOG = LoggerFactory.getLogger(NativeInput.class);

    private final EnumMap<KeyCode, ButtonState> buttonStates = Maps.newEnumMap(KeyCode.class);
    private final Vec2 pointerPos = new Vec2();
    private int pointerScroll;
    private boolean idle;

    private long timestampMs;

    @Override
    public void clearButtonStates() {
        buttonStates.clear();
    }

    public void update(long timestampMs, InputAccumulator accum) {
        List<Event> events = accum.drainEvents();

        this.timestampMs = timestampMs;
        this.idle = events.isEmpty();

        // Clear outdated state from last update
        pointerScroll = 0;
        for (ButtonState state : buttonStates.values()) {
            state.clearJustPressed();
        }

        // Process new input events
        for (Event raw : events) {
            if (raw instanceof ButtonEvent) {
                ButtonEvent event = (ButtonEvent)raw;
                ButtonState state = getButtonState(event.key, true);

                switch (event.pressState) {
                case PRESS:
                    state.onPressed(event.timestampMs);
                    break;
                case RELEASE:
                    state.onReleased(event.timestampMs);
                    break;
                default:
                    LOG.warn("Unknown press state: {}", event.pressState);
                }

                buttonStates.put(event.key, state); // Store updated state
            } else if (raw instanceof PointerPositionEvent) {
                PointerPositionEvent event = (PointerPositionEvent)raw;

                pointerPos.x = event.x;
                pointerPos.y = event.y;
            } else if (raw instanceof PointerScrollEvent) {
                PointerScrollEvent event = (PointerScrollEvent)raw;

                pointerScroll += event.scrollAmount;
            } else {
                LOG.warn("Unknown event type: {}", raw.getClass());
            }
        }
    }

    @Override
    public boolean consumePress(KeyCode button) {
        ButtonState state = getButtonState(button, false);
        if (state == null) {
            return false;
        }
        return state.consumePress();
    }

    @Override
    public boolean isJustPressed(KeyCode button) {
        ButtonState state = getButtonState(button, false);
        if (state == null) {
            return false;
        }
        return state.isJustPressed();
    }

    @Override
    public boolean isPressed(KeyCode button, boolean allowConsumedPress) {
        ButtonState state = getButtonState(button, false);
        if (state == null) {
            return false;
        }
        return state.isPressed(allowConsumedPress);
    }

    @Override
    public long getPressedTime(KeyCode button, boolean allowConsumedPress) {
        ButtonState state = getButtonState(button, false);
        if (state == null) {
            return 0;
        }
        return state.getPressedTime(timestampMs, allowConsumedPress);
    }

    private ButtonState getButtonState(KeyCode button, boolean createIfNeeded) {
        ButtonState state = buttonStates.get(button);
        if (state == null && createIfNeeded) {
            state = new ButtonState();
            buttonStates.put(button, state);
        }
        return state;
    }

    @Override
    public Vec2 getPointerPos(Matrix transform) {
        Vec2 result = new Vec2(pointerPos);
        transform.transform(result);
        return result;
    }

    @Override
    public int getPointerScroll() {
        return pointerScroll;
    }

    @Override
    public boolean isIdle() {
        return idle;
    }

}
