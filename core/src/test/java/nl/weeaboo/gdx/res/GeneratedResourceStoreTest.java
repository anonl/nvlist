package nl.weeaboo.gdx.res;

import java.io.Serializable;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.utils.Disposable;

import nl.weeaboo.vn.CoreTestUtil;
import nl.weeaboo.vn.core.impl.StaticRef;

public class GeneratedResourceStoreTest {

    private static final Logger LOG = LoggerFactory.getLogger(GeneratedResourceStoreTest.class);

    private StaticRef<GeneratedResourceStore> TEST_ID = StaticRef.from("test", GeneratedResourceStore.class);
    private GeneratedResourceStore store;

    @Before
    public void init() {
        TEST_ID.set(store = new GeneratedResourceStore(TEST_ID));
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
        for (int n = 0; n < 3; n++) {
            System.gc();
            store.cleanUp();
            if (store.size() == expectedSize) {
                break;
            }
            CoreTestUtil.trySleep(100);
        }
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
