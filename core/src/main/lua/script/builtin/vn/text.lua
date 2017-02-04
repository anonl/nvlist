
---Functions related to text display.
-- 
module("vn.text", package.seeall)

-- Local functions shared between sections
--------------------------------------------------------------------------------------------------------------

local function getSpeakerState()
    if context.speakerState == nil then
        context.speakerState = {
            name = nil,       -- Speaker's name (styled text)
            textStyle = nil,  -- Default text style for this speaker
            resetEOL = false, -- Speaker state should be reset at the end of the line
            changed = false   -- Speaker was modified since it was last passed to the textbox
        }
    end
    return context.speakerState
end

local function getLineState()
    if context.lineState == nil then
        context.lineState = {
            read = true,
            style = nil
        }
    end
    return context.lineState
end

local function hideSpeaker()
    local textBox = getTextBox()
    if textBox ~= nil then
        textBox:setSpeaker(nil)
    end
end

local function updateSpeaker()
    local textBox = getTextBox()
    if textBox == nil then
        return
    end
    
    local currentSpeaker = getSpeakerState()
    if not currentSpeaker.changed then
        return
    end
    
    local speakerShown = textBox:setSpeaker(currentSpeaker.name)
    if currentSpeaker.name == nil or currentSpeaker.name == "" then
        -- Omit name tags for nameless (narrator) speaker
        return
    end
    
    if speakerShown then
        appendTextLog(Text.format("[{}]\n", currentSpeaker.name))
    else
        if isTextModeADV() then
            appendText(Text.format("[{}]", currentSpeaker.name))
        else
            appendText(Text.format("[{}]\n", currentSpeaker.name))
        end                
    end
end

---Global declarations
-------------------------------------------------------------------------------------------------------------- @section globals

---Text modes. These determine the way the textbox looks.
TextMode = {
    ADV=1, --Adventure game style bottom-aligned text.
    NVL=2, --Novel style full screen text.
}

---Text functions
-------------------------------------------------------------------------------------------------------------- @section text

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

    local lineState = getLineState()
    local currentSpeaker = getSpeakerState()
    
    lineState.read = false
    if meta.filename ~= nil and meta.line >= 1 then
        lineState.read = Seen.hasSeenLine(meta.filename, meta.line)
    end

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
    
    updateSpeaker()    
    lineState.style = currentSpeaker.textStyle
    
    appendText(str)
    
    --Now wait until all text has faded in and execute triggers at appropriate times
    waitForTextVisible(getMainTextDrawable(), triggers)

    --Turn off skip mode if applicable
    if getSkipMode() == SkipMode.PARAGRAPH then
        stopSkipping()
    end
    
    --Wait for click
    waitClick()    
    
    --Reset speaker
    lineState.style = nil
    if currentSpeaker.resetEOL then
        say()
    end
    
    --Register line as read
    if meta.filename ~= nil and meta.line >= 1 then
        Seen.markLineSeen(meta.filename, meta.line)
    end
    lineState.read = true    
end

---Waits until the text in the main textbox (or other TextDrawable) has finished
-- appearing.
-- @tparam TextDrawable textDrawable An optional TextDrawable to wait for.
-- @tab[opt=nil] triggers An optional table containing functions that should be
--               called at specific text positions.
function waitForTextVisible(textDrawable, triggers)
    while textDrawable ~= nil and not textDrawable:isDestroyed() do
        if triggers ~= nil then
            local startGlyph = textDrawable:getGlyphOffset(textDrawable:getStartLine())
            local endGlyph = startGlyph + math.floor(textDrawable:getVisibleText())
            for i=startGlyph,endGlyph do
                if triggers[i] ~= nil then
                    triggers[i]()
                end
            end
        end
        
        if textDrawable:isFinalLineFullyVisible() then
            break
        elseif textDrawable:getVisibleText() >= textDrawable:getMaxVisibleText() then
            -- Not all remaining lines could be displayed, click to continue to the next text page            
            waitClick()

            local startLine = textDrawable:getStartLine()
            local endLine = textDrawable:getEndLine()
            local lineCount = textDrawable:getLineCount()

            textDrawable:setVisibleText(endLine, 0)
            if isSkipping() then
                textDrawable:setVisibleText(999999)
            end

            Log.debug("Show new text lines: {}-{} -> {}-{} (lineCount={})",
                    startLine, endLine, endLine, textDrawable:getEndLine(), lineCount)
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
    hideSpeaker()
end

---Appends text to the main textbox.
-- @param str The text to append (may be either a string or a StyledText
--        object).
-- @tab[opt=nil] meta A table with metadata for this piece of text (autoPage,
--      etc.)
function appendText(str, meta)
    meta = meta or {}

    local lineState = getLineState()
    
    local styled = Text.createStyledText(str, lineState.style)
    local logStyled = styled
    if lineState.read and prefs.textReadStyle ~= nil then
        styled = Text.createStyledText(str, Text.extendStyle(prefs.textReadStyle, lineState.style))
    end

    local textDrawable = getMainTextDrawable()
    local textState = getTextState()
    if textDrawable == nil then
        Log.info("No text drawable set, unable to display text")
        textState:appendText(styled)
        appendTextLog(logStyled)
        return
    end
    
    local oldText = textDrawable:getText()
    local oldLineCount = textDrawable:getLineCount()
    if oldLineCount > 0 then
        textState:appendText(styled)
        if meta.autoPage and textDrawable:getLineCount() > textDrawable:getEndLine() then
            textDrawable:setVisibleText(0)
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

    if isSkipping() then
        textDrawable:setVisibleText(999999)
    end
end

---Appends text to the textlog, but not the main textbox. Allows you to manually add lines to the textlog,
-- which can be useful if your VN has text that's not displayed in the main textbox.
-- @param str The text to append (may be either a string or a StyledText object).
-- @bool[opt=false] newPage If <code>true</code>, starts a new page in the textlog before appending the text.
function appendTextLog(str, newPage)
    local textLog = getTextState():getTextLog()
    if newPage then
        textLog:setText(Text.createStyledText(""))
    end
    textLog:appendText(Text.createStyledText(str))
end

function isLineRead()
    local lineState = getLineState()
    return lineState.read
end

function createStyledText(text, style)
    deprecated("4.0")

    return Text.createStyledText(text or "", style)
end

function createStyle(style)
    deprecated("4.0")

    return Text.createStyle(style)
end

---Stringifiers
-------------------------------------------------------------------------------------------------------------- @section stringifiers

--- id -> value/function
local stringifiers = {}

---Registers the specified function to be used whenever <code>id</code> needs to be stringified.
--
-- @string id The word to register a custom stringifier function for.
-- @func func A function that returns a string or StyledText object.
function registerStringifier(id, func)
    stringifiers[id] = func
end

---Gets called during execution of a text line to replace words starting with a dollar sign. If a stringify
-- handler function is registered for the word, that function is evaluated. Otherwise, if <code>word</code> is
-- a valid variable in the local context, its value is converted to a string representation.
-- 
-- @param word The characters following the dollar sign
-- @param level The relative level to search for local variables, depends on the depth of the call tree before
--     stringify is called.
function stringify(word, level)
    level = level or 3

    local value = stringifiers[word]    
    if value == nil then
        value = getDeepField(getLocalVars(level + 1), word) or getDeepField(getfenv(level), word)
    end
    
    --Evaluate functions fully
    while type(value) == "function" do
        value = value()
    end
    
    if value ~= nil then
        --Convert value to StyledText
        value = Text.createStyledText(value)
    end
        
    --Don't append when nil or empty string
    if value == nil or value:length() == 0 then
        return
    end
    
    return value
end


---Text tags
-------------------------------------------------------------------------------------------------------------- @section textTags

---Tag id -> function
local tagHandlers = {}

if Text ~= nil and not prefs.vnds then
    Text.registerBasicTagHandlers(tagHandlers)
end

---Registers text tag handler functions (open/close) for a specific text tag.
function registerTextTagHandler(tag, openFunc, closeFunc)
    tagHandlers[tag] = openFunc
    tagHandlers["/" .. tag] = closeFunc
end

---Gets called when an open tag is encountered within text.
function textTagOpen(tag, values, level)
    values = values or {}
    level = level or 3

    local func = tagHandlers[tag]
    if func == nil then
        return
    end
    
    --Resolve argument strings to their proper types/values
    local newValues = {}
    for k,v in pairs(values) do
        v = getDeepField(getLocalVars(level + 1), v)
          or getDeepField(getfenv(level), v)
          or Text.parseLuaLiteral(v)
        newValues[k] = v
    end

    return func(tag, newValues)
end

---Gets called whenever a close tag is encountered within text.
function textTagClose(tag)
    tag = tag or ""
    local func = tagHandlers["/" .. tag]
    if func == nil then
        return
    end
    return func(tag, values)
end

---Speakers
-------------------------------------------------------------------------------------------------------------- @section speakers

---Changes the name of currently speaking character.
-- @string name The character's display name. May be either an unstyled string, or styled text.
-- @tparam[opt=nil] TextStyle textStyle Default text style to use for text added
--        to the main textbox while this character is speaking.
function say(name, textStyle)
    local currentSpeaker = getSpeakerState()
    currentSpeaker.name = name
    currentSpeaker.textStyle = textStyle
    currentSpeaker.resetEOL = false
    currentSpeaker.changed = true
end

---Like <code>say</code>, but resets the speaking character at the end of the current paragraph.
-- @see say
function sayLine(...)
    local result = say(...)
    local currentSpeaker = getSpeakerState()
    currentSpeaker.resetEOL = true
    return result
end

---Registers a stringifier function to replace occurrences of $<code>id</code> with a call to
-- <code>sayLine</code>.
-- <br/>
-- Example use: <code>registerSpeaker("bal", "Balthasar")
-- $bal This line is now said with my name, Balthasar.</code>
-- @string id Unique identifier string for the speaker.
-- @param ... All other parameters are passed to <code>sayLine</code>.
function registerSpeaker(id, ...)
    local args = getTableOrVarArg(...)
    registerStringifier(id, function()
        return sayLine(unpack(args))
    end)
end

---Text mode
-------------------------------------------------------------------------------------------------------------- @section textmode

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
    if context.textMode ~= mode then
        context.textMode = mode

        setActiveTextBox(mode)
    end
end

---Returns the current text mode.
function getTextMode()
    return context.textMode or TextMode.ADV
end

--- @return <code>true</code> if the text mode is NVL
function isTextModeNVL()
    return getTextMode() == TextMode.NVL
end

--- @return <code>true</code> if the text mode is NVL
function isTextModeADV()
    return getTextMode() == TextMode.ADV
end

-- -----------------------------------------------------------------------------------------------------------
-- -----------------------------------------------------------------------------------------------------------
-- -----------------------------------------------------------------------------------------------------------
