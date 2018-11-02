package nl.weeaboo.vn.impl.core;

import static com.google.common.collect.testing.testers.ListListIteratorTester.getListIteratorFullyModifiableMethod;
import static com.google.common.collect.testing.testers.ListSubListTester.getSubListOriginalListSetAffectsSubListLargeListMethod;
import static com.google.common.collect.testing.testers.ListSubListTester.getSubListOriginalListSetAffectsSubListMethod;
import static com.google.common.collect.testing.testers.ListSubListTester.getSubListSubListRemoveAffectsOriginalLargeListMethod;

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

public final class DestructibleElemListCollectionTest {

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

    private static final class SampleDestructibles extends SampleElements<MockDestructible> {

        public SampleDestructibles() {
            super(new MockDestructible(1), new MockDestructible(2), new MockDestructible(3), new MockDestructible(4),
                    new MockDestructible(5));
        }

    }

    private static final class TestGenerator implements TestListGenerator<MockDestructible> {

        @Override
        public SampleElements<MockDestructible> samples() {
            return new SampleDestructibles();
        }

        @Override
        public MockDestructible[] createArray(int length) {
            return new MockDestructible[length];
        }

        @Override
        public Iterable<MockDestructible> order(List<MockDestructible> insertionOrder) {
            return insertionOrder;
        }

        @Override
        public DestructibleElemList<MockDestructible> create(Object... elements) {
            DestructibleElemList<MockDestructible> list = new DestructibleElemList<>();
            for (Object elem : elements) {
                list.add((MockDestructible)elem);
            }
            return list;
        }
    }
}
