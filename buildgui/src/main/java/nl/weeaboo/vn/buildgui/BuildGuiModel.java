package nl.weeaboo.vn.buildgui;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.annotation.Nullable;

import nl.weeaboo.vn.buildtools.project.NvlistProjectConnection;
import nl.weeaboo.vn.buildtools.project.ProjectFolderConfig;

final class BuildGuiModel {

    private final CopyOnWriteArrayList<IProjectModelListener> projectListeners = new CopyOnWriteArrayList<>();

    private @Nullable NvlistProjectConnection project;

    public void addProjectListener(IProjectModelListener listener) {
        projectListeners.add(Objects.requireNonNull(listener));
    }

    public void removeProjectListener(IProjectModelListener listener) {
        projectListeners.remove(Objects.requireNonNull(listener));
    }

    public Optional<NvlistProjectConnection> getProject() {
        return Optional.ofNullable(project);
    }

    public void setProjectFolders(ProjectFolderConfig folderConfig) {
        if (project != null) {
            project.close();
        }

        project = NvlistProjectConnection.openProject(folderConfig);

        projectListeners.forEach(ls -> ls.onProjectChanged(project));
    }

}
