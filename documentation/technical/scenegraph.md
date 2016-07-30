
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

The standard coordinate system used by libGDX/Scene2D has the y-axis pointing up (like OpenGL) instead of the traditional y-down used by most software. For easier libGDX integration, NVLis could choose to use y-up as well. However, since y-up is so rarely seen outside libGDX and because previous NVList versions used y-down, NVList 4 uses y-down.

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
@@@ I'd like to expand signal passing to outside the scene graph. The root node would be notified by the general system, and then the signal would travel down the tree using scenegraph-specific logic.

##Input handling

@@@ Input is special, needs to take parent transform into account.

@@@ See: input.md

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

##Layer
Layers have their own coordinate system. 

@@@ Layers are axis-aligned. Future extension: control layer blending via shader (layer renders to FBO,
 then renders the FBO texture using the user-supplied shader). This could also be used to set the alpha of
 a layer, or use a bitmap tween on it.

##Image
fdsf

###Pluggable renderers
@@@ Explain IRenderable and its uses
@@@ Used primarily by Image, but also used inside other nodes like button.

##Text
fdsf

###Text renderer features
* Text styles
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

@@@ This section will become very big

@@@ How do I integrate a layout system with my scene graph?
 - Check how scene2d javafx do it.
 - Possibility, let visual elements implement ILayoutElement, (ILayoutGroup?) to let them opt into layout
   support.
 - The way scene2d does it is wrap the visual elements in layout containers. The layout containers then
   transfer their bounds to the wrapper visual elements.

@@@ Research: Use a custom MigLayout back-end for my layouting purposes. That would save me from having to
    implement flow/grid/etc layouts myself. It's a bit more work initially, but I'd have a high quality
    layout engine and implementing a new back-end for it might point me to some layout problems I otherwise
    wouldn't have considered.
  - I should at least read the MigLayout source code to understand what kinds of operations my components
    and containers would need to support.
    
## GridLayout

Layout attributes: rows, cols, insets
 - rows/cols can be auto-determined
Component attributes: anchor/dock, grow/stretch + weight, hidemode, min/pref/max sizes, insets



@@@ Split into interfaces for all the various aspects: node, layout, signal, input, etc.
@@@ Create a list of everything that would need to be changed to implement this design
    [ ] Port textbox code to use a layout to position its elements and clickindicator.
@@@ Create a new Git branch and start implementing


