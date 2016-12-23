package nl.weeaboo.vn.scene;

import org.junit.Assert;
import org.junit.Test;

import nl.weeaboo.vn.EnumTester;

public class ButtonViewStateTest {

    @Test
    public void checkUnchanged() {
        // Trigger a test failure if the enum changes
        Assert.assertEquals(495950486, EnumTester.hashEnum(ButtonViewState.class));
    }

}
