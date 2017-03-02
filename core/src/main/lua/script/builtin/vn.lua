--- Provides core functions for scripting visual novels.
--  @module vn

require("builtin/stdlib")
require("builtin/callbacks")

-- ----------------------------------------------------------------------------
--  Functions
-- ----------------------------------------------------------------------------

-- Prints a warning when called from a script targetting an engine version more recent than the time of
-- deprecation.
function deprecated(deprecatedSince)
    local targetVersion = prefs.engineTargetVersion
    if deprecatedSince ~= nil and targetVersion ~= nil
            and System.compareVersion(deprecatedSince, targetVersion) <= 0
    then
        local info = debug.getinfo(3, 'n')
        Log.warn("Warning: Deprecated function used ({})", info.name)
    end
end

---Executes the script with the given filename. When the called script
-- completes, resumes executing the current script. Use
-- <code>jump("some-script.lvn")</code> when you don't want/need to
-- come back to the current script.
-- @string filename Path to the script, relative to the <code>res/script</code>
--         folder.
function call(filename)
    savepoint(filename)
    return dofile(filename)
end


---Jumps execution to the specified script file. If you want to resume from the
-- current position after the new script ends, use <code>call</code> instead.
-- @string filename Path to the script, relative to the <code>res/script</code>
--         folder.
function jump(filename)
    savepoint(filename)
    return Thread.jump(filename)
end

---Include submodules
-------------------------------------------------------------------------------------------------------------- @section submodules

module("vn", package.seeall)

--Require submodules
local submodules = {
    "anim",
    "choice",
    "context",
    "gui",
    "image",
    "imagefx",
    "save",
    "savescreen",
    "sound",
    "system",
    "text",
    "textbox",
    "textlog",
    "tween",
    "video"
}
for _,module in ipairs(submodules) do
    require("builtin/vn/" .. module)
end

-- ----------------------------------------------------------------------------
--  Flatten functions
-- ----------------------------------------------------------------------------

local function flattenSingle(env, pkg)
    if pkg == nil then
        return
    end

    for k,v in pairs(pkg) do
        if k[0] ~= '_' then
            env[k] = v
        end
    end
end

-- Flattens this module and its submodules into <code>env</code>
-- @param env The table (often <code>_G</code>) to flatten the module into.
local function flattenModule(env)    
    flattenSingle(env, package.loaded.vn)
    for _,module in ipairs(submodules) do
        flattenSingle(env, package.loaded.vn[module])
    end
    _G.vn = nil --Delete the now flattened table
end

-- Flatten submodules into main namespace
flattenModule(_G)

---Initialization
-------------------------------------------------------------------------------------------------------------- @section init

-- Register default textboxes
registerTextBox(TextMode.NVL, NvlTextBox.new)
registerTextBox(TextMode.ADV, AdvTextBox.new)

