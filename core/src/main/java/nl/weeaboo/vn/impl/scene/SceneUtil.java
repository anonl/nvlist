package nl.weeaboo.vn.impl.scene;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;

import nl.weeaboo.vn.scene.ILayer;
import nl.weeaboo.vn.scene.IVisualElement;
import nl.weeaboo.vn.scene.IVisualGroup;
import nl.weeaboo.vn.signal.ISignal;

public final class SceneUtil {

    private SceneUtil() {
    }

    /**
     * Finds the root (most distant ancestor) of a visual element.
     */
    public static IVisualElement getRoot(IVisualElement elem) {
        while (elem.getParent() != null) {
            elem = elem.getParent();
        }
        return elem;
    }

    /**
     * Finds the parent layer of a visual element.
     *
     * @return The parent layer, or {@code null} if no parent layer was found.
     */
    public static @Nullable ILayer getParentLayer(IVisualElement elem) {
        while (elem.getParent() != null) {
            elem = elem.getParent();
            if (elem instanceof ILayer) {
                return (ILayer)elem;
            }
        }
        return null;
    }

    /**
     * @return An immutable sorted snapshot of the direct descendants of the given element.
     */
    public static ImmutableCollection<? extends IVisualElement> getChildren(IVisualElement elem, VisualOrdering order) {
        if (elem instanceof IVisualGroup) {
            IVisualGroup group = (IVisualGroup)elem;
            return order.immutableSortedCopy(group.getChildren());
        }
        return ImmutableSet.of();
    }

    /**
     * Sends a signal to a visual element and its descendants.
     */
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
