package nl.weeaboo.vn.impl.sound.desc;

import org.junit.Assert;
import org.junit.Test;

import nl.weeaboo.test.ExceptionTester;
import nl.weeaboo.test.StringTester;
import nl.weeaboo.vn.sound.desc.ISoundDefinition;

public final class SoundDefinitionTest {

    private static final String FILENAME = "myFilename";
    private static final String DISPLAY_NAME = "myDisplayName";

    private final ExceptionTester exTester = new ExceptionTester();
    private final SoundDefinitionTestHelper tester = new SoundDefinitionTestHelper();

    /** Test the default attribute values when using the most basic constructor. */
    @Test
    public void testDefaultInstance() {
        SoundDefinition def = new SoundDefinition(FILENAME);
        Assert.assertEquals(FILENAME, def.getFilename());
        Assert.assertEquals(null, def.getDisplayName());
    }

    /** The display name isn't allowed to be an empty string to prevent  */
    @Test
    public void testInvalidDisplayName() {
        SoundDefinitionBuilder builder = new SoundDefinitionBuilder(FILENAME);
        builder.setDisplayName("");
        exTester.expect(IllegalArgumentException.class, () -> builder.build());
    }

    /**
     * The conversion constructor can convert from an arbitrary {@link ISoundDefinition} to the concrete
     * immutable implementation ({@link SoundDefinition}).
     */
    @Test
    public void testConversionConstructor() {
        // Calling from() on a builder returns an immutable copy
        SoundDefinitionBuilder builder = new SoundDefinitionBuilder(FILENAME);
        SoundDefinition fromBuilder = SoundDefinition.from(builder);
        Assert.assertNotSame(builder, fromBuilder);
        tester.assertEquals(builder, fromBuilder);

        // When passing in an already immutable implementation, the input argument is returned unmodified
        ISoundDefinition def = builder.build();
        Assert.assertSame(def, SoundDefinition.from(def));
    }

    @Test
    public void testBuilder() {
        SoundDefinitionBuilder builder = new SoundDefinitionBuilder("otherFilename");
        builder.setFilename(FILENAME);
        builder.setDisplayName(DISPLAY_NAME);
        assertBuilderResult(builder);

        SoundDefinition def = builder.build();
        assertBuilderResult(def);

        // Check that no information is lost when converting to/from the builder
        def = def.builder().build();
        assertBuilderResult(def);
    }

    @Test
    public void testToString() {
        SoundDefinition def = new SoundDefinition(FILENAME);
        String string = def.toString();

        // The filename should be contained somewhere in the string representation
        StringTester.assertContains(string, FILENAME);
    }

    private void assertBuilderResult(ISoundDefinition def) {
        Assert.assertEquals(FILENAME, def.getFilename());
        Assert.assertEquals(DISPLAY_NAME, def.getDisplayName());
    }

}
