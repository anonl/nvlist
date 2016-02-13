package nl.weeaboo.vn.scene.impl;

import java.util.Collections;
import java.util.List;
import java.util.Queue;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.collect.Queues;

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
        sendSignal(source, signal, VisualOrdering.BACK_TO_FRONT);
    }

    public static void sendSignal(IVisualElement source, ISignal signal, Ordering<IVisualElement> order) {
        IVisualElement root = getRoot(source);
        for (IVisualElement elem : collect(root, order)) {
            if (signal.isHandled()) {
                return;
            }
            elem.handleSignal(signal);
        }
    }

    private static Iterable<IVisualElement> collect(IVisualElement root, Ordering<IVisualElement> ordering) {
        List<IVisualElement> result = Lists.newArrayList();

        Queue<IVisualElement> workQ = Queues.newArrayDeque();
        workQ.add(root);
        while (!workQ.isEmpty()) {
            IVisualElement elem = workQ.remove();
            result.add(elem);

            if (elem instanceof IVisualGroup) {
                IVisualGroup group = (IVisualGroup)elem;
                Iterables.addAll(workQ, group.getChildren());
            }
        }
        Collections.sort(result, ordering);
        return result;
    }

}
