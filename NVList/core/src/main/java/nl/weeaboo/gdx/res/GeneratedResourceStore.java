package nl.weeaboo.gdx.res;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import nl.weeaboo.common.Checks;

/**
 * Handles dispose behavior for generated resources. Resources are registered through
 * {@link #register(Disposable)}, which returns an indirect reference to the resource. Once this reference
 * object is garbage collected, the referenced resource will be disposed.
 */
public class GeneratedResourceStore extends AbstractResourceStore {

    private static final Logger LOG = LoggerFactory.getLogger(GeneratedResourceStore.class);

    private final ReferenceQueue<GeneratedResource<?>> garbage;
    private final Array<ResourceRef<?>> resources = new Array<ResourceRef<?>>(false, 8);

    public GeneratedResourceStore() {
        super(LoggerFactory.getLogger("GeneratedResourceStore"));

        garbage = new ReferenceQueue<GeneratedResource<?>>();
    }

    public <T extends Disposable> IResource<T> register(T value) {
        return register(value, value);
    }
    
    public <T> IResource<T> register(T value, Disposable disposeFunction) {
        cleanUp();

        GeneratedResource<T> resource = new GeneratedResource<T>(value);
        resources.add(new ResourceRef<T>(resource, disposeFunction, garbage));
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

    private static class ResourceRef<T> extends WeakReference<GeneratedResource<T>> {

        private final Disposable disposeFunction;

        public ResourceRef(GeneratedResource<T> referent, Disposable disposeFunction,
                ReferenceQueue<? super GeneratedResource<T>> q) {

            super(referent, q);

            this.disposeFunction = Checks.checkNotNull(disposeFunction);
        }

        public void dispose() {
            disposeFunction.dispose();
        }

    }

}
