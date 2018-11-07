package nl.weeaboo.vn.impl.image.desc;

import javax.annotation.Nullable;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import nl.weeaboo.common.Area;
import nl.weeaboo.common.Dim;
import nl.weeaboo.test.ExceptionTester;
import nl.weeaboo.test.StringTester;
import nl.weeaboo.vn.image.desc.GLScaleFilter;
import nl.weeaboo.vn.image.desc.GLTilingMode;
import nl.weeaboo.vn.image.desc.IImageDefinition;

public final class ImageDefinitionTest {

    private static final String FILENAME = "myFilename";
    private static final Dim SIZE = Dim.of(10, 20);
    private static final GLScaleFilter MIN_FILTER = GLScaleFilter.LINEAR_MIPMAP;
    private static final GLScaleFilter MAG_FILTER = GLScaleFilter.NEAREST;
    private static final GLTilingMode WRAP_X = GLTilingMode.DEFAULT;
    private static final GLTilingMode WRAP_Y = GLTilingMode.CLAMP;
    private static final ImmutableList<ImageSubRect> SUB_RECTS = ImmutableList
            .of(new ImageSubRect("sub", Area.of(1, 2, 3, 4)));

    private final ExceptionTester exTester = new ExceptionTester();
    private final ImageDefinitionTestHelper tester = new ImageDefinitionTestHelper();

    /** Test the default attribute values when using the most basic constructor. */
    @Test
    public void testDefaultInstance() {
        ImageDefinition def = new ImageDefinition(FILENAME, SIZE);
        Assert.assertEquals(FILENAME, def.getFilename());
        Assert.assertEquals(SIZE, def.getSize());
        Assert.assertEquals(GLScaleFilter.DEFAULT, def.getMinifyFilter());
        Assert.assertEquals(GLScaleFilter.DEFAULT, def.getMagnifyFilter());
        Assert.assertEquals(GLTilingMode.DEFAULT, def.getTilingModeX());
        Assert.assertEquals(GLTilingMode.DEFAULT, def.getTilingModeY());
        Assert.assertEquals(0, def.getSubRects().size());
    }

    /**
     * The conversion constructor can convert from an arbitrary {@link IImageDefinition} to the concrete
     * immutable implementation ({@link ImageDefinition}).
     */
    @Test
    public void testConversionConstructor() {
        // Calling from() on a builder returns an immutable copy
        ImageDefinitionBuilder builder = new ImageDefinitionBuilder(FILENAME, SIZE);
        ImageDefinition fromBuilder = ImageDefinition.from(builder);
        Assert.assertNotSame(builder, fromBuilder);
        tester.assertEquals(builder, fromBuilder);

        // When passing in an already immutable implementation, the input argument is returned unmodified
        IImageDefinition def = builder.build();
        Assert.assertSame(def, ImageDefinition.from(def));
    }

    @Test
    public void testBuilder() {
        ImageDefinitionBuilder builder = new ImageDefinitionBuilder("otherFilename", Dim.of(3, 4));
        builder.setFilename(FILENAME);
        builder.setSize(SIZE);
        builder.setMinifyFilter(MIN_FILTER);
        builder.setMagnifyFilter(MAG_FILTER);
        builder.setTilingModeX(WRAP_X);
        builder.setTilingModeY(WRAP_Y);

        ImageSubRect tempRect = new ImageSubRect("willBeOverwritten", Area.of(0, 0, 1, 1));
        builder.addSubRect(tempRect);
        builder.clearSubRects();
        Assert.assertEquals(0, builder.getSubRects().size());
        builder.addSubRect(tempRect);
        builder.setSubRects(SUB_RECTS);

        assertBuilderResult(builder);

        ImageDefinition def = builder.build();
        assertBuilderResult(def);

        // Check that no information is lost when converting to/from the builder
        def = def.builder().build();
        assertBuilderResult(def);
    }

    @Test
    public void testToString() {
        ImageDefinition def = new ImageDefinition(FILENAME, SIZE);
        String string = def.toString();

        // The filename should be contained somewhere in the string representation
        StringTester.assertContains(string, FILENAME);
    }

    /**
     * The tiling wrap modes may only be used when the image dimensions are powers-of-two (this is an OpenGL
     * ES 2.0 limitation).
     */
    @Test
    public void testTilingConstraints() {
        ImageDefinitionBuilder builder = builder();
        builder.setTilingModeX(GLTilingMode.REPEAT);

        exTester.expect(IllegalArgumentException.class, () -> builder.build());

        builder.setTilingModeX(GLTilingMode.DEFAULT);
        builder.setTilingModeY(GLTilingMode.REPEAT);

        exTester.expect(IllegalArgumentException.class, () -> builder.build());

        // Set dimensions to powers-of-two -> now we can build the object without an exception
        builder.setSize(Dim.of(32, 32));
        builder.build();
    }

    /**
     * Lookup of sub-rects by their identifier.
     */
    @Test
    public void testFindSubRect() {
        ImageSubRect alpha = new ImageSubRect("alpha", Area.of(0, 0, 1, 1));
        ImageSubRect beta = new ImageSubRect("beta", Area.of(1, 0, 1, 1));

        ImageDefinitionBuilder builder = builder();
        builder.addSubRect(alpha);
        builder.addSubRect(beta);
        ImageDefinition def = builder.build();

        assertFindSubRect(builder, "alpha",   alpha);
        assertFindSubRect(def,     "alpha",   alpha);
        assertFindSubRect(builder, "beta",    beta);
        assertFindSubRect(def,     "beta",    beta);
        assertFindSubRect(builder, "missing", null);
        assertFindSubRect(def,     "missing", null);
    }

    private void assertFindSubRect(IImageDefinition def, String id, @Nullable ImageSubRect expected) {
        Assert.assertEquals(expected, def.findSubRect(id));
    }

    private ImageDefinitionBuilder builder() {
        return new ImageDefinitionBuilder(FILENAME, SIZE);
    }

    private void assertBuilderResult(IImageDefinition def) {
        Assert.assertEquals(FILENAME, def.getFilename());
        Assert.assertEquals(SIZE, def.getSize());
        Assert.assertEquals(MIN_FILTER, def.getMinifyFilter());
        Assert.assertEquals(MAG_FILTER, def.getMagnifyFilter());
        Assert.assertEquals(WRAP_X, def.getTilingModeX());
        Assert.assertEquals(WRAP_Y, def.getTilingModeY());
        tester.assertEquals(Iterables.getOnlyElement(SUB_RECTS),
                Iterables.getOnlyElement(def.getSubRects()));
    }

}
