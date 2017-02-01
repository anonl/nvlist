package nl.weeaboo.vn.impl.scene;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;

import nl.weeaboo.vn.scene.ILayer;
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

    public static ILayer getParentLayer(IVisualElement elem) {
        while (elem.getParent() != null) {
            elem = elem.getParent();
            if (elem instanceof ILayer) {
                return (ILayer)elem;
            }
        }
        return null;
    }

    public static ImmutableCollection<? extends IVisualElement> getChildren(IVisualElement elem, VisualOrdering order) {
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

        // Retrieve children now, since handleSignal may change the hierarchy otherwise
        ImmutableCollection<? extends IVisualElement> children = getChildren(elem, VisualOrdering.BACK_TO_FRONT);

        elem.handleSignal(signal);
        if (signal.isHandled()) {
            return;
        }

        for (IVisualElement child : children) {
            doSendSignal(child, signal);
            if (signal.isHandled()) {
                return;
            }
        }
    }

}
