
---User interface for changing global settings/preferences.
-- 
module("vn.settingsscreen", package.seeall)

local function nextDisplayMode()
    local m = System.getEnv():getDisplayMode()
    if m == DisplayMode.FULL_SCREEN then
        m = DisplayMode.WINDOWED
    else
        m = DisplayMode.FULL_SCREEN
    end
    return m
end

-------------------------------------------------------------------------------------------------------------- @section VolumeControl

local VolumeControl = {
    soundType=SoundType.MUSIC,
    panel=nil,
    label=nil,
    minusButton=nil,
    plusButton=nil,
    valueLabel=nil
    }

function VolumeControl.new(self)
    self = extend(VolumeControl, self)

    self.label = textimg()
    self.label:setText(self.soundType)
    self.label:setWidth(320)

    self.valueLabel = textimg()
    self.valueLabel:extendDefaultStyle(Text.createStyle{align="center"})

    self.minusButton = button("gui/settingsscreen#button-")
    self.minusButton:setText("-")
    Gui.setClickHandler(self.minusButton, function()
        self:adjustVolume(-.05)
    end)

    self.plusButton = button("gui/settingsscreen#button-")
    self.plusButton:setText("+")
    Gui.setClickHandler(self.plusButton, function()
        self:adjustVolume(.05)
    end)

    self.panel = gridPanel()
    self.panel:setColSpacing(50)
    self.panel:add(self.label)
    self.panel:add(self.minusButton)
    self.panel:add(self.valueLabel)
    self.panel:add(self.plusButton)
    self.panel:setSize(640, 50)

    self:onValueChanged()

    return self
end

function VolumeControl:onValueChanged()
    local percent = math.floor(100 * Sound.getMasterVolume(self.soundType) + 0.5)
    self.valueLabel:setText(percent .. "%")
end

function VolumeControl:adjustVolume(change)
    Sound.setMasterVolume(self.soundType, Sound.getMasterVolume(self.soundType) + change)
    self:onValueChanged()
end

---Default settings screen
-------------------------------------------------------------------------------------------------------------- @section SettingsScreen

local SettingsScreen = {
    topPanel=nil,
    bottomPanel=nil,
    returnButton=nil
    }

function SettingsScreen.new(self)
    self = extend(SettingsScreen, self)

    self.topPanel = gridPanel()

    for _,v in pairs(SoundType) do
        local vc = VolumeControl.new{soundType = v}
        self.topPanel:add(vc.panel)
        self.topPanel:endRow()
    end

    local displayModeButton = button("gui/settingsscreen#button-")
    displayModeButton:setEnabled(System.getEnv():isDisplayModeSupported(DisplayMode.WINDOWED))
    displayModeButton:setText(System.getEnv():getDisplayMode())
    Gui.setClickHandler(displayModeButton, function()
        local mode = nextDisplayMode()
        System.setDisplayMode(mode)
        displayModeButton:setText(mode)
    end)
    self.topPanel:add(displayModeButton)
    self.topPanel:endRow()

    self.topPanel:setInsets(100, 100, 100, 100)
    self.topPanel:pack(8)

    self.returnButton = button("gui/settingsscreen#button-")
    self.returnButton:setText("Return")

    self.bottomPanel = gridPanel()
    self.bottomPanel:add(self.returnButton)
    self.bottomPanel:setInsets(100, 100, 100, 100)
    self.bottomPanel:pack(2)

    return self
end

function SettingsScreen:destroy()
end

function SettingsScreen:show()
    while not Input.consume(VKeys.cancel) and not self.returnButton:consumePress() do
        yield()
    end
end

---Global accessor functions
-------------------------------------------------------------------------------------------------------------- @section globals

---Shows the settings screen.
function settingsScreen()
    callInContext(function()
        local screen = SettingsScreen.new()
        screen:show()
        screen:destroy()
    end)
end
