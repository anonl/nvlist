package nl.weeaboo.vn.core.impl;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import nl.weeaboo.entity.Part;
import nl.weeaboo.vn.core.IInput;
import nl.weeaboo.vn.core.IInputHandlerPart;
import nl.weeaboo.vn.core.IInputListener;

public class InputHandlerPart extends Part implements IInputHandlerPart {

    private static final long serialVersionUID = BaseImpl.serialVersionUID;

    private List<IInputListener> inputHandlers = new CopyOnWriteArrayList<IInputListener>();

    @Override
    public void handleInput(IInput input, boolean mouseContains) {
        for (IInputListener handler : inputHandlers) {
            handler.handleInput(input, mouseContains);
        }
    }

    @Override
    public <I extends IInputListener & Serializable> void addInputListener(I handler) {
        inputHandlers.add(handler);
    }

    @Override
    public void removeInputListener(IInputListener handler) {
        inputHandlers.remove(handler);
    }

}
