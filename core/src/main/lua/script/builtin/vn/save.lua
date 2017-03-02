--- Functionality related to saving/loading data
-- 
module("vn.save", package.seeall)

---Access to global storage
-------------------------------------------------------------------------------------------------------------- @section globals

---Sets a shared global variable. Similar to <code>setGlobal</code>, except all
-- save slots have access to the same set of <em>shared</em> globals. Shared
-- globals are often used to mark routes as cleared or unlocked.
-- @string name The name of the shared global. Names starting with
--        <code>vn.</code> are reserved for use by NVList.
-- @param value The new value to store for <code>name</code>.
function setSharedGlobal(name, value)
    Save.getSharedGlobals():set(name, value)
end

---Returns a value previously stored using <code>setSharedGlobal</code>.
-- @param name The name of the shared global.
-- @return The stored value, or <code>nil</code> if none exists.
-- @see setSharedGlobal
function getSharedGlobal(name)
    return Save.getSharedGlobals():get(name)
end

---Autosave
-------------------------------------------------------------------------------------------------------------- @section Autosave

local KEY_AUTOSAVE_SLOTS = "autoSaveSlots"

---Returns the number of autosave slots.
-- @treturn int The number of autosave slots, or <code>0</code> if autosaving is turned off. 
function getAutoSaveSlots()
    return getSharedGlobal(KEY_AUTOSAVE_SLOTS) or 4
end

---Changes the number of autosave slots used.
-- @int slots The number of autosave slots to cycle through. Use 0 to effectively turn off auto saving. 
function setAutoSaveSlots(slots)
    setSharedGlobal(KEY_AUTOSAVE_SLOTS, slots or 0)
end

