package nl.weeaboo.vn.scene.impl;

import nl.weeaboo.common.Checks;
import nl.weeaboo.common.Rect2D;
import nl.weeaboo.io.CustomSerializable;
import nl.weeaboo.vn.core.IRenderEnv;
import nl.weeaboo.vn.render.IDrawBuffer;
import nl.weeaboo.vn.scene.ILayer;
import nl.weeaboo.vn.scene.IScreen;
import nl.weeaboo.vn.scene.signal.ISignal;
import nl.weeaboo.vn.scene.signal.RenderEnvChangeSignal;
import nl.weeaboo.vn.scene.signal.TickSignal;

@CustomSerializable
public class Screen implements IScreen {

    private static final long serialVersionUID = SceneImpl.serialVersionUID;

	private final Rect2D bounds;

	private ILayer rootLayer; // Lazily (re-)initialized when null or destroyed
	private ILayer activeLayer; // Could potentially point to a destroyed layer (minor memory leak)
	private IRenderEnv renderEnv;

    public Screen(Rect2D bounds, IRenderEnv env) {
		this.bounds = Checks.checkNotNull(bounds);
		this.renderEnv = Checks.checkNotNull(env);
	}

    @Override
    public void update() {
        sendSignal(new TickSignal());
    }

    protected void sendSignal(ISignal signal) {
        SceneUtil.sendSignal(getRootLayer(), signal);
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
		} else {
			layer.setBounds(bounds.x, bounds.y, bounds.w, bounds.h);
		}
		return layer;
	}

	/**
	 * Creates a new layer.
	 * @param parentLayer If not {@code null}, creates the new layer as a sub-layer of {@code parentLayer}.
	 */
	protected ILayer newLayer(ILayer parentLayer) {
		if (parentLayer == null) {
            return new Layer(null);
		}
		return ((Layer)parentLayer).createSubLayer();
	}

	@Override
	public ILayer getRootLayer() {
        if (rootLayer == null || rootLayer.isDestroyed()) {
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
    @Override
    public void setActiveLayer(ILayer layer) {
        Checks.checkNotNull(layer, "layer");
        Checks.checkArgument(!layer.isDestroyed(), "The active layer may not be destroyed");
        Checks.checkArgument(containsLayer(layer), "The supplied layer is not contained in this screen");

        activeLayer = layer;
    }

	protected boolean containsLayer(ILayer layer) {
		return rootLayer != null && (rootLayer == layer || rootLayer.containsLayer(layer));
	}

    @Override
    public IRenderEnv getRenderEnv() {
        return renderEnv;
    }

	@Override
    public void setRenderEnv(IRenderEnv env) {
		renderEnv = env;

        sendSignal(new RenderEnvChangeSignal(env));
	}

}
