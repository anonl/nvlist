package nl.weeaboo.vn.impl.script.lua;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

import nl.weeaboo.lua2.LuaRunState;
import nl.weeaboo.lua2.luajava.ITypeCoercions;
import nl.weeaboo.lua2.vm.LuaValue;
import nl.weeaboo.vn.script.IScriptFunction;

final class LuaTypeCoercions implements ITypeCoercions {

    private static final long serialVersionUID = 1L;

    private final ITypeCoercions delegate;

    LuaTypeCoercions(ITypeCoercions delegate) {
        this.delegate = delegate;
    }

    static LuaTypeCoercions install(LuaRunState runState) {
        LuaTypeCoercions result = new LuaTypeCoercions(runState.getTypeCoercions());
        runState.setTypeCoercions(result);
        return result;
    }

    @Override
    public <T> LuaValue toLua(@Nullable T javaValue) {
        return toLua(javaValue, javaValue != null ? javaValue.getClass() : Object.class);
    }

    @Override
    public <T> LuaValue toLua(@Nullable T javaValue, Class<?> declaredType) {
        return delegate.toLua(javaValue, declaredType);
    }

    @CheckForNull
    @Override
    public <T> T toJava(LuaValue luaValue, Class<T> javaType) {
        // Special coercion so we can call IButton.setClickHandler() from Lua
        if (IScriptFunction.class.isAssignableFrom(javaType)) {
            return javaType.cast(LuaScriptUtil.toScriptFunction(luaValue, 1));
        }
        return delegate.toJava(luaValue, javaType);
    }

    @Override
    public int scoreParam(LuaValue arg, Class<?> javaParamType) {
        return delegate.scoreParam(arg, javaParamType);
    }

}
