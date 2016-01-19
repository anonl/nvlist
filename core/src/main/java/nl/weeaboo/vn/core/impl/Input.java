package nl.weeaboo.vn.core.impl;

import java.util.EnumMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

import nl.weeaboo.vn.core.IInput;
import nl.weeaboo.vn.core.KeyCode;
import nl.weeaboo.vn.core.impl.InputAccumulator.ButtonEvent;
import nl.weeaboo.vn.core.impl.InputAccumulator.Event;
import nl.weeaboo.vn.core.impl.InputAccumulator.MousePositionEvent;
import nl.weeaboo.vn.math.Vec2;

public final class Input implements IInput {

    private static final Logger LOG = LoggerFactory.getLogger(Input.class);

    private final EnumMap<KeyCode, ButtonState> buttonStates = Maps.newEnumMap(KeyCode.class);
    private final Vec2 pointerPos = new Vec2();
    private int pointerScroll; // TODO: Support mouse scroll events
    private boolean idle;

    private long timestampMs;

    public void update(long timestampMs, InputAccumulator accum) {
        List<Event> events = accum.drainEvents();
        this.timestampMs = timestampMs;
        this.idle = events.isEmpty();

        for (Event raw : events) {
            if (raw instanceof ButtonEvent) {
                ButtonEvent event = (ButtonEvent)raw;
                ButtonState state = getButtonState(event.key);

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
            } else if (raw instanceof MousePositionEvent) {
                MousePositionEvent event = (MousePositionEvent)raw;

                pointerPos.x = event.x;
                pointerPos.y = event.y;
            } else {
                LOG.warn("Unknown event type: {}", raw.getClass());
            }
        }
    }

    @Override
    public boolean consumePress(KeyCode button) {
        ButtonState state = getButtonState(button);
        if (state == null) {
            return false;
        }
        return state.consumePress();
    }

    @Override
    public boolean isJustPressed(KeyCode button) {
        ButtonState state = getButtonState(button);
        if (state == null) {
            return false;
        }
        return state.isJustPressed();
    }

    @Override
    public boolean isPressed(KeyCode button, boolean allowConsumedPress) {
        ButtonState state = getButtonState(button);
        if (state == null) {
            return false;
        }
        return state.isPressed(allowConsumedPress);
    }

    @Override
    public long getPressedTime(KeyCode button, boolean allowConsumedPress) {
        ButtonState state = getButtonState(button);
        if (state == null) {
            return 0;
        }
        return state.getPressedTime(timestampMs, allowConsumedPress);
    }

    private ButtonState getButtonState(KeyCode button) {
        ButtonState state = buttonStates.get(button);
        if (state == null) {
            state = new ButtonState();
            buttonStates.put(button, state);
        }
        return state;
    }

    @Override
    public double getPointerX() {
        return pointerPos.x;
    }

    @Override
    public double getPointerY() {
        return pointerPos.y;
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
