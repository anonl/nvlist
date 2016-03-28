package nl.weeaboo.gdx.res;

import java.io.Serializable;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import nl.weeaboo.common.Checks;
import nl.weeaboo.vn.core.impl.StaticRef;

/**
 * Handles dispose behavior for generated resources. Resources are registered through register(), which
 * returns an indirect reference to the resource. Once this reference object is garbage collected, the
 * referenced resource will be disposed.
 */
public class GeneratedResourceStore extends AbstractResourceStore {

    private static final Logger LOG = LoggerFactory.getLogger(GeneratedResourceStore.class);

    private final StaticRef<? extends GeneratedResourceStore> selfId;
    private final ReferenceQueue<GeneratedResource<?>> garbage;
    private final Array<ResourceRef<?>> resources = new Array<ResourceRef<?>>(false, 8);

    public GeneratedResourceStore(StaticRef<? extends GeneratedResourceStore> selfId) {
        super(LoggerFactory.getLogger("GeneratedResourceStore"));

        this.selfId = Checks.checkNotNull(selfId);

        garbage = new ReferenceQueue<GeneratedResource<?>>();
    }

    public <T extends Serializable & Disposable> IResource<T> register(T value) {
        cleanUp();

        GeneratedResource<T> resource = new GeneratedResource<T>(selfId, value);
        resources.add(new ResourceRef<T>(resource, value, garbage));
        return resource;
    }

    public int size() {
        return resources.size;
    }

    @Override
    public void clear() {
        resources.clear();

        cleanUp();
    }

    public void cleanUp() {
        // Remove dead references
        Iterator<ResourceRef<?>> itr = resources.iterator();
        while (itr.hasNext()) {
            ResourceRef<?> ref = itr.next();
            if (ref.get() == null) {
                LOG.trace("Removing dead resource reference: " + ref);
                itr.remove();
            }
        }

        // Clean garbage
        Reference<? extends GeneratedResource<?>> rawReference;
        while ((rawReference = garbage.poll()) != null) {
            ResourceRef<?> ref = (ResourceRef<?>) rawReference;
            ref.dispose();
        }
    }

    private static class ResourceRef<T> extends WeakReference<GeneratedResource<? extends T>> {

        private final Disposable disposeFunction;

        public ResourceRef(GeneratedResource<? extends T> referent, Disposable disposeFunction,
                ReferenceQueue<? super GeneratedResource<? extends T>> q) {

            super(referent, q);

            this.disposeFunction = Checks.checkNotNull(disposeFunction);
        }

        public void dispose() {
            disposeFunction.dispose();
        }

    }

}
