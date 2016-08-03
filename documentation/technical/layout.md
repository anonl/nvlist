
@@@ Move interfaces for IGridLayout, IFlowLayout to api package. That means also moving the constraints impl and ILayout*Peer interfaces to API.


#Layout

@@@ This section will become very big


@@@ Use the adapter panel to get a layout elem for a visual elem. That way, I can let certain component types use a custom implementation. For example, a button might require a min size dependent on its ninepatch.



@@@ [ ] How to determine when to require a relayout? When does a layout become dirty?

@@@ Text/flow components don't really have a preferred size in the traditional sense. Their width/height are dependent on each other.

@@@ How do I integrate a layout system with my scene graph?
 - Check how scene2d javafx do it.
   javafx has specialized panel implementations per layout type. That's actually quite a good idea. It allows you to restrict the way sub-components can be added to the container.
   -- Panel -> FlowPanel, GridPanel
   - JavaFX has separate layoutXY vs translateXY properties. This allows the translation to be used as a relative offset compared to the layout.
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

##Layout algorithm

Top-down:
Step 1: compute min/pref sizes for children
Step 2: Size self
Step 3: Layout children
Recusive step: Because children bounds changed, a parent relayout may be needed. 

###Minimum, preferred, maximum sizes

@@@Require the size in the other axis to be accurate.
@@@getMinWidth(-1)xgetMinHeight(-1) isn't the minimum size of the component. Those are the minimum possible width/height given an infinite amount of space in the other axis.

##GridLayout

Layout attributes: rows, cols, insets
 - rows/cols can be auto-determined
Component attributes: anchor/dock, grow/stretch + weight, hidemode, min/pref/max sizes, insets

###Grid algorithm

@@@ Grid's primary axis is horizontal, future extension: make configurable.

Step 1: Calculate column widths
    1a: Set initial sizes based on the min. widths
    1b: Grow columns equally until the grid's layout width is reached, or the columns reach their preferred sizes
    1c: Grow columns equally until the grid's layout width is reached, or the columns reach their max. sizes
Step 2: Calculate cell widths based on column widths 
Step 3: Calculate row heights based on cell widths. Use the same multi-step algorithm as was used for the column widths.
Step 4: Calculate cell heights based on row heights

@@@ TODO: Spanned cells

@@@ TODO: min/pref/max size algorithms for the grid itself
@@@       min width/height use the max. min sizes
@@@       max width/height use the min. max sizes
@@@       pref width/height can be the same as the min sizes in the first release. A better algorithm can be used later.

##FlowLayout

@@@ Direction: right, left

###Flow algorithm
Add children to the layout one by one. Use preferred size to determine if a child fits on the current line. If the current line is empty, wrapping to the next line won't do anything so a child element is always considered to fit on a line by itself. After we've determined which elements go onto a line, we can layout the items within a line.
To layout the elements within a line, we first determine the sizes in the primary axis (x-axis for horizontal directions). Based on these primary sizes, we then request the matching preferred secondary sizes for each component. The biggest seconodary size becomes the secondary size for the row.
The min/pref/max sizes for a flow layout (or component using a flow layout internally, such as text) are always dependent on the size in the other axis (width depends on height, height depends on width).
If no secondary size is known, the min width/height of a flow layout are equal to the biggest width/height of any single component.
If no secondary size is known, the pref width/height of a flow layout are the equal to the size of the layout if all of its children were in a single row along the primary axis.
If no secondary size is known, the max width/height of a flow layout are equal to the largest possible sizes in those directions (all components aligned in a single row horizontally/vertically).

##Text

@@@ FlowLayout/text are an interesting case, don't really have a preferred size. Width/height are codependent.

@@@ TODO: Embedded images etc. in TextLayout should use a string representation of "\uFFFC" (Object replacement character)

