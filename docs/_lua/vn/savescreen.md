---
id: vn/savescreen.lua
title: vn/savescreen.lua
---

<!--excerpt-->

{% include sourcecode.html id="textnotation" lang="lua" class="full-screen" content="
---Functions related to the save/load screens
-- 
module(\"vn.savescreen\", package.seeall)

local KEY_SAVE_LAST = \"vn.save.lastSaved\" -- Property value gets set in onSave()
local KEY_SAVE_PAGE = \"vn.save.lastPage\"

local SCREENSHOT_WIDTH = 256
local SCREENSHOT_HEIGHT = 144

---Save/load screen registry
-------------------------------------------------------------------------------------------------------------- @section registry

-- Forward declarations
local saveScreenConstructor = nil
local loadScreenConstructor = nil 

---Registers a save screen creation function. The save screen return by this function must implement the standard
-- save screen methods (show, destroy)
function registerSaveScreen(constructor)
    saveScreenConstructor = constructor
end

---Registers a load screen creation function. The load screen return by this function must implement the standard
-- load screen methods (show, destroy)
function registerLoadScreen(constructor)
    loadScreenConstructor = constructor
end

---Global accessor functions
-------------------------------------------------------------------------------------------------------------- @section globals

---Shows the save or load screen.
local function saveLoadScreen(isSave)
    local slot = nil
    local userData = nil

    callInContext(function()
        local function showScreen()
            local screen = nil
            if isSave then
                screen = saveScreenConstructor()
            else
                screen = loadScreenConstructor()
            end
            slot, userData = screen:show()
            screen:destroy()
        end
        showScreen()
    end)

    if slot > 0 then
        if isSave then
            -- Take a screenshot to add to the save file
            local ss = screenshot(getRootLayer(), -32768)
            ss:markTransient()
            local screenshotInfo = &#123;screenshot=ss, width=SCREENSHOT_WIDTH, height=SCREENSHOT_HEIGHT}

            Save.save(slot, userData, screenshotInfo)
            setSharedGlobal(KEY_SAVE_LAST, slot)
        else
            Save.load(slot)
        end
    end

    return slot
end

---Shows the save screen.
-- @return The save slot that the user saved in, or nil if cancelled.
function saveScreen()
    return saveLoadScreen(true)
end

---Shows the load screen.
function loadScreen()
    return saveLoadScreen(false)
end


---Save slot component
-------------------------------------------------------------------------------------------------------------- @section SaveSlot

local SaveSlot = &#123;
    slot=1, -- Save slot index
    isSave=false, -- True if we're in save mode, false if we're loading
    isNew=false, -- Was this save slot written recently
    isEmpty=true, -- True if the slot is unused

    compact=false, -- Toggles between full view and compact view
    
    backgroundImagePath=\"gui/savescreen#slotButton-\",
    screenshot=nil, -- Thumbnail image (nil for empty slots)
    }

function SaveSlot.new(self)
    self = extend(SaveSlot, self)
    
    local buttonImagePath = \"gui/savescreen#slotButton-\"
    if self.compact then
        buttonImagePath = \"gui/savescreen#quicksave-\"
    end
    
    local button = button(buttonImagePath)
    button:setToggle(true)
    button:setEnabled(self.isSave or not self.isEmpty)
    
    local label = textimg(self.label)
    label:setZ(button:getZ() - 10)
    
    local i = nil   
    local newI = nil
    if not self.compact then
        if self.screenshot ~= nil then
            i = img(self.screenshot)
        else
            i = img(\"gui/savescreen#noImage\")
        end
        i:setZ(button:getZ() - button:getWidth()/2)   

        if self.isNew and not self.isEmpty then
            newI = img(\"gui/savescreen#newSave\")
            newI:setZ(i:getZ() - 1)
        end
    end

    self.button = button
    self.label = label
    self.image = i
    self.newImage = newI

    return self
end

function SaveSlot:destroy()
    destroyValues&#123;self.button, self.image, self.newImage, self.label}
end

function SaveSlot:getBounds()
    return self.button:getVisualBounds()
end

function SaveSlot:setBounds(x, y, w, h)
    local b = self.button
    local l = self.label
    local i = self.image

    local fontSize = math.ceil(w * .065)
    l:extendDefaultStyle(Text.createStyle&#123;align=\"center\", fontSize=fontSize})

    if i ~= nil then
        local scale = math.min(w / 256, h / 192)
        b:setScale(scale)
        x = x + (w - b:getWidth()) / 2
        y = y + (h - b:getHeight()) / 2
        w = b:getWidth()
        h = b:getHeight()
        b:setPos(x, y)

        local iw = 224 * scale
        local ih = 126 * scale
        local ipad = (w - iw) / 2
        i:setBounds(x + ipad, y + ipad, iw, ih)

        local lh = h - ih - ipad
        l:setSize(iw, lh)
        l:setPos(math.ceil(i:getX()), math.ceil(y + h - (lh + l:getTextHeight()) / 2))
    else
        b:setBounds(x, y, w, h)
        l:setSize(w, h)
        l:setPos(math.ceil(x), math.ceil(y + (h - l:getTextHeight()) / 2))
    end

    local newI = self.newImage
    if newI ~= nil then
        if i ~= nil then    
            --Align with top-right of screenshot
            newI:setPos(i:getX() + i:getWidth() - newI:getWidth(), i:getY())
        else
            --Align with top-right of button
            newI:setPos(b:getX() + b:getWidth() - newI:getWidth(), b:getY())
        end
    end 
end

---Save/Load screen
-------------------------------------------------------------------------------------------------------------- @section SaveLoadScreen

local SaveLoadScreen = &#123;
    isSave=false,
    page=nil,
    pages=10,
    selected=0,
    metaData=nil, --Meta data Lua table added to the save data.
    rows=2,
    cols=5,
    newSaveSlot=SaveSlot.new,
    x=0,
    y=0,
    w=screenWidth,
    h=screenHeight,
    pack=5,
    qcols=5,
    qh=screenHeight/14,
    qpack=6,
    pad=nil,
    screenshotWidth=nil,
    screenshotHeight=nil,
    --GUI Components
    pageButtons=nil,
    saves=nil,
    qsaves=nil,
    okButton=nil,
    cancelButton=nil,
    topFade=nil,
    bottomFade=nil
    }

function SaveLoadScreen.new(self)
    self = extend(SaveLoadScreen, self)
                
    self.page = self.page or getSharedGlobal(KEY_SAVE_PAGE) or 1
    self.pad = self.pad or math.min(self.w, self.h) / 100
    self.screenshotWidth = self.screenshotWidth or prefs.saveScreenshotWidth                
    self.screenshotHeight = self.screenshotHeight or prefs.saveScreenshotHeight             
                
    self.saves = &#123;}
    self.qsaves = &#123;}        
        
    local okText = \"Load\"
    if self.isSave then
        okText = \"Save\"
    end
    
    local cancelText = \"Cancel\"
    
    local okB = button(\"gui/savescreen#button-\")
    okB:setText(okText)
    
    cancelB = button(\"gui/savescreen#button-\")
    cancelB:setText(cancelText)
    
    self.pageButtons = &#123;}       
    for p=1,self.pages do
        local tb = button(\"gui/savescreen#pageButton-\")
        tb:setText(p)
        tb:setToggle(true)
        self.pageButtons[p] = tb
    end
    
    self.okButton = okB
    self.cancelButton = cancelB
        
    local topFadeTex = tex(\"gui/savescreen#fade-top\", true)
    if topFadeTex ~= nil then
        self.topFade = img(topFadeTex, &#123;z=10})
    end

    local bottomFadeTex = tex(\"gui/savescreen#fade-bottom\", true)
    if bottomFadeTex ~= nil then
        self.bottomFade = img(bottomFadeTex, &#123;z=10})
    end
        
    self:setPage(self.page, true)
    self:initQSaves()
        
    return self
end

function SaveLoadScreen:destroy()
    destroyValues(self.pageButtons)
    destroyValues(self.saves)
    destroyValues(self.qsaves)
    destroyValues&#123;self.okButton, self.cancelButton}
    destroyValues&#123;self.topFade, self.bottomFade}
end

function SaveLoadScreen:layout()
    local x = self.x
    local y = self.y
    local w = self.w
    local h = self.h
    local qh = self.qh
    
    local ipad = self.pad
    local vpad = h / 7
    local mainW = w - ipad*2
    local mainH = h - vpad*2 - qh - ipad*3

    -- Layout page buttons
    local pageButtonPanel = gridPanel()
    pageButtonPanel:setBounds(x, y, w, vpad)
    for _,pb in ipairs(self.pageButtons) do
        pageButtonPanel:add(pb)
    end
    pageButtonPanel:pack(5)
    
    local function gridLayout(bounds, cols, components)
        local x = bounds.x
        local y = bounds.y
        
        local rows = math.ceil(#components / cols)
        local cw = bounds.w / cols
        local ch = bounds.h / rows
        
        local col = 0
        for _,component in ipairs(components) do
            component:setBounds(x + ipad, y + ipad, cw - ipad * 2, ch - ipad * 2)
        
            col = col + 1
            if col >= cols then
                col = 0
                x = bounds.x
                y = y + ch
            else
                x = x + cw
            end
        end
    end
    
    -- Layout save slot buttons
    gridLayout(&#123;x=x+ipad, y=y+vpad+ipad, w=mainW, h=mainH}, self.cols, self.saves)   
    gridLayout(&#123;x=x+ipad, y=y+h-vpad-qh-ipad*2, w=mainW, h=qh}, self.qcols, self.qsaves)

    -- Bottom buttons
    local bottomButtonPanel = gridPanel()
    bottomButtonPanel:setBounds(x, y+h-vpad, w, vpad)
    for _,b in ipairs&#123;self.okButton, self.cancelButton} do
        bottomButtonPanel:add(b)
    end
    bottomButtonPanel:pack(5)

    if self.topFade ~= nil then
        self.topFade:setBounds(x, y, w, vpad)
    end
    if self.bottomFade ~= nil then
        self.bottomFade:setBounds(x, y+math.ceil(h-vpad), w, vpad)
    end
end

function SaveLoadScreen:initQSaves()
    destroyValues(self.qsaves)
    self.qsaves = &#123;}
    for pass=1,2 do
        local defaultLabel = \"autosave\"
        local startSlot = Save.getAutoSaveSlot(1)
        local maxSlots = getAutoSaveSlots()
        if self.isSave then
            -- Don't allow manual saving into autosave slots
            maxSlots = 0
        end
        
        if pass == 2 then
            startSlot = Save.getQuickSaveSlot(1)
            maxSlots = 1
            defaultLabel = \"quicksave\"
        end

        local saved = Save.getSaves(startSlot, maxSlots)       
        for slot=startSlot,startSlot+maxSlots-1 do
            local i = slot - startSlot + 1
            local label = \"Empty\n\" .. defaultLabel .. \" \" .. i
            local empty = true
            
            local si = saved[slot]
            if si ~= nil then
                label = defaultLabel .. \" \" .. i --.. \"\n\" .. si:getDateString()
                empty = false
            end
            
            local ss = self.newSaveSlot&#123;slot=slot, label=label, isEmpty=empty,
                isSave=self.isSave, isNew=false, compact=true}
            table.insert(self.qsaves, ss)
        end
    end
end

function SaveLoadScreen:getPage()
    return self.page
end

function SaveLoadScreen:setPage(p, force)
    for i,pb in ipairs(self.pageButtons) do
        pb:setSelected(i == p) 
    end

    if self.page ~= p or force then
        self.page = p               
        
        --Destroy old slots
        destroyValues(self.saves);
        self.saves = &#123;}             
        
        --Create new slots
        local slotsPerPage = self.rows * self.cols
        local pageStart = 1 + (p - 1) * slotsPerPage
        local pageEnd   = 1 + (p    ) * slotsPerPage
        local saved = Save.getSaves(pageStart, pageEnd) 
        local lastSaved = getSharedGlobal(KEY_SAVE_LAST)
        
        for i=pageStart,pageEnd-1 do
            local slot = i
            local screenshot = nil
            local label = \"Empty \" .. slot
            local empty = true
            local new = false
            
            local si = saved[i]
            if si ~= nil then
                slot = si:getSlot()
                screenshot = si:getThumbnail(self.screenshotWidth, self.screenshotHeight)
                label = \"Save \" .. slot -- .. \"\n\" .. si:getDateString()
                empty = false
                new = (lastSaved == i)
            end
            
            local ss = self.newSaveSlot&#123;slot=slot, label=label, isEmpty=empty, screenshot=screenshot,
                isSave=self.isSave, isNew=new}
            table.insert(self.saves, ss)
        end
        
        if self.selected < pageStart or self.selected >= pageEnd then
            self.selected = 0
        end
        self:setSelected(self.selected)
        
        self:layout()
    end
end

function SaveLoadScreen:setSelected(s)
    self.selected = s
    for _,save in ipairs(self.saves) do
        save.button:setSelected(save.slot == s)
    end
    for _,save in ipairs(self.qsaves) do
        save.button:setSelected(save.slot == s)
    end
end

function SaveLoadScreen:show()
    self:layout()

    while not Input.consume(VKeys.cancel) do    
        for i,pb in ipairs(self.pageButtons) do
            if pb:consumePress() then
                self:setPage(i)
            end
        end
        for _,save in ipairs(self.saves) do
            if save.button:consumePress() then
                self:setSelected(save.slot)
                break
            end
        end
        for _,save in ipairs(self.qsaves) do
            if save.button:consumePress() then
                self:setSelected(save.slot)
                break
            end
        end

        self.okButton:setEnabled(self.selected ~= 0)
        if self.okButton:consumePress() then
            break
        elseif self.cancelButton:consumePress() then
            self.selected = 0
            break
        end

        yield()
    end

    setSharedGlobal(KEY_SAVE_PAGE, self:getPage())

    return self.selected, self.metaData
end

registerSaveScreen(function()
    return SaveLoadScreen.new&#123;isSave=true}
end)

registerLoadScreen(function()
    return SaveLoadScreen.new&#123;isSave=false}
end)
" %}
                