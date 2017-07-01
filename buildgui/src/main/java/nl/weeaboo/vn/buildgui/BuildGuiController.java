package nl.weeaboo.vn.buildgui;

import java.util.Objects;

import nl.weeaboo.vn.buildgui.task.ITaskController;

public final class BuildGuiController implements IBuildGuiController {

    private final IBuildController buildController;
    private final ITaskController taskController;

    public BuildGuiController(IBuildController buildController, ITaskController taskController) {
        this.buildController = Objects.requireNonNull(buildController);
        this.taskController = Objects.requireNonNull(taskController);
    }

    @Override
    public IBuildController getBuildController() {
        return buildController;
    }

    @Override
    public ITaskController getTaskController() {
        return taskController;
    }

}
