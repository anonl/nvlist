
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

local function getDisplayModeName(mode)
    if mode == DisplayMode.FULL_SCREEN then
        return "Full-screen"
    elseif mode == DisplayMode.WINDOWED then
        return "Windowed"
    end
    return mode
end

local function getSoundTypeName(mode)
    if mode == SoundType.MUSIC then
        return "Music"
    elseif mode == SoundType.SOUND then
        return "SFX"
    elseif mode == SoundType.VOICE then
        return "Voice"
    end
    return mode
end

-------------------------------------------------------------------------------------------------------------- @section RangeControl


local RangeControl = {
    label=nil,
    minusButton=nil,
    plusButton=nil,
    valueLabel=nil
    }

function RangeControl.new(label, self)
    self = extend(RangeControl, self)

    self.label = textimg()
    self.label:setText(label)

    self.valueLabel = textimg()
    self.valueLabel:setMaxSize(180, 100)
    self.valueLabel:extendDefaultStyle(Text.createStyle{align="center"})

    self.minusButton = button("gui/button")
    self.minusButton:setText("-")
    self.minusButton:setSize(75, 75)
    Gui.setClickHandler(self.minusButton, function()
        self:adjustValue(-1)
    end)

    self.plusButton = button("gui/button")
    self.plusButton:setText("+")
    self.plusButton:setSize(75, 75)
    Gui.setClickHandler(self.plusButton, function()
        self:adjustValue(1)
    end)

    return self
end

function RangeControl:layout(x, y)
    self.label:setPos(x, y + 10)
    x = x + 180
    self.minusButton:setPos(x, y)
    x = x + 50
    self.valueLabel:setPos(x, y + 10)
    x = x + 160
    self.plusButton:setPos(x, y)
end

-------------------------------------------------------------------------------------------------------------- @section TextSpeedControl

local TextSpeedControl = {}

function TextSpeedControl.new(self)
    self = RangeControl.new("Text speed", extend(TextSpeedControl, self))
    self:onValueChanged()
    return self
end

function TextSpeedControl:onValueChanged()
    local charsPerSecond = math.floor(60 * Text.getTextSpeed() + 0.5)
    self.valueLabel:setText(charsPerSecond)
    self.minusButton:setEnabled(charsPerSecond > 1)
    self.plusButton:setEnabled(charsPerSecond < 100)
end

function TextSpeedControl:adjustValue(change)
    Text.setTextSpeed(Text.getTextSpeed() + change / 60)
    self:onValueChanged()
end

-------------------------------------------------------------------------------------------------------------- @section VolumeControl

local VolumeControl = {
    soundType=SoundType.MUSIC,
    }

function VolumeControl.new(self)
    self = RangeControl.new(getSoundTypeName(self.soundType), extend(VolumeControl, self))
    self:onValueChanged()
    return self
end

function VolumeControl:onValueChanged()
    local percent = math.floor(100 * Sound.getMasterVolume(self.soundType) + 0.5)
    self.valueLabel:setText(percent .. "%")
    self.minusButton:setEnabled(percent > 0)
    self.plusButton:setEnabled(percent < 100)
end

function VolumeControl:adjustValue(change)
    Sound.setMasterVolume(self.soundType, Sound.getMasterVolume(self.soundType) + change * .05)
    self:onValueChanged()
end

---Default settings screen
-------------------------------------------------------------------------------------------------------------- @section SettingsScreen

local SettingsScreen = {
    returnButton=nil
    }

function SettingsScreen.new(self)
    self = extend(SettingsScreen, self)

    local x = 400
    local y = 100

    local textSpeedControl = TextSpeedControl.new()
    textSpeedControl:layout(x, y)
    y = y + 75

    local volumeControls = {}
    for _,v in pairs(SoundType) do
        local vc = VolumeControl.new{soundType = v}
        vc:layout(x, y)
        y = y + 75
        table.insert(volumeControls, vc)
    end

    y = y + 25

    local dmLabel = textimg("Display Mode")
    dmLabel:setPos(x, y + 10)

    local displayModeButton = button("gui/button")
    displayModeButton:setEnabled(System.getEnv():isDisplayModeSupported(DisplayMode.WINDOWED))
    displayModeButton:setText(getDisplayModeName(System.getEnv():getDisplayMode()))
    displayModeButton:setWidth(200)
    Gui.setClickHandler(displayModeButton, function()
        local mode = nextDisplayMode()
        System.setDisplayMode(mode)
        displayModeButton:setText(getDisplayModeName(mode))
    end)
    displayModeButton:setPos(x + 250, y)
    y = y + 50

    self.returnButton = button("gui/button")
    self.returnButton:setText("Return")
    self.returnButton:setWidth(200)
    self.returnButton:setPos((screenWidth - 200) / 2, screenHeight - 150)

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
