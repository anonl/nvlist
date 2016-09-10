package nl.weeaboo.vn.image.impl;

import java.io.IOException;
import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.google.common.collect.ImmutableMap;

import nl.weeaboo.common.Area;
import nl.weeaboo.common.Checks;
import nl.weeaboo.common.Dim;
import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.filesystem.FileSystemView;
import nl.weeaboo.gdx.graphics.GdxTextureUtil;
import nl.weeaboo.gdx.res.GeneratedResourceStore;
import nl.weeaboo.gdx.res.IResource;
import nl.weeaboo.gdx.res.TransformedResource;
import nl.weeaboo.vn.core.ResourceId;
import nl.weeaboo.vn.core.impl.FileResourceLoader;
import nl.weeaboo.vn.core.impl.StaticEnvironment;
import nl.weeaboo.vn.core.impl.StaticRef;
import nl.weeaboo.vn.image.IImageModule;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.image.desc.IImageDefinition;
import nl.weeaboo.vn.image.desc.IImageSubRect;
import nl.weeaboo.vn.image.impl.desc.ImageDefinitionIO;
import nl.weeaboo.vn.render.RenderUtil;

/**
 * Does the heavy lifting related to texture loading and generation for {@link IImageModule}.
 */
final class TextureManager implements Serializable {

    private static final long serialVersionUID = ImageImpl.serialVersionUID;
    private static final Logger LOG = LoggerFactory.getLogger(TextureManager.class);

    private final StaticRef<GdxTextureStore> textureStore = StaticEnvironment.TEXTURE_STORE;
    private final StaticRef<GeneratedResourceStore> generatedTextureStore = StaticEnvironment.GENERATED_RESOURCES;

    private final FileResourceLoader resourceLoader;

    private transient ImmutableMap<FilePath, IImageDefinition> cachedImageDefs;

    public TextureManager(FileResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public void invalidateImageDefinitions() {
        cachedImageDefs = null;
    }

    private final IImageDefinition getImageDef(FilePath relPath) {
        if (cachedImageDefs == null) {
            try {
                FileSystemView fs = resourceLoader.getFileSystem();
                cachedImageDefs = ImmutableMap.copyOf(ImageDefinitionIO.fromFileSystem(fs, FilePath.empty()));
            } catch (IOException e) {
                LOG.warn("Error loading image definitions", e);
            }
        }
        return cachedImageDefs.get(relPath);
    }

    public ITexture getTexture(ResourceId resourceId, double sx, double sy) {
        FilePath relPath = resourceId.getFilePath();
        String subId = resourceId.getSubId();

        IResource<Texture> res = textureStore.get().get(resourceLoader.getAbsolutePath(relPath));
        if (res == null) {
            return null;
        }

        IImageDefinition imageDef = getImageDef(relPath);
        if (imageDef != null) {
            LOG.trace("Image definition found: {}", relPath);

            IImageSubRect subRect = imageDef.findSubRect(subId);
            if (subRect == null) {
                LOG.warn("Image definition sub-rect not found: {}#{}", relPath, subId);
            } else {
                LOG.debug("Load image sub-rect: {}#{}: {}", relPath, subId, subRect.getArea());
                return newTexture(new RegionResource(res, subRect.getArea()), sx, sy);
            }
        }
        LOG.trace("Image definition not found: {}", relPath);
        return newTexture(new RegionResource(res), sx, sy);
    }

    public IResource<TextureRegion> generateTextureRegion(IGdxTextureData texData) {
        GeneratedResourceStore generatedStore = generatedTextureStore.get();
        return new GeneratedRegionResource(generatedStore.register(texData));
    }

    public ITexture generateTexture(IGdxTextureData texData, double sx, double sy) {
        return newTexture(generateTextureRegion(texData), sx, sy);
    }

    public ITexture generateTexture(int colorARGB, Dim size, double sx, double sy) {
        // Create solid-colored pixmap texture data
        Pixmap pixmap = new Pixmap(size.w, size.h, Format.RGBA8888);
        pixmap.setColor(RenderUtil.toRGBA(colorARGB));
        pixmap.fill();
        PixelTextureData texData = PixelTextureData.fromPixmap(pixmap);

        return generateTexture(texData, sx, sy);
    }

    private static ITexture newTexture(IResource<TextureRegion> tr, double sx, double sy) {
        return new TextureAdapter(tr, sx, sy);
    }

    private static class RegionResource extends TransformedResource<Texture, TextureRegion> {

        private static final long serialVersionUID = 1L;

        /** May be null */
        private final Area subRect;

        public RegionResource(IResource<Texture> inner) {
            super(inner);

            this.subRect = null;
        }
        public RegionResource(IResource<Texture> inner, Area subRect) {
            super(inner);

            this.subRect = Checks.checkNotNull(subRect);
        }

        @Override
        protected TextureRegion transform(Texture original) {
            if (subRect == null) {
                return GdxTextureUtil.newGdxTextureRegion(original);
            } else {
                return GdxTextureUtil.newGdxTextureRegion(original, subRect);
            }
        }

    }

    private static class GeneratedRegionResource extends TransformedResource<IGdxTextureData, TextureRegion> {

        private static final long serialVersionUID = 1L;

        public GeneratedRegionResource(IResource<IGdxTextureData> inner) {
            super(inner);
        }

        @Override
        protected TextureRegion transform(IGdxTextureData original) {
            return original.toTextureRegion();
        }

    }

}
