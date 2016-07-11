package nl.weeaboo.vn.scene.impl;

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

    public static void sendSignal(IVisualElement source, ISignal signal) {
        sendSignal(source, signal, VisualOrdering.BACK_TO_FRONT);
    }
    public static void sendSignal(IVisualElement source, ISignal signal, VisualOrdering order) {
        doSendSignal(getRoot(source), signal, order);
    }

    public static void doSendSignal(IVisualElement elem, ISignal signal, VisualOrdering order) {
        if (signal.isHandled()) {
            return;
        }

        if (order.isBackToFront()) {
            elem.handleSignal(signal);
            if (signal.isHandled()) {
                return;
            }
        }

        // Traverse children
        if (elem instanceof IVisualGroup) {
            IVisualGroup group = (IVisualGroup)elem;
            for (IVisualElement child : order.immutableSortedCopy(group.getChildren())) {
                doSendSignal(child, signal, order);
                if (signal.isHandled()) {
                    return;
                }
            }
        }

        if (!order.isBackToFront()) {
            elem.handleSignal(signal);
            if (signal.isHandled()) {
                return;
            }
        }
    }

}
