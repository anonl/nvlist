
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

---Textbox functions
-------------------------------------------------------------------------------------------------------------- @section textbox

-- Prototype for TextBox implementations
local TextBox = {
    install = nil,
    destroy = nil,
    show = nil,
    hide = nil,
    getTextDrawable = nil,
    setSpeaker = nil
}

function TextBox:install()
    context.textBox = self
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

function TextBox:getDrawables()
    return Image.getDrawables(self.layer)
end

function TextBox:fadeTo(targetAlpha, duration)
    local threads = {}
    for _,d in ipairs(self:getDrawables()) do
        local a = targetAlpha * (self.baseAlpha[d] or 1.0)
        table.insert(threads, newThread(fadeTo, d, a, duration))
    end
    update1join(threads)
end

function TextBox:show(duration)
    self:fadeTo(1.0, duration)
end

function TextBox:hide(duration)
    self:fadeTo(0.0, duration)
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

---Textbox functions
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
    local textArea = Text.createTextDrawable(layer, "TEXT")
    textArea:setZ(-100)    
    local textBox = createBgBox(layer, bgColor)
    local textPad = .025 * math.min(screenWidth, screenHeight);
    textBox:setSize(math.ceil(math.min(screenWidth, screenHeight * 1.30) - textPad*2),
        math.ceil(screenHeight/4 - textPad*2))
    textBox:setPos(math.floor((screenWidth - textBox:getWidth()) / 2), math.floor(textPad))
    layoutPadded(textBox, textArea, math.ceil(textPad * 0.75))
    
    -- Create a box for the speaker's name
    local nameLabel = Text.createTextDrawable(layer, "NAME")
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

function AdvTextBox:destroy()
    destroyValues{self.textArea, self.textBox, self.nameLabel, self.nameBox}
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
end
