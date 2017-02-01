package nl.weeaboo.vn.impl.render;

import java.util.Collection;

import com.google.common.collect.Lists;

import nl.weeaboo.common.Checks;
import nl.weeaboo.vn.render.IOffscreenRenderTask;
import nl.weeaboo.vn.render.IOffscreenRenderTaskBuffer;

public final class OffscreenRenderTaskBuffer implements IOffscreenRenderTaskBuffer {

    private static final long serialVersionUID = RenderImpl.serialVersionUID;

    private final Collection<IOffscreenRenderTask> tasks = Lists.newArrayList();

    @Override
    public void update() {
        // In the future, we may allow tasks to span multiple frames. For now, everything is blocking.
        for (IOffscreenRenderTask task : tasks) {
            task.render();
        }
        tasks.clear();
    }

    @Override
    public void add(IOffscreenRenderTask task) {
        tasks.add(Checks.checkNotNull(task));
    }

    @Override
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

}
