package nl.weeaboo.vn.scene.signal;

import nl.weeaboo.common.Checks;
import nl.weeaboo.vn.scene.IVisualElement;

public final class DestroySignal extends AbstractSignal {

    private final IVisualElement destroyedElement;

    public DestroySignal(IVisualElement destroyedElement) {
        this.destroyedElement = Checks.checkNotNull(destroyedElement);
    }

    public IVisualElement getDestroyedElement() {
        return destroyedElement;
    }

}
