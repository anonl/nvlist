
#Scene graph structure
Screen is the wrapper around the scene graph. The visual hierarchy starts at a single root layer which is
created by the screen. This root layer may directly contain images but usually just contains several
sub-layers for the background, sprites, textbox, etc.

@@@ TODO: Expand

#Coordinate systems
@@@ TODO: Expand

The standard coordinate system used by libGDX/Scene2D has the y-axis pointing up instead of the traditional
y-down used by most software. For easier libGDX integration, NVLis could choose to use y-up as well. However,
since y-up is so rarely seen outside libGDX and because previous NVList versions used y-down, NVList 4 uses
y-down.

NVList uses a right-handed coordinate system with the following convention:
* The upper-left corner of the screen is at <code>(0, 0, 0)</code>
* x-axis pointing right
* y-axis pointing down
* z-axis pointing into the screen (higher Z values are further away)

#Scene node lifecycle
@@@ Issue: In the current impl. components may be detached from the scene graph without being destroyed.

@@@ Every node can be destroyed, reattached

#Pluggable renderers
@@@ Explain IRenderable and its uses

#Event handling

@@@ Signal passing system

## Input handling

@@@ Input is special, needs to take parent transform into account.

#Component types
* Image
* Text
* Button
* Panel/group component
* Scrollpane/viewport
* Toggle button / checkbox
* Radio button
* List

##Image
fdsf

##Text
fdsf

##Button
fdsf

##Panel
fdsf

##Viewport
fdsf

##Toggle button
Implemented by [Button](#button)



@@@ Create a list of everything that would need to be changed to implement this design
@@@ Create a new Git branch and start implementing


