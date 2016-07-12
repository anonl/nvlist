package nl.weeaboo.vn.scene.impl;

import com.google.common.collect.ImmutableSet;

import nl.weeaboo.vn.scene.IVisualElement;
import nl.weeaboo.vn.scene.IVisualGroup;
import nl.weeaboo.vn.signal.ISignal;

public final class SceneUtil {

    private SceneUtil() {
    }

    public static IVisualElement getRoot(IVisualElement elem) {
        while (elem.getParent() != null) {
            elem = elem.getParent();
        }
        return elem;
    }

    public static Iterable<? extends IVisualElement> getChildren(IVisualElement elem, VisualOrdering order) {
        if (elem instanceof IVisualGroup) {
            IVisualGroup group = (IVisualGroup)elem;
            return order.immutableSortedCopy(group.getChildren());
        }
        return ImmutableSet.of();
    }

    public static void sendSignal(IVisualElement source, ISignal signal) {
        doSendSignal(getRoot(source), signal);
    }
    private static void doSendSignal(IVisualElement elem, ISignal signal) {
        if (signal.isHandled()) {
            return;
        }

        elem.handleSignal(signal);
        if (signal.isHandled()) {
            return;
        }

        for (IVisualElement child : getChildren(elem, VisualOrdering.BACK_TO_FRONT)) {
            doSendSignal(child, signal);
            if (signal.isHandled()) {
                return;
            }
        }
    }

}
