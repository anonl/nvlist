package nl.weeaboo.vn.impl.core;

import static com.google.common.collect.testing.testers.ListListIteratorTester.getListIteratorFullyModifiableMethod;
import static com.google.common.collect.testing.testers.ListSubListTester.getSubListOriginalListSetAffectsSubListLargeListMethod;
import static com.google.common.collect.testing.testers.ListSubListTester.getSubListOriginalListSetAffectsSubListMethod;
import static com.google.common.collect.testing.testers.ListSubListTester.getSubListSubListRemoveAffectsOriginalLargeListMethod;

import java.io.Serializable;
import java.util.List;

import org.junit.Test;

import com.google.common.collect.testing.ListTestSuiteBuilder;
import com.google.common.collect.testing.SampleElements;
import com.google.common.collect.testing.TestListGenerator;
import com.google.common.collect.testing.features.CollectionFeature;
import com.google.common.collect.testing.features.CollectionSize;
import com.google.common.collect.testing.features.ListFeature;
import com.google.common.collect.testing.testers.ListSubListTester;

import junit.framework.TestSuite;
import nl.weeaboo.vn.core.IDestructible;

public final class DestructibleElemListTest {

    @Test
    public static TestSuite suite() throws NoSuchMethodException, SecurityException {
        return ListTestSuiteBuilder.using(new TestGenerator())
                .named("DestructibleElemList")
                .withFeatures(
                        ListFeature.SUPPORTS_SET,
                        ListFeature.SUPPORTS_ADD_WITH_INDEX,
                        ListFeature.SUPPORTS_REMOVE_WITH_INDEX,
                        CollectionFeature.SERIALIZABLE,
                        // CollectionFeature.FAILS_FAST_ON_CONCURRENT_MODIFICATION,
                        CollectionSize.ANY)
                .suppressing(
                        // Skip various tests that assume iterator() returns a mutable view of the list
                        getSubListOriginalListSetAffectsSubListMethod(),
                        getSubListOriginalListSetAffectsSubListLargeListMethod(),
                        getSubListSubListRemoveAffectsOriginalLargeListMethod(),
                        getListIteratorFullyModifiableMethod(),
                        ListSubListTester.class.getMethod("testSubList_subListClearAffectsOriginal"))
                .createTestSuite();
    }

    private static final class SampleDestructibles extends SampleElements<Dummy> {

        public SampleDestructibles() {
            super(new Dummy(1), new Dummy(2), new Dummy(3), new Dummy(4), new Dummy(5));
        }

    }

    private static final class TestGenerator implements TestListGenerator<Dummy> {

        @Override
        public SampleElements<Dummy> samples() {
            return new SampleDestructibles();
        }

        @Override
        public Dummy[] createArray(int length) {
            return new Dummy[length];
        }

        @Override
        public Iterable<Dummy> order(List<Dummy> insertionOrder) {
            return insertionOrder;
        }

        @Override
        public DestructibleElemList<Dummy> create(Object... elements) {
            DestructibleElemList<Dummy> list = new DestructibleElemList<>();
            for (Object elem : elements) {
                list.add((Dummy)elem);
            }
            return list;
        }
    }

    private static final class Dummy implements IDestructible, Serializable {

        private static final long serialVersionUID = 1L;

        private final int id;
        private boolean destroyed;

        private Dummy(int id) {
            this.id = id;
        }

        @Override
        public void destroy() {
            destroyed = true;
        }

        @Override
        public boolean isDestroyed() {
            return destroyed;
        }

        @Override
        public int hashCode() {
            return id;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Dummy)) {
                return false;
            }
            Dummy dummy = (Dummy)obj;
            return id == dummy.id;
        }

        @Override
        public String toString() {
            return "Dummy [id=" + id + "]";
        }

    }
}
