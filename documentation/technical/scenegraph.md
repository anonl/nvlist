

@@@ Rethink naming. I want a consistent naming between visual world and layout world.


#Scene graph structure
Screen is the wrapper around the scene graph. The visual hierarchy starts at a single root layer which is created by the screen. Each layer may contain any number of sub-layers and/or other visual elements.

@@@ TODO: Expand

@@@ TODO: Explain the basic interfaces: visual element, visual group

#Coordinate system
NVList requires you to specify a fixed virtual screen size to work with (default: 1280x720). Scripts and images are created assuming this fixed screen size. Using a fixed size makes various positioning/layout tasks easier to write, since absolute coordinates may be used.

NVList uses a right-handed coordinate system with the following convention:
* The upper-left corner of the screen is at `(0, 0, 0)`
* x-axis pointing right
* y-axis pointing down
* z-axis pointing into the screen (higher z values are further away)

The z-coordinate determines the draw order of elements within a layer. The elements are drawn back-to-front, going from the highest z-value to the lowest. Relative draw order between elements with an equal z-value is explicitly undefined. Note that the z-order is local to the layer. Layers themselves also have a z-coordinate which determines the relative draw order of layers.

##Other coordinate systems
@@@ TODO: Expand other coordinates: screen coordinates, OpenGL coordinates.

The standard coordinate system used by libGDX/Scene2D has the y-axis pointing up (like OpenGL) instead of the traditional y-down used by most software. For easier libGDX integration, NVList could choose to use y-up as well. However, since y-up is so rarely seen outside libGDX and because previous NVList versions used y-down, NVList 4 uses y-down.

## Nested transforms
@@@ Explain that x/y-coordinates are parent-relative and z-coordinates layer-relative.
@@@ z being different is confusing, but seems useful. Need to monitor how it works out in practise.

@@@ Layers have bounds + clipping, see #layer

@@@ VisualElement has ...
@@@ Transformable has ...

#Scene node lifecycle
@@@ Issue: In the current impl. components may be detached from the scene graph without being destroyed.

@@@ Every node can be destroyed, reattached

@@@ Destruction in nested contexts can be difficult. NVList3 used weak references to handle removal. If it's hard to guarantee destruction otherwise, that seems like a reasonable option.

#Event handling

@@@ Signal passing system
@@@ Application-wide event bus. All modules and screens are connected. Used to broadcast events throughout the framework.
@@@ Screen is notified by EventBus, then dispatches through the scenegraph.
@@@ Scenegraph events don't leave the scenegraph (for now).
@@@ Event order can be guaranteed for scenegraph events, but not for eventbus events.
@@@ Event bus is not currently needed, so don't build it yet until I have some good uses for it.

@@@ Think about to whom each signal should be passed. For example, see javafx EventTarget or Scene2d Actor.

##Input handling

@@@ Input is special, needs to take parent transform into account.

@@@ See: input.md

@@@ How to determine which nodes need to receive the input event?
@@@ If a parent node has no visual components, does it still receive input events?
@@@ If a child component lies outside its parent's visual bounds, does the parent receive bubbled input events?
@@@ If a single input event touches multiple hierarchies, are both hierarchies notified? In what order do the events bubble?

###Mouse input

@@@ alphaEnableThreshold, touchMargin -> PointerInputHelper

###Keyboard focus

@@@ Keyboard focus is part of the input handling

#Node types
* [Layer](#layer)
* [Image](#image)
* [Text](#text)
* [Button](#button)
* [Panel](#panel)
* [Viewport](#viewport)
* [Toggle-button, checkbox](#toggle-button)
* ~~Radio button~~ _(not yet implemented)_
* ~~List~~ _(not yet implemented)_
* ~~Combo box~~ _(not implemented)_
* ~~Slider~~ _(not implemented)_

##Layer
Layers have their own coordinate system. 

@@@ Layers are axis-aligned.

@@@ Future extension: control layer blending via shader (layer renders to FBO,
 then renders the FBO texture using the user-supplied shader). This could also be used to set the alpha of
 a layer, or use a bitmap tween on it.

@@@ Future extension: Support for 3D rendering.

##Image
fdsf

###Pluggable renderers
@@@ Explain IRenderable and its uses
@@@ Used primarily by Image, but also used inside other nodes like button.

##Text
fdsf

###Text renderer features
* Text styles
** Font
** Style (normal, bold, italic)
** Size
** Outline (color + size)
** Shadow (offset + color)
** Underline
* Right-to-left and BiDi text
* Kerning (through libGDX)

Not supported:
* Subscript/superscript
* Strikethrough
* Ligatures
* Text shaping (HarfBuzz)
Required for complex scripts such as Arabic where letter shapes are context-dependent.

##Button
@@@ Backgrounds are single texture or ninepatch.
@@@ Optional styled text label overlaid.

@@@ ButtonModel, ButtonRenderer

@@ Button groups

##Panel
@@@ Panel is visual group with a single texture or ninepatch background. Sub-components are arranged using a layout.

##Viewport
fdsf

@@@ Clipping performed by wrapped layer. This layer is passed to the viewports constructor and used to
    initialize the viewport. From that point on, the layer is sized by the viewport.
    
@@@ add/remove operations are delegated to the layer.

@@@ Viewport has optional scrollbars

##Toggle-button
Implemented by [Button](#button)

#Layout
See [layout.md](layout.md)


@@@ Split into interfaces for all the various aspects: node, layout, signal, input, etc.
@@@ Create a list of everything that would need to be changed to implement this design
    [ ] Port textbox code to use a layout to position its elements and clickindicator.
    [ ] Better define in which coordinate system the collision shape and contains method function.
    [ ] To render components in visual groups, I need to concatenate the node's transform with its parents's transform. I don't think this is always done properly in the current implementation. Notably, IDrawable can't implement IDrawTransform.
    [ ] Use package name vn.impl.* instead of vn.*.impl
    [ ] I don't want to implement IRenderable<->ITransformable multiple times. Just integrate it into Transformable and make all Transformables use a renderer.
@@@ Create a new Git branch and start implementing




@@@
[ ] Op welke plekken introduceer ik een nieuw coordinatensysteem?
  - Elke branch node in de boom. Als je geen rotaties hebt, dan kun je de x/y translaties vna de parents optellen, maar bij ortaties kun je beter elke keer met een nieuw coordinatenstelsel beginnen.
[ ] Op welke plekken heb ik mogelijk last van een nested transform?
  1. Rendering
  2. collision detection
  - Voor rendering kan ik gewoon de parent transform meegeven als parameter.
  - Collision detection als in picking (bepalen welk component op een bepaalde scherm-pixel ligt) is wat vervelender, maar in mijn huidige opzet worden input signalen getransformeerd doorgeven door de boom. De sub-nodes doen dan hun eigen bounds checking. Als ik straks iets met capture/bubble event handling wil doen, dan kan ik dit hierarchische systeem gebruiken voor de capture fase. Als ik dan eenmaal een lijst van affected nodes heb, dan zijn dat er weinig genoeg dat efficiëntie niet zo belangrijk meer is.
  

