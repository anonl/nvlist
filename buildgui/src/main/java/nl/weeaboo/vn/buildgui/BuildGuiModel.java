package nl.weeaboo.vn.buildgui;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.annotation.Nullable;

import nl.weeaboo.vn.buildtools.project.ProjectFolderConfig;
import nl.weeaboo.vn.buildtools.project.ProjectModel;

final class BuildGuiModel {

    private final CopyOnWriteArrayList<IProjectModelListener> projectListeners = new CopyOnWriteArrayList<>();

    private @Nullable ProjectModel project;

    public void addProjectListener(IProjectModelListener listener) {
        projectListeners.add(Objects.requireNonNull(listener));
    }

    public void removeProjectListener(IProjectModelListener listener) {
        projectListeners.remove(Objects.requireNonNull(listener));
    }

    public Optional<ProjectModel> getProject() {
        return Optional.ofNullable(project);
    }

    public void setProjectFolders(ProjectFolderConfig folderConfig) {
        project = ProjectModel.createProjectModel(folderConfig);

        projectListeners.forEach(ls -> ls.onProjectModelChanged(project));
    }

}
