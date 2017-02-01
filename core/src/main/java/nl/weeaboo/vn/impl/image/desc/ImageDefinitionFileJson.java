package nl.weeaboo.vn.impl.image.desc;

final class ImageDefinitionFileJson {

    public String version;
    public ImageDefinitionJson[] images;

    static final class ImageDefinitionJson {

        public String file;
        public int width;
        public int height;
        public String minFilter;
        public String magFilter;
        public String wrapX;
        public String wrapY;

        public ImageSubRectJson[] subRects;

    }

    static final class ImageSubRectJson {

        public String id;
        public int[] rect;

    }

}
