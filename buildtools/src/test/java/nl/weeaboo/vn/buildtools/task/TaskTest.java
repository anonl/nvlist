package nl.weeaboo.vn.buildtools.task;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

public final class TaskTest {

    private final Task task = new Task();

    @Test
    public void testProgressListeners() {
        ProgressListenerMock ls = new ProgressListenerMock();

        task.fireProgress("a");
        task.addProgressListener(ls);

        task.fireProgress("b");
        task.fireProgress("c");
        Assert.assertEquals(ImmutableList.of("b", "c"), ls.consumeProgress());
        Assert.assertNull(ls.getResult());

        task.fireFinished(TaskResultType.FAILED, "finished");
        Assert.assertEquals(ImmutableList.of("finished"), ls.consumeProgress());
        Assert.assertEquals(TaskResultType.FAILED, ls.getResult());

        task.removeProgressListener(ls);
        task.fireProgress("d");
        Assert.assertEquals(ImmutableList.of(), ls.consumeProgress());
    }

    /**
     * The default implementation of {@link Task#cancel()} is a no-op.
     */
    @Test
    public void testCancel() {
        ProgressListenerMock ls = new ProgressListenerMock();
        task.addProgressListener(ls);
        task.cancel();

        Assert.assertNull(ls.getResult());
    }

    /**
     * If you add a listener to an already finished task, the listener receives a finish event immediately.
     * This is to prevent race conditions when adding listeners to fast-completing tasks.
     */
    @Test
    public void testRetroactiveFinished() {
        task.fireFinished(TaskResultType.SUCCESS, "finished");

        ProgressListenerMock ls = new ProgressListenerMock();
        task.addProgressListener(ls);
        Assert.assertEquals(ImmutableList.of("finished"), ls.consumeProgress());
        Assert.assertEquals(TaskResultType.SUCCESS, ls.getResult());
    }

}
