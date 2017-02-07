package nl.weeaboo.vn.gdx.res;

import java.io.Serializable;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.utils.Disposable;
import com.google.common.testing.GcFinalization;
import com.google.common.testing.GcFinalization.FinalizationPredicate;

import nl.weeaboo.vn.impl.core.StaticRef;

public class GeneratedResourceStoreTest {

    private static final Logger LOG = LoggerFactory.getLogger(GeneratedResourceStoreTest.class);

    private StaticRef<GeneratedResourceStore> testId = StaticRef.from("test", GeneratedResourceStore.class);
    private GeneratedResourceStore store;

    @Before
    public void init() {
        testId.set(store = new GeneratedResourceStore(testId));
    }

    @Test
    public void invalidateGC() {
        createResource(1);
        garbageCollect(0); // Unreferences resources get collected

        IResource<Dummy> alpha = createResource(2);
        LOG.info(String.valueOf(alpha.get()));
        createResource(3);

        garbageCollect(1); // We're still holding a reference to alpha

        alpha = null;
        garbageCollect(0); // alpha can now be garbage collected
    }

    private IResource<Dummy> createResource(int id) {
        IResource<Dummy> resource = store.register(new Dummy(id));
        assertNotDisposed(resource.get());
        return resource;
    }

    private void garbageCollect(int expectedSize) {
        GcFinalization.awaitDone(new FinalizationPredicate() {
            @Override
            public boolean isDone() {
                store.cleanUp();
                return store.size() == expectedSize;
            }
        });
    }

    private static void assertNotDisposed(Dummy dummy) {
        Assert.assertFalse(dummy.disposed);
    }

    private static class Dummy implements Serializable, Disposable {

        private static final long serialVersionUID = 1L;

        public final int id;
        public boolean disposed;

        public Dummy(int id) {
            this.id = id;
        }

        @Override
        public void dispose() {
            disposed = true;
        }

        @Override
        public String toString() {
            return "Dummy(" + id + ")";
        }

    }

}
