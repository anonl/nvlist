--- Contains functions related to the operating system and external environment.
-- 
module("vn.system", package.seeall)

-- ----------------------------------------------------------------------------
--  Variables
-- ----------------------------------------------------------------------------

local exitFunction = nil

-- ----------------------------------------------------------------------------
--  Callbacks 
-- ----------------------------------------------------------------------------

-- This function is called when the user presses the window close button or exit option from the window menu.
function onExit()
    if exitFunction == nil then
        return System.exit(true)
    end

    local ctxt = createContext(function()
        if exitFunction() then
            System.exit(true)
            return
        end
        getCurrentContext():destroy()
    end)
    setContextActive(ctxt, true)
end

-- ----------------------------------------------------------------------------
--  Functions
-- ----------------------------------------------------------------------------

---Completely resets all state and restarts from the titlescreen.
function restart()
    return System.restart()
end

---Checks if NVList is running in an environment where it makes sense to close
-- itself. When running embedded in a webpage for example, exiting doesn't make
-- much sense.
function canExit()
    return System.canExit()
end

---Asks (or forces) the engine to exit.
-- @bool[opt=false] force If <code>true</code>, forces an exit. Otherwise, the
--      user will be presented with a confirmation popup.
function exit(force)
    return System.exit(force)
end

---Sets a custom function to be called when the user tries to exit the program or when the <code>exit</code>
-- function is called. This starts a new mode, &quot;exit&quot; in which the supplied exit function is called.
-- @func func The function to call when the &quot;exit&quot; submode is entered. If this function returns
--       <code>false</code>, the exit process is cancelled.
function setExitFunction(func)
    exitFunction = func
end

---Opens the website with the given <code>url</code> in an external web browser. 
-- @string url The URL of the website.
function website(url)
    return System.openWebsite(url)
end

---Returns a textual representation of the total play time.
function getPlayTime()
    return System.getTimer():getTotalTime():toString()
end
