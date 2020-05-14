---
id: vn/textlog.lua
title: vn/textlog.lua
---

<!--excerpt-->

{% include sourcecode.html id="textnotation" lang="lua" class="full-screen" content="
---Functions related to the text history screen
-- 
module(\"vn.textlog\", package.seeall)

---Text log screen registry
-------------------------------------------------------------------------------------------------------------- @section registry

-- Forward declaration
local textLogConstructor = nil 

---Registers a text log screen creation function. The screen return by this function must implement the standard
-- text log screen methods (show, destroy)
function registerTextLogScreen(constructor)
    textLogConstructor = constructor
end

---Global accessor functions
-------------------------------------------------------------------------------------------------------------- @section globals

---Shows the textlog screen.
function textLog()
    callInContext(function()
        local screen = textLogConstructor()
        screen:show()
        screen:destroy()
    end)
end

---TextLog screen
-------------------------------------------------------------------------------------------------------------- @section TextLog

TextLogScreen = &#123;
    layer = nil
}

function TextLogScreen.new(self)
    self = extend(TextLogScreen, self)
    
    return self
end

function TextLogScreen:destroy()
    if self.layer ~= nil then
        self.layer:destroy()
        self.layer = nil
    end
end

function TextLogScreen:show()
    local oldActiveLayer = getActiveLayer()

    self.layer = createLayer(oldActiveLayer)
    setActiveLayer(self.layer)

    local topHeight = screenHeight / 16
    local bottomHeight = screenHeight / 8
    
    local edgeTop = img(\"gui/textlog#edge-top\")
    edgeTop:setZ(1000)
    edgeTop:setBounds(0, 0, screenWidth, topHeight)
    
    local edgeBottom = img(\"gui/textlog#edge-top\")
    edgeBottom:setZ(1000)
    edgeBottom:setBounds(0, screenHeight - bottomHeight, screenWidth, bottomHeight)
            
    local viewport = createViewport()
    viewport:setBounds(0, topHeight, screenWidth, screenHeight - topHeight - bottomHeight)

    local returnButton = button(\"gui/button\")
    returnButton:setAlign(0.5, 0.5)
    returnButton:setText(\"Return\")
    returnButton:setPos(screenWidth / 2, screenHeight - bottomHeight / 2)
    returnButton:setWidth(200)

    local panel = gridPanel()
    local pad = screenHeight / 16
    panel:setInsets(pad, pad, pad, pad)
    panel:setRowSpacing(pad)
    viewport:setContents(panel)
    
    local textLog = getTextState():getTextLog()
    local page = textLog:getPageCount() - 1
    while page >= 0 do
        local text = textimg(textLog:getPage(page))

        panel:add(text):growX()
        panel:endRow()
        
        page = page - 1
    end
    panel:pack()
    
    -- Scroll to bottom
    viewport:scroll(0, 999999)

    setActiveLayer(oldActiveLayer)

    while not Input.consume(VKeys.cancel) and not Input.consume(VKeys.down) do
        if returnButton:consumePress() then
            break
        end
        yield()
    end 
end

-- Set as default text log screen
registerTextLogScreen(TextLogScreen.new)
" %}
                