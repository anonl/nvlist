
---Functions related to the main text box.
-- 
module("vn.textbox", package.seeall)

---Global accessor functions
-------------------------------------------------------------------------------------------------------------- @section globals

function getText()
    return getTextState():getText()
end

function getTextBox()
    return context.textBox
end

function getMainTextDrawable()
    return getTextState():getTextDrawable()
end

function setMainTextDrawable(textDrawable)
    getTextState():setTextDrawable(textDrawable)
end

---Registry of textboxes
-------------------------------------------------------------------------------------------------------------- @section registry

local textBoxRegistry = {
}

---Registers a textbox activation function for a given textmode. When the textmode changes, the corresponding
-- textbox constructor function is called to create a new textbox. The textbox created must implement the
-- standard textbox functions (install, destroy, etc.)
function registerTextBox(textMode, textBoxConstructor)
    textBoxRegistry[textMode] = textBoxConstructor
end

---Replaces the currently active textbox with the one associated with the given textmode.
-- @param textMode The current textmode (may be equal to the previous textmode)
function setActiveTextBox(textMode)
    local wasVisible = false
    local oldTextBox = getTextBox()
    if oldTextBox ~= nil then
        wasVisible = oldTextBox:isVisible()
        if wasVisible then
            oldTextBox:hide()
        end
        oldTextBox:destroy()
    end

    clearText()

    local textBoxConstr = textBoxRegistry[textMode]
    local textBox = nil 
    if textBoxConstr == nil then
        if textMode ~= 0 then
            Log.warn("No textbox registered for textmode {}", textMode)
        end
    else
        textBox = textBoxConstr()
        textBox:install()
        if wasVisible then
            textBox:hide(0) -- Set alpha to 0.0 (starts at 1.0)
            textBox:show() -- Gradually fade to visibility
        end
    end
    context.textBox = textBox
end

---Textbox functions
-------------------------------------------------------------------------------------------------------------- @section textbox

-- Prototype for TextBox implementations
local TextBox = {
    install = nil,
    destroy = nil,
    show = nil,
    hide = nil,
    visible = true,
    getTextDrawable = nil,
    setSpeaker = nil
}

function TextBox:install()
    context.textBox = self
    self:setSpeaker(nil)
    setMainTextDrawable(self:getTextDrawable())
    
    -- Store initial alpha values for each subcomponent so we can restore the alpha after fading out
    self.baseAlpha = {}
    for _,d in ipairs(self:getDrawables()) do
        self.baseAlpha[d] = d:getAlpha()
    end
end

function TextBox:destroy()
    self.layer:destroy()
end

function TextBox:setSpeaker(speaker)
    -- Returns false to indicate this textbox doesn't support a separate display area for the speaker name
    return false
end

function TextBox:getDrawables()
    return Image.getDrawables(self.layer)
end

function TextBox:isVisible()
    return self.visible
end

function TextBox:fadeTo(targetAlpha, duration)
    duration = duration or 30

    local threads = {}
    for _,d in ipairs(self:getDrawables()) do
        local a = targetAlpha * (self.baseAlpha[d] or 1.0)
        table.insert(threads, newThread(fadeTo, d, a, duration))
    end
    update1join(threads)
end

function TextBox:show(duration)
    self:fadeTo(1.0, duration)
    self.visible = true
end

function TextBox:hide(duration)
    self:fadeTo(0.0, duration)
    self.visible = false
end

local function createTextBoxLayer()
    local textLayer = createLayer(getRootLayer())
    textLayer:setZ(-1000)
    return textLayer
end

--- Creates and returns a new colored box in the given color
local function createBgBox(layer, colorARGB)
    local box = Image.createImage(layer, Image.getWhiteTexture())
    box:setZ(1000)
    box:setColorARGB(colorARGB)
    return box
end

--- Resizes inner to lie inside outer, with the specified amount of padding around it.
local function layoutPadded(outer, inner, pad)
    inner:setBounds(outer:getX() + pad, outer:getY() + pad,
        outer:getWidth() - pad*2, outer:getHeight() - pad*2)
end

---NVL textbox
-------------------------------------------------------------------------------------------------------------- @section NVL textbox

NvlTextBox = extend(TextBox, {
    textArea = nil,
    textBox = nil
})

function NvlTextBox.new(self)
    self = extend(NvlTextBox, self)
    
    local layer = createTextBoxLayer()
    local bgColor = 0xA0000000

    -- Create the main text box
    local textArea = Text.createTextDrawable(layer, "")
    textArea:setZ(-100)    
    local textPad = .05 * math.min(screenWidth, screenHeight);
    local textBox = createBgBox(layer, bgColor)
    textBox:setPos(math.floor(textPad), math.floor(textPad))
    textBox:setSize(math.ceil(screenWidth - textPad*2), math.ceil(screenHeight - textPad*2))
    layoutPadded(textBox, textArea, math.ceil(textPad * 0.50))
        
    self.layer = layer
    self.textArea = textArea
    self.textBox = textBox
    
    return self
end

function NvlTextBox:getTextDrawable()
    return self.textArea
end

---ADV textbox
-------------------------------------------------------------------------------------------------------------- @section ADV textbox

AdvTextBox = extend(TextBox, {
    textArea = nil,
    textBox = nil,
    nameLabel = nil,
    nameBox = nil
})

function AdvTextBox.new(self)
    self = extend(AdvTextBox, self)
    
    local layer = createTextBoxLayer()
    local bgColor = 0xE0000000

    -- Create the main text box
    local textArea = Text.createTextDrawable(layer, "")
    textArea:setZ(-100)    
    local textBox = createBgBox(layer, bgColor)
    local textPad = .025 * math.min(screenWidth, screenHeight);
    textBox:setSize(math.ceil(math.min(screenWidth, screenHeight * 1.30) - textPad*2),
        math.ceil(screenHeight/4 - textPad*2))
    textBox:setPos(math.floor((screenWidth - textBox:getWidth()) / 2), math.floor(textPad))
    layoutPadded(textBox, textArea, math.ceil(textPad * 0.75))
    
    -- Create a box for the speaker's name
    local nameLabel = Text.createTextDrawable(layer, "?")
    nameLabel:setZ(-100)

    local nameBox = createBgBox(layer, bgColor)    
    local namePad = .01 * math.min(screenWidth, screenHeight)
    nameBox:setSize(textBox:getWidth(), nameLabel:getTextHeight() + namePad * 2)
    nameBox:setPos(textBox:getX(), textBox:getY() + textBox:getHeight())    
    layoutPadded(nameBox, nameLabel, namePad)
    
    self.layer = layer
    self.textArea = textArea
    self.textBox = textBox
    self.nameLabel = nameLabel
    self.nameBox = nameBox
    
    return self
end

function AdvTextBox:getTextDrawable()
    return self.textArea
end

function AdvTextBox:setSpeaker(speaker)
    if speaker == nil then
        self.nameLabel:setText("")
        self.nameBox:setVisible(false)
    else
        self.nameLabel:setText(speaker)
        self.nameBox:setVisible(true)        
    end
    return true
end
