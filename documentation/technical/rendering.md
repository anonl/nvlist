
#Scaling
For various reasons, the backbuffer may not be the same size as the virtual screen size. In this case NVList will attempt to render first to an offscreen buffer (FBO), then draw a scaled copy of that buffer to the screen. Black borders are used when the aspect ratios don't match. If the offscreen buffer is redundant (backbuffer is the same size) or unsupported by the graphics hardware the intermediate step may be skipped and NVList will render directly to the backbuffer.

Rendering to a fixed size buffer, then scaling the result has some advantages over directly rendering to the screen with a scale factor:
1. Scaling artifacts are reduced. Drawing everything at native resolution, then scaling up/down has slightly different semantics compared to drawing scaled versions of each image. Texture sampling will be different, and any operation that rounds to integer coordinates will be affected (for example clipping performed by layers).
2. NVList lets you to take screenshots in code, allowing you to read back previously rendered pixels. If the screen resolution is changed, a previously taken screenshot will still be at the old resolution. The screenshot reports its width/height in virtual coordinates, so the relative rendering size is fine, but the physical resolution will be fixed at the old value.

#Rendering

@@@ Blend modes -> everything should be premultiplied

@@@ Deferred rendering: Draw commands batched in DrawBuffer, then drawn in batches. Possibly reordered for more efficient rendering. Complete overkill for the type of rendering required by a visual novel, structure inherited from previous projects. Decouples rendering specifics from scene graph nodes, and allows the game logic to run in a separate thread from the renderer. Currently run in the same thread.

