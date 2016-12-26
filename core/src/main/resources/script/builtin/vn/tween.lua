---Defines some image transition functions (image 'tweens').
-- 
module("vn.tween", package.seeall)

-- Local functions shared between sections
--------------------------------------------------------------------------------------------------------------

local function doTween(image, tween, endTexture)
    tween:setEndTexture(endTexture)
    image:setRenderer(tween)
    while not image:isDestroyed() and not tween:isFinished() do
        yield()
    end
    image:setTexture(endTexture)
end

---Global declarations
-------------------------------------------------------------------------------------------------------------- @section globals

---Performs an animated transition from the current texture of <code>image</code> to a new texture,
-- <code>targetTexture</code>.
-- 
-- @tparam ImageDrawable image The image to change the texture of.
-- @param targetTexture A texture object or a path to a valid image file (relative to <code>/img</code>).
function imgtween(image, targetTexture)
    targetTexture = tex(targetTexture)
    crossFadeTween(image, targetTexture, 30)
    return image
end

---Convenience function for performing an <code>imgtween</code> on the current background image. In the case
-- of backgrounds, changing the background through <code>bgf</code> has roughly the same default effect. The
-- use of <code>bgtween</code> is mainly for cases when <code>imgtween</code> is overridden to do something
-- different or to keep changes to the ImageDrawable used for the background (<code>bgf</code> creates a new
-- ImageDrawable each time).
-- 
-- @param targetTexture A texture object or a path to a valid image file (relative to <code>/img</code>).
function bgtween(targetTexture)
    return imgtween(getBackground(), targetTexture)
end

---Crossfade
-------------------------------------------------------------------------------- @section Crossfade tween

---Changes an ImageDrawable's texture using a cross fade (dissolve) transition.
--
-- @tparam ImageDrawable image The image to tween.
-- @tparam Texture targetTexture The new texture for the image.
-- @number duration The duration of the fade in frames (will be multiplied by <code>effectSpeed</code>
--        internally).
-- @param interpolator A function or Interpolator object mapping an input in the range <code>(0, 1)</code> to
--        an output in the range <code>(0, 1)</code>.
function crossFadeTween(image, targetTexture, duration, interpolator)
    duration = duration or 30
    targetTexture = tex(targetTexture)

    local tween = Tween.crossFade(duration, interpolator)
    return doTween(image, tween, targetTexture)
end


---Bitmap tween
-------------------------------------------------------------------------------- @section Bitmap tween

---Changes an ImageDrawable's texture through a dissolve effect shaped by a grayscale bitmap.
--
-- @tparam ImageDrawable image The image to tween.
-- @tparam Texture targetTexture The new texture for the image.
-- @param controlImage The filename (relative to <code>/img</code>) of the bitmap that controls the shape of
--        the tween.
-- @number duration The duration of the tween in frames (will be multiplied by <code>effectSpeed</code>
--         internally).
-- @number[opt=0.5] range Determines the relative width of the fading region between <code>0.0</code> and
--                  <code>1.0</code>.
-- @param[opt=nil] interpolator A function or interpolator object mapping an input in the range
--                 <code>(0, 1)</code> to an output in the range <code>(0, 1)</code>.
function bitmapTween(image, targetTexture, controlImage, duration, range, interpolator)
    duration = duration or 60
    range = range or 0.5
    targetTexture = tex(targetTexture)

    local tween = Tween.bitmapTween(controlImage, duration, range, interpolator)
    return doTween(image, tween, targetTexture)
end

---Fades in an ImageDrawable's texture using a bitmap transition.
--
-- @tparam ImageDrawable image The image to tween.
-- @param controlImage The filename (relative to <code>/img</code>) of the bitmap that controls the shape of
--        the tween.
-- @number duration The duration of the tween in frames (will be multiplied by <code>effectSpeed</code>
--         internally).
-- @number[opt=0.5] range Determines the relative width of the fading region between <code>0.0</code> and
--                  <code>1.0</code>.
-- @param[opt=nil] interpolator A function or interpolator object mapping an input in the range
--                 <code>(0, 1)</code> to an output in the range <code>(0, 1)</code>.
-- @see bitmapTween
function bitmapTweenIn(image, controlImage, duration, range, interpolator)
    local tex = image:getTexture()
    image:setTexture(nil)
    return bitmapTween(image, tex, controlImage, duration, range, interpolator)
end

---Fades away an ImageDrawable's texture using a bitmap transition.
--
-- @tparam ImageDrawable image The image to tween.
-- @param controlImage The filename (relative to <code>/img</code>) of the bitmap that controls the shape of
--        the tween.
-- @number duration The duration of the tween in frames (will be multiplied by <code>effectSpeed</code>
--         internally).
-- @number[opt=0.5] range Determines the relative width of the fading region between <code>0.0</code> and
--                  <code>1.0</code>.
-- @param[opt=nil] interpolator A function or interpolator object mapping an input in the range
--                 <code>(0, 1)</code> to an output in the range <code>(0, 1)</code>.
-- @see bitmapTween
function bitmapTweenOut(image, controlImage, duration, range, interpolator)
    return bitmapTween(image, nil, controlImage, duration, range, interpolator)
end

-------------------------------------------------------------------------------- @section end
