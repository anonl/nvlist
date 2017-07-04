package nl.weeaboo.vn.buildgui;

import nl.weeaboo.vn.buildtools.project.NvlistProjectConnection;

public interface IProjectModelListener {

    void onProjectChanged(NvlistProjectConnection projectModel);

}
