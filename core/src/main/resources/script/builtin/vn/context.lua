
---Functions related to the current script context
-- 
module("vn.context", package.seeall)

-- ----------------------------------------------------------------------------
--  Variables
-- ----------------------------------------------------------------------------

-- Init dummy prefs if needed
prefs = prefs or {}

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

function getRenderEnv()
    return getScreen():getRenderEnv()
end

function getEffectSpeed()
    return prefs.effectSpeed or 1
end

-- ----------------------------------------------------------------------------
--  Skip functions
-- ----------------------------------------------------------------------------

function getSkipState()
	return getCurrentContext():getSkipState()
end

function isSkipping()
    return getSkipState():isSkipping()
end

function getSkipMode()
    return getSkipState():getSkipMode()
end

function setSkipMode(mode)
    return getSkipState():setSkipMode(mode)
end

function stopSkipping()
    return getSkipState():stopSkipping()
end

function waitClick(hard)
    --getSkipState():waitClick(hard)
    while not Input.consume(VKeys.textContinue) do
        yield()
    end
end
