package nl.weeaboo.vn.impl.image.desc;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import nl.weeaboo.common.Area;
import nl.weeaboo.common.Dim;
import nl.weeaboo.filesystem.FileCollectOptions;
import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.filesystem.FileSystemUtil;
import nl.weeaboo.filesystem.IFileSystem;
import nl.weeaboo.vn.image.desc.GLScaleFilter;
import nl.weeaboo.vn.image.desc.GLTilingMode;
import nl.weeaboo.vn.image.desc.IImageDefinition;
import nl.weeaboo.vn.image.desc.IImageSubRect;
import nl.weeaboo.vn.impl.image.desc.ImageDefinitionFileJson.ImageDefinitionJson;
import nl.weeaboo.vn.impl.image.desc.ImageDefinitionFileJson.ImageSubRectJson;
import nl.weeaboo.vn.impl.save.JsonUtil;
import nl.weeaboo.vn.save.SaveFormatException;

/** Helper class for reading/writing {@link ImageDefinition} objects */
public final class ImageDefinitionIO {

    private static final Logger LOG = LoggerFactory.getLogger(ImageDefinitionIO.class);
    private static final String VERSION = "1.0";

    private ImageDefinitionIO() {
    }

    /**
     * Searches the given file system for all img.json files and deserializes them, returning the combined result.
     *
     * @throws IOException If an I/O error occurs while trying to read the files.
     * @throws SaveFormatException If one of the JSON files can't be parsed.
     */
    public static Map<FilePath, IImageDefinition> fromFileSystem(IFileSystem fileSystem, FilePath rootFolder)
            throws IOException, SaveFormatException {

        Map<FilePath, IImageDefinition> result = Maps.newHashMap();
        for (FilePath folder : getFolders(fileSystem, rootFolder)) {
            FilePath path = folder.resolve(IImageDefinition.IMG_DEF_FILE);
            if (!fileSystem.getFileExists(path)) {
                continue;
            }

            for (ImageDefinition imageDef : deserialize(FileSystemUtil.readString(fileSystem, path))) {
                FilePath relPath = folder.resolve(imageDef.getFilename());
                result.put(relPath, imageDef);
            }
        }
        return result;
    }

    private static Iterable<FilePath> getFolders(IFileSystem fileSystem, FilePath rootFolder) throws IOException {
        return Iterables.concat(ImmutableList.of(rootFolder),
                fileSystem.getFiles(FileCollectOptions.folders(rootFolder)));
    }

    /**
     * Serializes the given image definitions to JSON.
     *
     * @see #deserialize(String)
     */
    public static String serialize(Collection<? extends IImageDefinition> imageDefs) {
        ImageDefinitionFileJson fileJson = new ImageDefinitionFileJson();
        fileJson.version = VERSION;
        fileJson.images = new ImageDefinitionJson[imageDefs.size()];
        int t = 0;
        for (IImageDefinition imageDef : imageDefs) {
            fileJson.images[t++] = encodeJson(imageDef);
        }
        return JsonUtil.toJson(fileJson);
    }

    /**
     * Deserializes image definitions from JSON.
     *
     * @throws SaveFormatException If the JSON data has an incompatible version number.
     * @see #serialize(Collection)
     */
    public static Collection<ImageDefinition> deserialize(String string) throws SaveFormatException {
        ImageDefinitionFileJson fileJson = JsonUtil.fromJson(ImageDefinitionFileJson.class, string);
        if (!VERSION.equals(fileJson.version)) {
            throw new SaveFormatException("Expected " + VERSION + ", was " + fileJson.version);
        }

        List<ImageDefinition> result = Lists.newArrayList();
        for (ImageDefinitionJson imageDefJson : fileJson.images) {
            try {
                result.add(decodeJson(imageDefJson));
            } catch (RuntimeException re) {
                LOG.error("Invalid image definition: {}", imageDefJson.file, re);
            }
        }
        return result;
    }

    private static ImageDefinitionJson encodeJson(IImageDefinition imageDef) {
        ImageDefinitionJson imageDefJson = new ImageDefinitionJson();
        imageDefJson.file = imageDef.getFilename();
        imageDefJson.width = imageDef.getSize().w;
        imageDefJson.height = imageDef.getSize().h;
        imageDefJson.minFilter = imageDef.getMinifyFilter().toString();
        imageDefJson.magFilter = imageDef.getMagnifyFilter().toString();
        imageDefJson.wrapX = imageDef.getTilingModeX().toString();
        imageDefJson.wrapY = imageDef.getTilingModeY().toString();

        List<ImageSubRectJson> subRects = Lists.newArrayList();
        for (IImageSubRect subRect : imageDef.getSubRects()) {
            subRects.add(encodeJson(subRect));
        }
        imageDefJson.subRects = subRects.toArray(new ImageSubRectJson[0]);

        return imageDefJson;
    }

    private static ImageSubRectJson encodeJson(IImageSubRect subRect) {
        ImageSubRectJson subRectJson = new ImageSubRectJson();
        subRectJson.id = subRect.getId();
        subRectJson.rect = encodeJson(subRect.getArea());
        return subRectJson;
    }

    private static int[] encodeJson(Area area) {
        return new int[] {area.x, area.y, area.w, area.h};
    }

    private static ImageDefinition decodeJson(ImageDefinitionJson imageDefJson) {
        Dim size = Dim.of(imageDefJson.width, imageDefJson.height);
        ImageDefinitionBuilder b = new ImageDefinitionBuilder(imageDefJson.file, size);

        b.setMinifyFilter(parseScaleFilter(imageDefJson.minFilter));
        b.setMagnifyFilter(parseScaleFilter(imageDefJson.magFilter));
        b.setTilingModeX(parseTilingMode(imageDefJson.wrapX));
        b.setTilingModeY(parseTilingMode(imageDefJson.wrapY));

        if (imageDefJson.subRects != null) {
            for (ImageSubRectJson subRectJson : imageDefJson.subRects) {
                b.addSubRect(parseSubRect(subRectJson));
            }
        }

        return b.build();
    }

    private static GLScaleFilter parseScaleFilter(String filterString) {
        if (Strings.isNullOrEmpty(filterString)) {
            return GLScaleFilter.DEFAULT;
        }
        return GLScaleFilter.fromString(filterString);
    }

    private static GLTilingMode parseTilingMode(String tilingModeString) {
        if (Strings.isNullOrEmpty(tilingModeString)) {
            return GLTilingMode.DEFAULT;
        }
        return GLTilingMode.fromString(tilingModeString);
    }

    private static ImageSubRect parseSubRect(ImageSubRectJson subRectJson) {
        return new ImageSubRect(subRectJson.id, parseArea(subRectJson.rect));
    }

    private static Area parseArea(int[] area) {
        return Area.of(area[0], area[1], area[2], area[3]);
    }

}
