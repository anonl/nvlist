package nl.weeaboo.vn.buildgui;

import nl.weeaboo.vn.buildtools.project.NvlistProjectConnection;

public interface IProjectModelListener {

    /**
     * This method is called when the active project changes.
     */
    void onProjectChanged(NvlistProjectConnection projectModel);

}
