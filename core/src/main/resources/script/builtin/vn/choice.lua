
---Defines functions for allowing a user to select from a list of options
-- 
module("vn.choice", package.seeall)


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
    
    -- TODO: Show choice screen
    local selected = 1
    -- local selected = Screens.choice(uniqueChoiceId, options)
    -- seenLog:setChoiceSelected(uniqueChoiceId, selected)
    
    return selected
end
