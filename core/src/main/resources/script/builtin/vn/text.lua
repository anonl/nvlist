
---Functions related to text display.
-- 
module("vn.text", package.seeall)

-- ----------------------------------------------------------------------------
--  Variables
-- ----------------------------------------------------------------------------

-- ----------------------------------------------------------------------------
--  Local Functions
-- ----------------------------------------------------------------------------

-- ----------------------------------------------------------------------------
--  Functions
-- ----------------------------------------------------------------------------

function getScreen()
	return getCurrentContext():getScreen()
end

function getTextState()
	return getScreen():getTextState()
end

function getMainTextBox()
	return getTextState():getTextDrawable()
end
function setMainTextBox(textDrawable)
	getTextState():setTextDrawable(textDrawable)
end
