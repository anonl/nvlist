package nl.weeaboo.vn.core;

import java.io.Serializable;

import nl.weeaboo.vn.render.IRenderEnvConsumer;
import nl.weeaboo.vn.signal.ISignalHandler;

/**
 * Sub-module of the engine.
 */
public interface IModule extends Serializable, IDestructible, IUpdateable, IPrefsChangeListener, IRenderEnvConsumer,
        IResourceCache, ISignalHandler {

}
