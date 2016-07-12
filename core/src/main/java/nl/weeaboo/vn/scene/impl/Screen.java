package nl.weeaboo.vn.scene.impl;

import nl.weeaboo.common.Checks;
import nl.weeaboo.common.Rect2D;
import nl.weeaboo.io.CustomSerializable;
import nl.weeaboo.vn.core.IRenderEnv;
import nl.weeaboo.vn.core.impl.StaticEnvironment;
import nl.weeaboo.vn.input.IInput;
import nl.weeaboo.vn.input.VKey;
import nl.weeaboo.vn.math.Matrix;
import nl.weeaboo.vn.render.IDrawBuffer;
import nl.weeaboo.vn.scene.ILayer;
import nl.weeaboo.vn.scene.IScreen;
import nl.weeaboo.vn.scene.IScreenTextState;
import nl.weeaboo.vn.scene.ITextDrawable;
import nl.weeaboo.vn.signal.ISignal;
import nl.weeaboo.vn.signal.RenderEnvChangeSignal;
import nl.weeaboo.vn.signal.TickSignal;
import nl.weeaboo.vn.text.ITextRenderer;

@CustomSerializable
public class Screen implements IScreen {

    private static final long serialVersionUID = SceneImpl.serialVersionUID;

	private final Rect2D bounds;
    private final IScreenTextState textState;

	private ILayer rootLayer; // Lazily (re-)initialized when null or destroyed
	private ILayer activeLayer; // Could potentially point to a destroyed layer (minor memory leak)
	private IRenderEnv renderEnv;

    public Screen(Rect2D bounds, IRenderEnv env, IScreenTextState textState) {
		this.bounds = Checks.checkNotNull(bounds);
		this.renderEnv = Checks.checkNotNull(env);
        this.textState = Checks.checkNotNull(textState);
	}

    @Override
    public void update() {
        textState.update();

        sendSignal(new TickSignal());

        IInput input = StaticEnvironment.INPUT.get();
        if (!input.isIdle()) {
            getRootLayer().handleInput(Matrix.identityMatrix(), input);

            handleInput(input);
        }
    }

    private void handleInput(IInput input) {
        ITextDrawable td = textState.getTextDrawable();
        if (td != null) {
            int lineCount = td.getLineCount();
            int endLine = td.getEndLine();
            if (td.getVisibleText() < td.getMaxVisibleText() && input.consumePress(VKey.TEXT_CONTINUE)) {
                // Make all glyphs in the current lines fully visible
                td.setVisibleText(ITextRenderer.ALL_GLYPHS_VISIBLE);
            } else if (endLine < lineCount && input.consumePress(VKey.TEXT_CONTINUE)) {
                // Scroll to new visible lines
                td.setVisibleText(endLine, 0f);
            }
        }
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
    public IScreenTextState getTextState() {
        return textState;
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
        renderEnv = Checks.checkNotNull(env);

        sendSignal(new RenderEnvChangeSignal(env));
	}

}
