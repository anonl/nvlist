--- Contains functions related to the operating system and external environment.
-- 
module("vn.system", package.seeall)

-- ----------------------------------------------------------------------------
--  Variables
-- ----------------------------------------------------------------------------

-- ----------------------------------------------------------------------------
--  Callbacks 
-- ----------------------------------------------------------------------------

-- This function is called when the user presses the window close button or exit option from the window menu.
function onExit()
    System.exit(true)
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

---Opens the website with the given <code>url</code> in an external web browser. 
-- @string url The URL of the website.
function website(url)
    return System.openWebsite(url)
end
