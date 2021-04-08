package nl.weeaboo.vn.desktop;

import org.junit.Assert;
import org.junit.Test;

import com.badlogic.gdx.backends.headless.mock.graphics.MockGraphics;

import nl.weeaboo.common.Dim;

public final class DesktopGraphicsUtilTest {

    private static final Dim LARGE = Dim.of(1920, 1080);
    private static final Dim MEDIUM = Dim.of(1280, 720);

    @Test
    public void testLimitWindowSize() {
        assertLimitWindowSize(MEDIUM, LARGE, MEDIUM);

        // Safety margins of 100px (horizontal) and 150px (vertical) are subtracted
        // If the window is too large, it's proportionally resized within those limits
        assertLimitWindowSize(MEDIUM, MEDIUM, Dim.of(1013, 570));

        // Large has the same proportions as medium
        assertLimitWindowSize(LARGE, MEDIUM, Dim.of(1013, 570));
    }

    private static void assertLimitWindowSize(Dim window, Dim screen, Dim expectedResult) {
        Dim actual = DesktopGraphicsUtil.limitInitialWindowSize(new MockGraphics() {
            @Override
            public int getBackBufferWidth() {
                return window.w;
            }

            @Override
            public int getBackBufferHeight() {
                return window.h;
            }

            @Override
            public DisplayMode getDisplayMode() {
                return new DisplayMode(screen.w, screen.h, 60, 24) { };
            }
        });
        Assert.assertEquals(expectedResult, actual);
    }
}
