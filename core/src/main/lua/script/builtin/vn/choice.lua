
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
        Seen.registerChoice(uniqueChoiceId, #options)
    
        local screen = choiceScreenConstr()
        selected = screen:choose(uniqueChoiceId, options)
        screen:destroy()
        
        Seen.markChoiceSelected(uniqueChoiceId, selected)
    end
    return selected
end

---Default choice screen
-------------------------------------------------------------------------------------------------------------- @section default choice screen

ChoiceScreen = {
    seenStyle=nil --Text style extension for previously selected choices
}

function ChoiceScreen.new(self)
    self = extend(ChoiceScreen, self)
    
    self.seenStyle = self.seenStyle or Text.createStyle{color=0xFF808080}
    
    return self
end

function ChoiceScreen:destroy()
end

---Show the user a selection screen
-- @param uniqueChoiceId Unique identifier for the script location from which this choice screen was
--        triggered.
-- @param options Table of styled text option descriptions.
function ChoiceScreen:choose(uniqueChoiceId, options)
    if #options == 0 then
        return
    end

    Log.info("Showing choice screen (id={}): {}", uniqueChoiceId, table.concat(options, ", "))

    local panel = gridPanel()
    local pad = 100
    panel:setBounds(pad, pad, screenWidth - pad*2, screenHeight - pad*2)
    
    local buttons = {}
    for i,option in ipairs(options) do
        local b = button("gui/button")
        b:setZ(-1000)
        
        local styledText = option
        if Seen.hasSelectedChoice(uniqueChoiceId, i) then
            --Apply a custom text style if the user has selected this choice before
            styledText = Text.createStyledText(option, self.seenStyle)
        end
        b:setText(styledText)
        buttons[i] = b
        
        panel:add(b):growX()
        panel:endRow()
    end
    panel:pack()
    
    local selected = 0
    while selected == 0 do
        for i,b in ipairs(buttons) do
            if b:consumePress() then
                selected = i
            end
        end
        yield()
    end
    panel:destroy()
    
    Log.info("Choice selected (id={}): [{}] {}", uniqueChoiceId, selected, options[selected])
    
    return selected
end

registerChoiceScreen(ChoiceScreen.new)
