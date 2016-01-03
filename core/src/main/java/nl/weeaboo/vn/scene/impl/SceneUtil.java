package nl.weeaboo.vn.scene.impl;

import com.google.common.collect.ImmutableList;

import nl.weeaboo.vn.scene.IVisualElement;
import nl.weeaboo.vn.scene.IVisualGroup;
import nl.weeaboo.vn.scene.signal.ISignal;

public final class SceneUtil {

    private SceneUtil() {
    }

    public static IVisualElement getRoot(IVisualElement elem) {
        while (elem.getParent() != null) {
            elem = elem.getParent();
        }
        return elem;
    }

    public static void sendSignal(IVisualElement source, ISignal signal) {
        doSendSignal(getRoot(source), signal);
    }

    private static void doSendSignal(IVisualElement current, ISignal signal) {
        if (signal.isHandled()) {
            return;
        }

        current.handleSignal(signal);

        if (current instanceof IVisualGroup) {
            IVisualGroup group = (IVisualGroup)current;
            for (IVisualElement child : ImmutableList.copyOf(group.getChildren())) {
                doSendSignal(child, signal);
            }
        }
    }

}
