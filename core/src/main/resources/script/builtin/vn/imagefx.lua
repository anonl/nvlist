--- Functionality for manipulating textures and images.
--  
module("vn.imagefx", package.seeall)

local function waitForTask(task)
    while not task:isAvailable() and not task:isFailed() do
        Log.debug("Waiting for async task to finish: {}", task)
        yield()
    end

    if not task:isAvailable() then
        Log.debug("Async task failed: {}", task)
        return nil
    end
    
    return task:getResult()
end

---Creates a cropped copy of the given input texture.
-- @tparam Texture t The source texture.
-- @int x The top-left x-coordinate of the crop rectangle.
-- @int y The top-left y-coordinate of the crop rectangle.
-- @int w The crop rectangle's width.
-- @int h The crop rectangle's height.
-- @treturn Texture A new texture created from the pixels within the crop
--          rectangle.
function crop(t, x, y, w, h)
    t = tex(t)
    return ImageFx.crop(t, x, y, w, h)
end

---Creates a blurred copy of a texture.
-- @tparam Texture t The texture to create a blurred copy of, or a filename
--         pointing to a valid image file.
-- @int kernelSize Determines the size of the blur kernel.
-- @int[default=2468] expandDirs Pad the image with extra whitespace in certain directions. This extra space
--                    is needed to avoid cropping the blur.
--                    <p>
--                    Each digit corresponds to a numpad direction (example: 268 is bottom+right+top).
-- @treturn Texture A blurred copy of the input texture.
function blur(t, kernelSize, borderExtend)
    t = tex(t)
    
    local task = ImageFx.blur(t, kernelSize, borderExtend)
    return waitForTask(task)
end


---Creates a brightened copy of a texture.
-- @tparam Texture t The texture to create a brightened copy of, or a filename
--         pointing to a valid image file.
-- @number add A fraction between <code>-1.0</code> and <code>1.0</code> that
--         will be added to the color components <code>(r,g,b)</code> of the
--         input texture.
-- @treturn Texture A brightened copy of the input texture.
-- @see #applyColorMatrix
function brighten(t, add)
    t = tex(t)
    local task = ImageFx.brighten(t, add)
    return waitForTask(task)
end


---Creates a copy of the given texture with a color matrix applied. Pseudocode:<br/>
-- <code>r' = rf[1]*r + rf[2]*g + rf[3]*b + rf[4]*a + rf[5]</code><br/>
-- This assumes unassociated alpha (non-premultiplied) and color values ranging between
-- <code>0.0</code> and <code>1.0</code>.
-- @tparam Texture t The input texture.
-- @tparam {number} rf A table containing the multiplication factors for the red channel.
-- @tparam {number} gf A table containing the multiplication factors for the green channel.
-- @tparam {number} bf A table containing the multiplication factors for the blue channel.
-- @tparam {number} af A table containing the multiplication factors for the alpha channel.
-- @treturn Texture A new texture with the color matrix applied.
function applyColorMatrix(t, rf, gf, bf, af)
    t = tex(t)
    local task = ImageFx.colorMatrix(t, rf, gf, bf, af)
    return waitForTask(task)
end

--[[

---Creates a series of progressively more blurred copies of an input texture.
-- @tparam Texture t The texture to create blurred copies of, or a filename
--         pointing to a valid image file.
-- @int levels The number of blur images to generate.
-- @int kernelSize Determines the size of the blur kernel.
-- @bool borderExtend Use <code>true</code> to pad the expanded image area
--       with the image's border pixels, or <code>false</code> to pad with
--       transparent pixels.<br/>
--       You can also use an integer to only extend in certain directions,
--       each digit corresponds to a numpad direction (example: 268 is
--       bottom-right-top).
-- @bool cropResult Use <code>true</code> to crop the result texture to the
--       same size as <code>tex</code>, otherwise the result texture will be
--       padded on each side equal to half the size of the blur kernel.
--       You can also use an integer to only crop in certain directions,
--       each digit corresponds to a numpad direction (example: 486 is
--       left-top-right).
-- @treturn {Texture} A table containing the blurred texture copies.
-- @see blur
function blurMultiple(t, levels, kernelSize, borderExtend, cropResult)
    t = tex(t)
    return ImageFx.blurMultiple(t, levels, kernelSize, borderExtend, cropResult)
end

---Creates a copy of the input texture downscaled by a factor
-- <code>2<sup>level</sup></code>.
-- @tparam Texture t The texture to create a downscaled copy of, or a filename
--         pointing to a valid image file.
-- @int level The mipmap level (amount of downscaling).
-- @treturn Texture A downscaled copy of the input texture.
function mipmap(t, level)
    t = tex(t)
    return ImageFx.mipmap(t, level)
end

---Creates a new texture by blending together multiple input textures.
-- @param args A table of the form:<br/>
-- <pre>{
--     {tex=myTexture1},
--     {tex=myTexture2, pos={10, 10}}
--     {tex=myTexture3}
-- }</pre><br/>
-- The <code>pos</code> field is optional and assumed <code>{0, 0}</code> when
-- omitted.
-- @int w Width of the output texture.
-- @int h Height of the output texture.
-- @treturn Texture A new texture composed of all input textures blended
--          together.
function composite(args, w, h)
    for _,entry in ipairs(args) do
        entry.tex = tex(entry.tex)
    end
    return ImageFx.composite(args, w, h)
end

]]
