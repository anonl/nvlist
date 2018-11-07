package nl.weeaboo.vn.impl.image.desc;

import org.junit.Assert;
import org.junit.Test;

import nl.weeaboo.common.Area;
import nl.weeaboo.test.ExceptionTester;
import nl.weeaboo.test.StringTester;
import nl.weeaboo.vn.image.desc.IImageSubRect;

public final class ImageSubRectTest {

    private final ExceptionTester exTester = new ExceptionTester();
    private final ImageDefinitionTestHelper tester = new ImageDefinitionTestHelper();

    /**
     * The conversion constructor can convert from an arbitrary {@link IImageSubRect} to the concrete
     * immutable implementation ({@link ImageSubRect}).
     */
    @Test
    public void testConversionConstructor() {
        // Calling from() on a mutable object returns an immutable copy
        ImageSubRectStub stub = new ImageSubRectStub();
        ImageSubRect fromStub = ImageSubRect.from(stub);
        Assert.assertNotSame(stub, fromStub);
        tester.assertEquals(stub, fromStub);

        // When passing in an already immutable implementation, the input argument is returned unmodified
        ImageSubRect rect = new ImageSubRect("test", Area.of(0, 0, 1, 1));
        Assert.assertSame(rect, ImageSubRect.from(rect));
    }

    /**
     * Sub-rects must have non-zero width/height.
     */
    @Test
    public void testInvalidArea() {
        // Zero width
        exTester.expect(IllegalArgumentException.class, () -> subRect(Area.of(0, 0, 0, 1)));
        // Zero height
        exTester.expect(IllegalArgumentException.class, () -> subRect(Area.of(0, 0, 1, 0)));
    }

    @Test
    public void testToString() {
        ImageSubRect rect = new ImageSubRect("myId", Area.of(0, 1, 2, 3));
        String string = rect.toString();

        // The toString() representation should contain the ID somewhere
        StringTester.assertContains(string, "myId");
    }

    private ImageSubRect subRect(Area area) {
        return new ImageSubRect("test", area);
    }

}
