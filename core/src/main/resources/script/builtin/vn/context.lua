
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
    local speed = prefs.effectSpeed or 1
    if isSkipping() then
        speed = speed * 4
    end
    return speed
end

---Skip functions
-------------------------------------------------------------------------------------------------------------- @section skip functions

function getSkipState()
    return getCurrentContext():getSkipState()
end

function isSkipping()
    return getSkipState():isSkipping()
end

function shouldSkipLine()
    return getSkipState():shouldSkipLine(isLineRead())
end

function stopSkipping()
    return getSkipState():stopSkipping()
end

function getSkipMode()
    return getSkipState():getSkipMode()
end

---Increases the skip level to the specified mode. If the skip level is already at that level or higher,
-- nothing is changed.
function skip(mode)
    getSkipState():skip(mode)
end

---Turns skip mode on for the remainder of the paragraph.
-- @see skip
function skipParagraph()
    return skip(SkipMode.PARAGRAPH)
end

---Turns skip mode on for the remainder of the scene (the end of the file, or when a choice appears).
-- @see skip
function skipScene()
    return skip(SkipMode.SCENE)
end

---Wait functions
-------------------------------------------------------------------------------------------------------------- @section wait functions

---Waits for the specified time to pass. Time progression is influenced by the current
-- <code>effectSpeed</code> and the wait may be cancelled by holding the skip key or pressing the text
-- continue key.
-- @number durationFrames The wait time in frames (default is 60 frames per second).
function wait(durationFrames)
    while durationFrames > 0 do
        if shouldSkipLine() or Input.consume(VKeys.textContinue) then
            break
        end
        durationFrames = durationFrames - getEffectSpeed()
        yield()
    end    
end

---Waits until the text continue key is pressed. Skipping ignores the wait.
function waitClick()
    if shouldSkipLine() then
        return
    end

    local textBox = getTextBox()
    if textBox ~= nil then
        textBox:showClickIndicator()
    end
        
    while not shouldSkipLine() and not Input.consume(VKeys.textContinue) do
        yield()
    end
    
    if textBox ~= nil then
        textBox:hideClickIndicator()
    end
end

---Waits indefinitely.
function waitForever()
    while true do
        yield(60) --A high yield value will prevent the thread from waking up.
    end
end
