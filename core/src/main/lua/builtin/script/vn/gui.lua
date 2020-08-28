---GUI components
--
module("vn.gui", package.seeall)

-- ----------------------------------------------------------------------------
--  Variables
-- ----------------------------------------------------------------------------

-- ----------------------------------------------------------------------------
--  Functions
-- ----------------------------------------------------------------------------

---Creates a new on-screen button.
-- @string filename Path to an image file (relative to <code>res/img</code>).
-- @treturn ButtonDrawable The newly created button.
function button(filename)
    return Gui.createButton(getActiveLayer(), filename)
end

---Creates a new TextDrawable, used to display dynamic text on the screen.
-- @string[opt=""] text The initial text to display.
-- @treturn TextDrawable The newly created text drawable.
function textimg(text)
    return Text.createTextDrawable(getActiveLayer(), text)
end

---Creates a new panel with a grid layout
function gridPanel()
    return Gui.createGridPanel(getActiveLayer())
end

---Creates a new viewport
function createViewport()
    return Gui.createViewport(getActiveLayer())
end
