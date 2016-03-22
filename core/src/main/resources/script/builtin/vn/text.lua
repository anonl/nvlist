
---Functions related to text display.
-- 
module("vn.text", package.seeall)

-- ----------------------------------------------------------------------------
--  Variables
-- ----------------------------------------------------------------------------

---Text modes. These determine the way the textbox looks.
TextMode = {
	ADV=1, --Adventure game style bottom-aligned text.
	NVL=2, --Novel style full screen text.
}

local textMode = 0
local speaker = {}

local lineState = {
	read = true,
	style = nil
}

-- ----------------------------------------------------------------------------
--  Local Functions
-- ----------------------------------------------------------------------------

local function getText()
	local textBox = getMainTextBox()
	if textBox == nil then
		return nil
	end
	return textBox:getText()
end

-- ----------------------------------------------------------------------------
--  Functions
-- ----------------------------------------------------------------------------

function getMainTextBox()
	return getTextState():getTextDrawable()
end

function setMainTextBox(textDrawable)
	getTextState():setTextDrawable(textDrawable)
end

-- ----------------------------------------------------------------------------

---Sets the current text of the main textbox.
-- @param str The new text (may be either a string or a StyledText object). Any
--        embedded stringifiers or text tags are evaluated unless
--        <code>meta.parse == false</code>.ua
-- @tab[opt=nil] triggers An optional table containing functions that should be
--               called at specific text positions.
-- @tab[opt=nil] meta A table with metadata for this line of text (filename,
--      line, etc.)
function text(str, triggers, meta)
	meta = meta or {}

	local textBox = getMainTextBox()
	
	--TODO: Update lineRead based on meta

	--Handle paragraph start differently based on text mode
	if isTextModeADV() then
		clearText()
	elseif isTextModeNVL() then
		local curText = getText()
		if curText ~= nil and curText:length() > 0 then
			local lastChar = curText:getChar(curText:length()-1)
			if lastChar ~= 0x20 and lastChar ~= 0x0A then			
				appendText("\n\n")
			end
		end	
	end
	
	--Parse str and execute stringifiers, text tag handlers, etc.
	if meta.parse == nil or meta.parse == true then
		str, triggers = Text.parseText(str, triggers)
	end
	
	lineState.style = speaker.textStyle
	
	appendText(str)
	
	--Now wait until all text has faded in and execute triggers at appropriate times
	waitForTextVisible(textBox, triggers)

	--Turn off skip mode if applicable
	if getSkipMode() == SkipMode.PARAGRAPH then
		setSkipMode(0)
	end
	
	--Wait for click
	waitClick()	
	
	--Reset speaker
	lineState.style = nil
	if speaker.resetEOL then
		say()
	end
	
	--Register line as read
	--if meta.filename ~= nil and meta.line >= 1 then
	--	seenLog:setTextLineRead(meta.filename, meta.line)
	--end
	lineState.read = true	
end

---Waits until the text in the main textbox (or other TextDrawable) has finished
-- appearing.
-- @tparam[opt=nil] TextDrawable textDrawable An optional TextDrawable to wait
--        for. If not specified, waits for the text in the main textbox.
-- @tab[opt=nil] triggers An optional table containing functions that should be
--               called at specific text positions.
function waitForTextVisible(textDrawable, triggers)
	textDrawable = textDrawable or getMainTextBox()
	
	local i0 = 0
	while textDrawable ~= nil and not textDrawable:isDestroyed() do
		if triggers ~= nil then
			local i1 = math.min(textDrawable:getMaxVisibleChars(), math.floor(textDrawable:getVisibleChars()))
			for i=i0,i1 do
--TODO: Add textdrawable's glyph offset (so we support startLine > 0 )
				if triggers[i] ~= nil then
					triggers[i]()
				end
			end
			i0 = i1 + 1
		end
		
		if textDrawable:getFinalLineFullyVisible() then
			break
		end
		
		yield()
	end
end

---Clears the text of the main textbox (effectively sets it to <code>""</code>).
-- In ADV mode, the text is cleared between each line of text. In NVL mode, you
-- need to call <code>clearText</code> manually.
function clearText()
    getTextState():setText("")
    appendTextLog("", true)
    hideSpeakerName()
end

---Appends text to the main textbox.
-- @param str The text to append (may be either a string or a StyledText
--        object).
-- @tab[opt=nil] meta A table with metadata for this piece of text (autoPage,
--      etc.)
function appendText(str, meta)
	local styled = nil
	local logStyled = nil
	if lineState.read and prefs.textReadStyle ~= nil then
		styled = Text.createStyledText(str, Text.extendStyle(prefs.textReadStyle, lineState.style))
		logStyled = Text.createStyledText(str, lineState.style)
	else
		styled = Text.createStyledText(str, lineState.style)
		logStyled = styled
	end

	local textBox = getMainTextBox()
	local textState = getTextState()
	if textBox == nil then
		textState:appendText(styled)
	    appendTextLog(logStyled)
		return
	end
	
	local oldText = textBox:getText()
	local oldLineCount = textBox:getLineCount()
	if oldLineCount > 0 then
		textState:appendText(styled)
		if meta.autoPage and textBox:getLineCount() > textBox:getEndLine() then
			textBox:setVisibleChars(0)
			local index = 0
			while index < styled:length() and styled:charAt(index) == 0x0A do
				index = index + 1
			end
			textState:setText(styled:substring(index))
			appendTextLog(logStyled:substring(index), true)
		else
			appendTextLog(logStyled)
		end
	else
		textState:appendText(styled)
    	appendTextLog(logStyled)
	end

	if quickRead then
		textBox:setVisibleChars(999999)
	end
end

---Changes the text mode to full-screen textbox mode
function setTextModeNVL()
	setTextMode(TextMode.NVL)
end

---Changes the text mode to bottom-aligned textbox mode
function setTextModeADV()
	setTextMode(TextMode.ADV)
end

---Changes the current text mode
function setTextMode(mode)
	--TODO: Change textbox based on the new mode
	textMode = mode
end

---Returns the current text mode.
function getTextMode()
	return textMode
end

--- @return <code>true</code> if the text mode is NVL
function isTextModeNVL()
	return getTextMode() == TextMode.NVL
end

--- @return <code>true</code> if the text mode is NVL
function isTextModeADV()
	return getTextMode() == TextMode.ADV
end
