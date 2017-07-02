package nl.weeaboo.vn.buildgui;

import java.util.Objects;

import nl.weeaboo.vn.buildgui.task.ITaskController;

public final class BuildGuiController implements IBuildGuiController {

    private final IBuildController buildController;
    private final ITaskController taskController;
    private final BuildGuiModel model = new BuildGuiModel();

    public BuildGuiController(IBuildController buildController, ITaskController taskController) {
        this.buildController = Objects.requireNonNull(buildController);
        this.taskController = Objects.requireNonNull(taskController);

        model.addProjectListener(buildController);
    }

    @Override
    public IBuildController getBuildController() {
        return buildController;
    }

    @Override
    public ITaskController getTaskController() {
        return taskController;
    }

    @Override
    public BuildGuiModel getModel() {
        return model;
    }

}
