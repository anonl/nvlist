Rendering
=

@@@ Rendering system needs grouping, transforms and rendering logic.
@@@ Layers for grouping, maybe non-layer transform groups in the future. For now, use baseTransform property of transformables.
@@@ IDrawablePart/ITransformablePart to the transforms and set up the rendering environment (visibility, clipping, blending mode, etc.)
@@@ IRenderable handles render logic
@@@ Naming -- check what Android uses. I think it uses Drawable for what I call Renderable.

System requirements
-
NVList was originally designed to run even on exceptionally crummy hardware. In order to achieve this, any feature requiring OpenGL 1.3+ functionality was implemented with graceful degradation in mind. At the time, a meaningful percentage of (Android) devices didn't support OpenGL ES 2 yet. Now that we're a few years further along, devices without OpenGL ES 2.0 support are laughably outdated. It therefore seems reasonable to drop support for shaderless OpenGL versions in order to simplify rendering code.

Interfaces
-
	IVisualElement {
		IVisualGroup getParent();
		Rect2D getVisualBounds();
		short getZ();
	}
	
	IVisualGroup : IVisualElement {
	}
	
	ILayer : IVisualGroup {
		@Override
		ILayer getParent();
	}
	
	IDrawablePart : IVisualElement {
		@Override
		Rect2D getVisualBounds(); // Transformed render bounds, coords relative to drawable's parent
		
		IRenderable getRenderer();
		// Rendering logic is like a plugin of drawable
		void setRenderer(IRenderable);
	}
	
	IRenderable {
		void render(IDrawBuffer drawBuffer);
		Rect2D getLocalRenderBounds(); // Untransformed render bounds
	}

Scene graph
-
The visual scene graph has the following structure:
	
	Screen -> Layer
	Layer -> (Layer | TransformGroup | Drawable)*
	TransformGroup -> (TransformGroup | Drawable)*
	Drawable -> Renderable	

Screen is the root node and has one child, the root layer. Layers may contain sub-layers and/or other renderable elements. Layers perform clipping and provide an x/y offset for their children. TransformGroup and Drawable may be arbitrarily transformed, including rotation and scaling.

@@@ When I destroy a Layer, I want all of its contents recursively destroyed as well
@@@ When I destroy a transform group, my instinct would be to simply move its children to the transformgroup's parent.
@@@ Parent nodes must be notified when their children change parents or are destroyed.
@@@ Should layers/transformgroups be entities?

@@@ How the graph is mapped to a list of render commands in a draw buffer. All the render commands must be grouped into a contiguous chunk per-layer for the command sorter.

Renderables
-
* ImageRenderable
* NinePatchRenderable
* TextRenderable
* ButtonRenderable (draws a nine-patch with optional text on top)
* TweenRenderable (animated transition between Renderables, requires render-to-texture capabilities to tween anything other than Image<->Image)
