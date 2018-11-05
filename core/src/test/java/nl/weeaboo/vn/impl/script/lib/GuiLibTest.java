package nl.weeaboo.vn.impl.script.lib;

import org.junit.Assert;
import org.junit.Test;

import nl.weeaboo.styledtext.StyledText;
import nl.weeaboo.test.RectAssert;
import nl.weeaboo.vn.impl.script.lua.LuaScriptEnv;
import nl.weeaboo.vn.impl.script.lua.LuaScriptUtil;
import nl.weeaboo.vn.impl.script.lua.LuaTestUtil;
import nl.weeaboo.vn.impl.test.CoreTestUtil;
import nl.weeaboo.vn.scene.ButtonViewState;
import nl.weeaboo.vn.scene.IButton;
import nl.weeaboo.vn.scene.IGridPanel;
import nl.weeaboo.vn.scene.ILayer;
import nl.weeaboo.vn.scene.IViewport;
import nl.weeaboo.vn.scene.IVisualElement;
import nl.weeaboo.vn.script.ScriptException;

public final class GuiLibTest extends AbstractLibTest {

    @Override
    protected void addInitializers(LuaScriptEnv scriptEnv) {
        scriptEnv.addInitializer(new ImageLib(env));
        scriptEnv.addInitializer(new GuiLib(env));
    }

    @Test
    public void testCreateButton() throws ScriptException {
        loadScript("integration/gui/button");

        // createButton with no arguments
        IButton defaultButton = LuaTestUtil.getGlobal("defaultButton", IButton.class);
        assertParentLayer(defaultButton, LuaScriptUtil.getActiveLayer());
        // When no background image is specified, the button's text is changed (otherwise it would be invisible)
        Assert.assertEquals(new StyledText("ERROR"), defaultButton.getText());

        // createButton with an image path
        IButton imageButton1 = LuaTestUtil.getGlobal("imageButton1", IButton.class);
        Assert.assertEquals(StyledText.EMPTY_STRING, imageButton1.getText());
        assertTextureSet(imageButton1, ButtonViewState.DEFAULT);
        assertTextureSet(imageButton1, ButtonViewState.DISABLED);
        assertTextureSet(imageButton1, ButtonViewState.PRESSED);
        assertTextureSet(imageButton1, ButtonViewState.ROLLOVER);

        // createButton with a texture object - this only sets the background for the default view state
        IButton imageButton2 = LuaTestUtil.getGlobal("imageButton2", IButton.class);
        Assert.assertEquals(StyledText.EMPTY_STRING, imageButton2.getText());
        assertTextureSet(imageButton2, ButtonViewState.DEFAULT);
        assertTextureNotSet(imageButton2, ButtonViewState.DISABLED);
        assertTextureNotSet(imageButton2, ButtonViewState.PRESSED);
        assertTextureNotSet(imageButton2, ButtonViewState.ROLLOVER);
    }

    @Test
    public void testCreateViewport() throws ScriptException {
        loadScript("integration/gui/viewport");

        ILayer activeLayer = LuaScriptUtil.getActiveLayer();

        IViewport defaultViewport = LuaTestUtil.getGlobal("defaultViewport", IViewport.class);
        assertParentLayer(defaultViewport, activeLayer);
        RectAssert.assertEquals(activeLayer.getVisualBounds(), defaultViewport.getVisualBounds(), CoreTestUtil.EPSILON);
    }

    @Test
    public void testCreateGridPanel() throws ScriptException {
        loadScript("integration/gui/gridpanel");

        ILayer activeLayer = LuaScriptUtil.getActiveLayer();

        IGridPanel defaultGrid = LuaTestUtil.getGlobal("defaultGrid", IGridPanel.class);
        assertParentLayer(defaultGrid, activeLayer);
        RectAssert.assertEquals(activeLayer.getVisualBounds(), defaultGrid.getVisualBounds(), CoreTestUtil.EPSILON);
    }

    private void assertParentLayer(IVisualElement button, ILayer expected) {
        Assert.assertEquals(true, expected.contains(button));
    }

    private void assertTextureSet(IButton button, ButtonViewState viewState) {
        Assert.assertNotNull(button.getTexture(viewState));
    }

    private void assertTextureNotSet(IButton button, ButtonViewState viewState) {
        Assert.assertNull(button.getTexture(viewState));
    }

}
