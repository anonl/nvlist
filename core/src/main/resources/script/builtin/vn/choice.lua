
---Defines functions for allowing a user to select from a list of options
-- 
module("vn.choice", package.seeall)

---Choice screen registry
-------------------------------------------------------------------------------------------------------------- @section registry

local choiceScreenRegistry = {
    constructor = nil
}

---Registers a choice screen creation function. When the choice function is called, the registered constructor
-- function is called to create a new choice screen. The choice screen created must implement the standard
-- choice screen functions (choose, destroy, etc.)
function registerChoiceScreen(choiceScreenConstructor)
    choiceScreenRegistry.constructor = choiceScreenConstructor
end

---Choice functions
-------------------------------------------------------------------------------------------------------------- @section choice functions

---Asks the user to select an option.
-- @string ... Any number of strings to use as options. Example use:
--         <code>choice("First option", "Second option")</code>
-- @treturn number The index of the selected option (starting at <code>1</code>).
function choice(...)
    return choice2(getScriptPos(1), ...)
end

function choice2(uniqueChoiceId, ...)
    local options = getTableOrVarArg(...)
    if options == nil or #options == 0 then
        options = {"Genuflect"}
    end
    
    local selected = 1
    local choiceScreenConstr = choiceScreenRegistry.constructor
    if choiceScreenConstr == nil then
        Log.warn("No choice screen registered")
    else    
        local screen = choiceScreenConstr()
        selected = screen:choose(uniqueChoiceId, options)
        -- seenLog:setChoiceSelected(uniqueChoiceId, selected)
        screen:destroy()
    end
    return selected
end

---Default choice screen
-------------------------------------------------------------------------------------------------------------- @section default choice screen

ChoiceScreen = {
}

function ChoiceScreen.new(self)
    self = extend(ChoiceScreen, self)    
    return self
end

function ChoiceScreen:destroy()
end

---Show the user a selection screen
-- @param uniqueChoiceId Unique identifier for the script location from which this choice screen was
--        triggered.
-- @param options Table of styled text option descriptions.
function ChoiceScreen:choose(uniqueChoiceId, options)
    
end

registerChoiceScreen(ChoiceScreen.new)
