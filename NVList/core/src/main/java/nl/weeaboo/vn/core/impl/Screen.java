package nl.weeaboo.vn.core.impl;

import java.io.IOException;
import java.io.ObjectInputStream;

import nl.weeaboo.common.Checks;
import nl.weeaboo.common.Rect2D;
import nl.weeaboo.entity.DefaultEntityStreamDef;
import nl.weeaboo.entity.Entity;
import nl.weeaboo.entity.EntityStream;
import nl.weeaboo.entity.ISignal;
import nl.weeaboo.entity.Scene;
import nl.weeaboo.io.CustomSerializable;
import nl.weeaboo.vn.core.ILayer;
import nl.weeaboo.vn.core.IRenderEnv;
import nl.weeaboo.vn.core.IScreen;
import nl.weeaboo.vn.core.TickSignal;
import nl.weeaboo.vn.render.IDrawBuffer;

@CustomSerializable
public class Screen implements IScreen, ILayerHolder {

	private static final long serialVersionUID = CoreImpl.serialVersionUID;

	protected final Scene scene;
	private final Rect2D bounds;
	private final BasicPartRegistry partRegistry;

	private ILayer rootLayer; // Lazily (re-)initialized when null or destroyed
	private ILayer activeLayer; // Could potentially point to a destroyed layer (minor memory leak)
	private IRenderEnv renderEnv;

	private transient EntityStream signalStream;

	public Screen(Scene s, Rect2D bounds, BasicPartRegistry pr, IRenderEnv env) {
		this.scene = Checks.checkNotNull(s);
		this.bounds = Checks.checkNotNull(bounds);
		this.partRegistry = Checks.checkNotNull(pr);
		this.renderEnv = Checks.checkNotNull(env);

		initTransients();
	}

	//Functions
    private void initTransients() {
        signalStream = scene.joinStream(DefaultEntityStreamDef.ALL_ENTITIES_STREAM);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        initTransients();
    }

    @Override
    public Entity createEntity() {
        return scene.createEntity();
    }

    @Override
    public void update() {
        sendSignal(new TickSignal());
    }

    protected void sendSignal(ISignal signal) {
        signalStream.sendSignal(signal);
    }

	public void draw(IDrawBuffer buffer) {
        Layer layer = (Layer)getRootLayer();
		layer.draw(buffer);
	}

	@Override
	public ILayer createLayer(ILayer parentLayer) {
		if (!containsLayer(parentLayer)) {
			throw new IllegalArgumentException("Parent layer (" + parentLayer + ") isn't attached to this screen");
		}
		return doCreateLayer(parentLayer);
	}

	protected ILayer createRootLayer() {
		return doCreateLayer(null);
	}

	private ILayer doCreateLayer(ILayer parentLayer) {
		ILayer layer = newLayer(parentLayer);
		if (parentLayer != null) {
			layer.setBounds(parentLayer.getX(), parentLayer.getY(), parentLayer.getWidth(), parentLayer.getHeight());
			layer.setRenderEnv(parentLayer.getRenderEnv());
		} else {
			layer.setBounds(bounds.x, bounds.y, bounds.w, bounds.h);
			layer.setRenderEnv(renderEnv);
		}
		return layer;
	}

	/**
	 * Creates a new layer.
	 * @param parentLayer If not {@code null}, creates the new layer as a sub-layer of {@code parentLayer}.
	 */
	protected ILayer newLayer(ILayer parentLayer) {
		if (parentLayer == null) {
			return new Layer(this, scene, partRegistry);
		}
		return ((Layer)parentLayer).createSubLayer();
	}

	@Override
	public void onSubLayerDestroyed(ILayer layer) {
		if (layer == rootLayer) {
			rootLayer = null;
		} else {
			throw new IllegalStateException("Received a destroyed event from a non-root layer: " + layer);
		}
	}

	//Getters
	@Override
	public ILayer getRootLayer() {
		if (rootLayer == null) {
			rootLayer = createRootLayer();
		}
		return rootLayer;
	}

	@Override
	public ILayer getActiveLayer() {
		if (activeLayer == null || activeLayer.isDestroyed()) {
			activeLayer = getRootLayer();
		}
		return activeLayer;
	}

	protected boolean containsLayer(ILayer layer) {
		return rootLayer != null && (rootLayer == layer || rootLayer.containsLayer(layer));
	}

    @Override
    public IRenderEnv getRenderEnv() {
        return renderEnv;
    }

	//Setters
	@Override
    public void setRenderEnv(IRenderEnv env) {
		renderEnv = env;

		getRootLayer().setRenderEnv(env);
	}

}
