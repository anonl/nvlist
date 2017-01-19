---Functions to show and manipulate images on the screen.
--
module("vn.image", package.seeall)

-- ----------------------------------------------------------------------------
--  Variables
-- ----------------------------------------------------------------------------

-- ----------------------------------------------------------------------------
--  Functions
-- ----------------------------------------------------------------------------

---Returns the <code>x, y, z</code> of the specified sprite slot <code>(lc, l, c, rc, r)</code>
-- @param i The image to position
-- @param slot The sprite slot (a string)
-- @param y The baseline y (sprite bottom y-coordinate)
-- @return Three values, the natural <code>x, y, z</code> for the image in the specified sprite slot.
local function getSpriteSlotPosition(i, slot, y)
    local x = 0
    local y = y or screenHeight - i:getHeight()
    local z = 0

    if slot == "l" then
        x = screenWidth*1/5
        z = 1
    elseif slot == "lc" then
        x = screenWidth*1/3
        z = 2
    elseif slot == "c" then
        x = screenWidth*1/2
    elseif slot == "rc" then
        x = screenWidth*2/3
        z = 2
    elseif slot == "r" then
        x = screenWidth*4/5
        z = 1
    end

    x = x - i:getWidth() / 2
    return x, y, z
end

---Creates an image and adds it to the current image layer.
-- @param tex A texture object or a path to a valid image file (relative to <code>res/img</code>).
-- @param x Can be either a string or a number. If it's a number, it specifies the leftmost x-coordinate of
--        the image. If it's a string, it can be one of:<br/>
--        <ul>
--          <li>l</li>
--          <li>lc</li>
--          <li>c</li>
--          <li>rc</li>
--          <li>r</li>
--        </ul>
--        These refer to predefined sprite positions from left to right.
-- @param y The desired y-coordinate of the bottom of the sprite.
-- @param properties Optional argument containing a table containing initial values for the new image's
--        properties.
-- @treturn ImageDrawable The newly created image.
function img(tex, x, y, properties)
    if type(x) == "table" then
        properties = x
    elseif type(y) == "table" then
        properties = y
    end

    local i = Image.createImage(getActiveLayer(), tex)

    if type(x) == "string" then
        local z = 0
        x, y, z = getSpriteSlotPosition(i, x, y)
        i:setZ(z)
    end
    if type(x) == "number" and type(y) == "number" then
        i:setPos(x, y)
    end

    --Handle properties given in a table
    setProperties(i, properties)

    return i
end

---Like <code>img</code>, gradually fades in the new image instead of instantly displaying it.
-- @param tex A texture object or a path to a valid image file (relative to <code>res/img</code>).
-- @param x Can be either a string or a number. If it's a number, it specifies the leftmost x-coordinate of
--        the image. If it's a string, it can be one of:<br/>
--        <ul>
--          <li>l</li>
--          <li>lc</li>
--          <li>c</li>
--          <li>rc</li>
--          <li>r</li>
--        </ul>
--        These refer to predefined sprite positions from left to right.
-- @param y The desired y-coordinate of the bottom of the sprite.
-- @param properties Optional argument containing a table with overrides for the new image's properties.
-- @treturn ImageDrawable The newly created image.
-- @see img
function imgf(tex, x, y, properties)
    local i = img(tex, x, y, properties)
    i:setAlpha(0)
    fadeTo(i, 1)
    return i
end

---Destroys an image or drawable, removing it from the screen.
-- @param image The image to remove.
function rm(image)
    if image ~= nil and not image:isDestroyed() then
        image:destroy()
    end
end

---Like <code>rm</code>, but fades out the image gradually before destroying it.
-- @param image The image to remove.
-- @number fadeTimeFrames The duration of the fade-out effect in frames.
-- @see rm
function rmf(image, fadeTimeFrames)
    fadeTo(image, 0, fadeTimeFrames)
    rm(image)
end

---Changes the current background image.
-- @param tex A texture object or a path to a valid image file (relative to <code>res/img</code>).
-- @tab properties Optional argument containing a table with overrides for the new image's properties.
-- @treturn ImageDrawable The new background image.
function bg(tex, properties)
    local background = getBackground()
    if background ~= nil and not background:isDestroyed() then
        background:destroy()
    end

    properties = extend({z=30000}, properties or {})
    background = img(tex, properties)

    context.background = background
    return background
end

---Like <code>bg</code>, but gradually fades to the new background instead of
-- instantly changing it.
-- @param tex A texture object or a path to a valid image file (relative to
--        <code>res/img</code>).
-- @number[opt=30] fadeTimeFrames The duration of the fade-out effect in frames.
-- @tab[opt={}] properties Optional argument containing a table with overrides
--              for the new image's properties.
-- @treturn ImageDrawable The new background image.
-- @see bg
function bgf(tex, fadeTimeFrames, properties)
    fadeTimeFrames = fadeTimeFrames or 30

    local background = getBackground()
    if background == nil or background:isDestroyed() then
        background = bg(tex, properties)
        if fadeTimeFrames > 0 then
            background:setAlpha(0)
            fadeTo(background, 1, fadeTimeFrames)
        end
    else
        local newbg = img(tex, properties)
        if fadeTimeFrames > 0 then
            newbg:setAlpha(0)
            newbg:setZ(background:getZ() - 1)
            fadeTo(newbg, 1, fadeTimeFrames)
        end
        newbg:setZ(background:getZ())
        background:destroy()
        background = newbg
    end

    context.background = background
    return background
end

---Returns the current background image.
-- @treturn ImageDrawable The current background image, or <code>nil</code> if
--          no background image currently exists.
function getBackground()
    local imageLayer = getActiveLayer()
    if imageLayer == nil or not imageLayer:contains(context.background) then
        context.background = nil
    end
    return context.background
end

---Replaces the current background with the <code>bg</code>.
-- @tparam ImageDrawable bg The new background image.
function setBackground(bg)
    local old = context.background
    if old ~= nil and old ~= bg then
        rmbg()
    end
    context.background = bg
end

---Removes and destroys the background image previously created with
-- <code>bg</code>.
function rmbg()
    local bg = getBackground()
    context.background = nil
    if bg == nil then
        return
    end
    return rm(bg)
end

---Like <code>rmbg</code>, but fades out the background image gradually before
-- destroying it.
-- @number fadeTimeFrames The duration of the fade-out effect in frames.
-- @see rmbg
function rmbgf(fadeTimeFrames)
    local bg = getBackground()
    context.background = nil
    if bg == nil then
        return
    end
    return rmf(bg, fadeTimeFrames)
end

---Creates a texture object from an image file.
-- @param filename The path to a valid image file (relative to
--        <code>res/img</code>). When a texture is passed instead of a filename,
--        the function will just return that texture.
-- @bool[opt=false] suppressErrors If <code>true</code> suppresses any errors
--                  that occur during loading.
-- @treturn Texture The created Texture object, or <code>nil</code> if something
--          went wrong.
function tex(filename, suppressErrors)
    if type(filename) == "string" then
        return Image.getTexture(filename, suppressErrors)
    end
    return filename
end

---Creates a texture object with the specified color.
-- @param argb The ARGB color packed into a single int (<code>0xFFFF0000</code>
--        is red, <code>0xFF0000FF</code> is blue, etc.)
-- @treturn Texture A new texture (w,h) with all pixels colored
--          <code>argb</code>.
function colorTex(argb)
    return Image.getColorTexture(argb)
end

---Creates a new layer.
-- @tparam Layer parentLayer The parent layer for the new layer.
-- @treturn Layer The newly created layer.
function createLayer(parentLayer)
    parentLayer = parentLayer or getActiveLayer()
    return Image.createLayer(parentLayer)
end

---Takes a screenshot to be used later (usually to create an ImageDrawable by
-- passing the screenshot to <code>img</code>).
-- @tparam Layer layer The layer in which to take the screenshot. Any layers
--        underneath it will be visible in the screenshot. Passing
--        <code>nil</code> for this parameter takes a screenshot of all layers.
-- @int[opt=-999] z The z-index in the selected layer to take the screenshot at.
-- @bool[opt=true] clip If <code>false</code>, ignores the layer's clipping
--                 bounds.
-- @bool[opt=false] volatile Allow optimizations which may cause the
--                  screenshot's pixels to disappear at any time.
-- @treturn Screenshot A screenshot object to be used as an argument for the
--          <code>img</code> function later.
function screenshot(layer, z, clip, volatile)
    local ss = nil
    while ss == nil do
        ss = Image.screenshot(layer, z, clip, volatile)
        while not ss:isAvailable() and not ss:isFailed() do
            Log.debug("Waiting for screenshot to become available: {}", ss)
            yield()
        end

        if not ss:isAvailable() then
            ss = nil
        end
    end
    return ss
end

---Takes a screenshot and makes an image out of it. Very useful for creating
-- complex fade effects by making it possible to fade out the entire screen
-- as a single image.
-- @tparam Layer layer The layer in which to take the screenshot. Any layers
--        underneath it will be visible in the screenshot. Passing
--        <code>nil</code> for this parameter takes a screenshot of all layers.
-- @int[opt=-999] z The z-index in the selected layer to take the screenshot at.
-- @bool[opt=true] clip If <code>false</code>, ignores the layer's clipping
--                 bounds.
-- @bool[opt=false] volatile Allow optimizations which may cause the
--                  screenshot's pixels to disappear at any time.
-- @treturn ImageDrawable The image created from the screenshot.
function screen2image(layer, z, clip, volatile)
    layer = layer or getActiveLayer()
    z = z or -999

    local i = Image.createImage(layer, screenshot(layer, z, clip, volatile))
    i:setZ(z + 1)
    return i
end

---Gradually changes the alpha of <code>i</code> to <code>targetAlpha</code>.
-- @tparam Drawable i The image to change the alpha of.
-- @number targetAlpha The end alpha for <code>i</code>.
-- @number durationFrames The duration of the movement in frames (gets
--         multiplied with <code>getEffectSpeed()</code> internally)
function fadeTo(i, targetAlpha, durationFrames)
    durationFrames = durationFrames or 20

    local startAlpha = i:getAlpha()
    local frame = 1
    while frame + getEffectSpeed() <= durationFrames do
        local f = frame / durationFrames
        i:setAlpha(startAlpha + (targetAlpha - startAlpha) * f)
        frame = frame + getEffectSpeed()
        yield()
    end

    i:setAlpha(targetAlpha)
end

---Gradually moves <code>i</code> to <code>(x, y)</code>.
-- @tparam Drawable i The image to move.
-- @number x The end x-position for <code>i</code>
-- @number y The end y-position for <code>i</code>
-- @number durationFrames The duration of the movement in frames (gets
--         multiplied with <code>getEffectSpeed()</code> internally)
-- @tparam Interpolator interpolator A function or interpolator object mapping
--         an input in the range <code>(0, 1)</code> to an output in the range
--         <code>(0, 1)</code>.
function translateTo(i, x, y, durationFrames, interpolator)
    x = x or i:getX()
    y = y or i:getY()
    durationFrames = durationFrames or 60
    
    if interpolator == nil then
        interpolator = Interpolators.SMOOTH
    else
        interpolator = Interpolators.get(interpolator)
    end

    local startX = i:getX()
    local startY = i:getY()

    local frame = 1
    while not i:isDestroyed() and frame + getEffectSpeed() <= durationFrames do
        local f = interpolator:remap(frame / durationFrames)
        i:setPos(startX + (x-startX) * f, startY + (y-startY) * f)
        frame = frame + getEffectSpeed()
        yield()
    end
    i:setPos(x, y)
end

---Gradually moves <code>i</code> by <code>(dx, dy)</code>, relative to its current position.
-- @tparam Drawable i The image to move.
-- @number dx The end x-position for <code>i</code>
-- @number dy The end y-position for <code>i</code>
-- @number durationFrames The duration of the movement in frames (gets multiplied with
--         <code>getEffectSpeed()</code> internally)
-- @tparam Interpolator interpolator A function or interpolator object mapping an input in the range
--         <code>(0, 1)</code> to an output in the range <code>(0, 1)</code>.
function translateRelative(i, dx, dy, durationFrames, interpolator)
    if i == nil then
        i = {}
    elseif type(i) ~= "table" then
        i = {i}
    end
    dx = dx or 0
    dy = dy or 0

    local threads = {}
    for _,d in pairs(i) do
        table.insert(threads, newThread(translateTo, d, d:getX()+dx, d:getY()+dy, durationFrames, interpolator))
    end
    join(threads)
end

---Asks NVList to preload one or more images. In most cases NVList does a pretty
-- good job preloading images by itself, but in rare cases a little helpcan
-- improve performance.
-- tparam string ... Any number of filenames of images to preload.
function preload(...)
    return Image.preload(...)
end

---Returns the root layer which (recursively) contains all other layers.
-- @treturn Layer The root layer.
function getRootLayer()
    return Image.getRootLayer()
end

---Returns the current default layer for newly created images/drawables.
-- @treturn Layer The active image layer.
-- @see setActiveLayer
function getActiveLayer()
    return Image.getActiveLayer()
end

---Changes the current image layer. Functions that create Drawables such as <code>img</code> typically create
-- them in the image layer.
-- @tparam Layer layer The layer to use as image layer.
-- @see getActiveLayer
function setActiveLayer(layer)
    Image.setActiveLayer(layer)
end


--- Use getActiveLayer() instead
function getImageLayer()
    deprecated("4.0")

    return getActiveLayer()
end

--- Use setActiveLayer() instead
function setImageLayer(layer)
    deprecated("4.0")

    return setActiveLayer(layer)
end