
---Functions related to the current script context
-- 
module("vn.context", package.seeall)

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

function getSkipState()
	return getCurrentContext():getSkipState()
end
