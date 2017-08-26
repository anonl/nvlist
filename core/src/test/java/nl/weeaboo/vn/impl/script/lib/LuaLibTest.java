package nl.weeaboo.vn.impl.script.lib;

import java.lang.reflect.Method;

import javax.annotation.Nullable;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import nl.weeaboo.lua2.LuaRunState;
import nl.weeaboo.lua2.lib.VarArgFunction;
import nl.weeaboo.lua2.vm.LuaConstants;
import nl.weeaboo.lua2.vm.LuaError;
import nl.weeaboo.lua2.vm.LuaInteger;
import nl.weeaboo.lua2.vm.LuaUserdata;
import nl.weeaboo.lua2.vm.LuaValue;
import nl.weeaboo.lua2.vm.Varargs;
import nl.weeaboo.vn.impl.script.lua.LuaTestUtil;
import nl.weeaboo.vn.script.ScriptException;
import nl.weeaboo.vn.script.ScriptFunction;

public class LuaLibTest {

    private TestLib testLib;
    private LuaRunState luaRunState;

    @Before
    public void before() {
        testLib = new TestLib();

        luaRunState = LuaTestUtil.newRunState();
    }

    @After
    public void after() {
        luaRunState.destroy();
    }

    @Test
    public void validScriptFunctionDefinitions() {
        // Default case: Accepts any number of args, returns any number of args
        assertValid(true, "varargsVarargs");
        // Returning void is also supported. Equivalent to always returning NONE
        assertValid(true, "voidVarargs");

        // Methods without parameters are not (yet) supported
        assertValid(false, "voidVoid");
        assertValid(false, "varargsVoid");
    }

    @Test
    public void callVarargsFunction() {
        VarArgFunction method = getWrappedFunction("varargsVarargs");

        Varargs in = LuaInteger.valueOf(987);
        Varargs out = method.invoke(in);

        // Output = 123, in
        Assert.assertEquals(2, out.narg());
        Assert.assertEquals(123, out.checkint(1));
        Assert.assertEquals(987, out.checkint(2));
    }

    @Test
    public void callVoidFunction() {
        VarArgFunction method = getWrappedFunction("voidVarargs");

        Varargs in = LuaInteger.valueOf(987);
        Varargs out = method.invoke(in);

        // ScriptFunctions returning void in Java return zero results in Lua
        Assert.assertEquals(0, out.narg());
    }

    @Test(expected = LuaError.class)
    public void scriptFunctionThrowsException() {
        VarArgFunction method = getWrappedFunction("throwsException");

        // Runtime exception is wrapped in a LuaError and rethrown
        method.invoke(LuaUserdata.userdataOf(new RuntimeException()));
    }

    @Test(expected = LuaError.class)
    public void inaccessibleScriptFunction() {
        VarArgFunction method = getWrappedFunction("privateMethod");

        // If the method is no longer accessible (may happen when loading an old save), an exception is thrown
        method.invoke();
    }

    private void assertValid(boolean expected, String methodName) {
        Method method = findMethod(methodName);
        try {
            testLib.wrapFunction(method);
            Assert.assertEquals(expected, true);
        } catch (ScriptException e) {
            Assert.assertEquals(e.toString(), expected, false);
        }
    }

    private VarArgFunction getWrappedFunction(String methodName) {
        try {
            return testLib.wrapFunction(findMethod(methodName));
        } catch (ScriptException e) {
            throw new AssertionError(e);
        }
    }

    private @Nullable Method findMethod(String methodName) {
        for (Method method : testLib.getClass().getDeclaredMethods()) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        return null;
    }

    private static class TestLib extends LuaLib {

        private static final long serialVersionUID = 1L;

        public TestLib() {
            super("");
        }

        @ScriptFunction
        public void voidVoid() {
        }

        @ScriptFunction
        public void voidVarargs(@SuppressWarnings("unused") Varargs args) {
        }

        @ScriptFunction
        public Varargs varargsVoid() {
            return LuaConstants.NONE;
        }

        @ScriptFunction
        public Varargs varargsVarargs(Varargs args) {
            return LuaValue.varargsOf(LuaInteger.valueOf(123), args);
        }

        @ScriptFunction
        public void throwsException(Varargs args) throws Exception {
            throw args.checkuserdata(1, Exception.class);
        }

        @ScriptFunction
        private void privateMethod(@SuppressWarnings("unused") Varargs args) {
        }

    }

}
