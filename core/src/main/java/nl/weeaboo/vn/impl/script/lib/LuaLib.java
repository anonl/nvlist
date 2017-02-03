package nl.weeaboo.vn.impl.script.lib;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import nl.weeaboo.common.StringUtil;
import nl.weeaboo.lua2.lib.VarArgFunction;
import nl.weeaboo.lua2.vm.LuaConstants;
import nl.weeaboo.lua2.vm.LuaError;
import nl.weeaboo.lua2.vm.LuaNil;
import nl.weeaboo.lua2.vm.LuaTable;
import nl.weeaboo.lua2.vm.Varargs;
import nl.weeaboo.vn.impl.script.lua.ILuaScriptEnvInitializer;
import nl.weeaboo.vn.impl.script.lua.LuaScriptEnv;
import nl.weeaboo.vn.script.ScriptException;
import nl.weeaboo.vn.script.ScriptFunction;

/** Base class for modules of Lua functions */
public abstract class LuaLib implements ILuaScriptEnvInitializer {

    private static final long serialVersionUID = 1L;

    private final String tableName;

    /**
     * @param tableName The name of the global table object to which the library functions should be added, or
     *        {@code null} if the library functions should be directly added to the global table.
     */
    public LuaLib(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public void initEnv(LuaScriptEnv scriptEnv) throws ScriptException {
        LuaTable globals = scriptEnv.getGlobals();

        if (tableName != null) {
            LuaTable table = new LuaTable();
            initTable(table, scriptEnv);
            globals.rawset(tableName, table);
        } else {
            initTable(globals, scriptEnv);
        }
    }

    /**
     * @param env The script environment.
     */
    protected void initTable(LuaTable table, LuaScriptEnv env) throws ScriptException {
        for (Method method : getClass().getMethods()) {
            ScriptFunction functionAnnot = method.getAnnotation(ScriptFunction.class);
            if (functionAnnot == null) {
                continue;
            }

            String name = method.getName();
            if (table.rawget(name) != LuaNil.NIL) {
                throw new ScriptException("There's already a table entry named: " + name + " :: " + table.rawget(name));
            }

            table.rawset(name, wrapFunction(method));
        }
    }

    protected VarArgFunction wrapFunction(Method method) throws ScriptException {
        Class<?> returnType = method.getReturnType();
        if (!returnType.equals(Varargs.class) && !returnType.equals(Void.TYPE)) {
            throw new ScriptException("Return type must be Varargs or void");
        }

        Class<?>[] parameterTypes = method.getParameterTypes();
        if (!Arrays.equals(parameterTypes, new Class<?>[] { Varargs.class })) {
            throw new ScriptException("Method must have a single parameter of type Varargs");
        }

        return new FunctionWrapper(this, method.getName(), parameterTypes);
    }

    private static class FunctionWrapper extends VarArgFunction {

        private static final long serialVersionUID = 1L;

        private final LuaLib object;
        private final String methodName;
        private final Class<?>[] parameterTypes;

        private transient Method method;

        public FunctionWrapper(LuaLib object, String methodName, Class<?>[] parameterTypes) {
            this.object = object;
            this.methodName = methodName;
            this.parameterTypes = parameterTypes.clone();
        }

        @Override
        public Varargs invoke(Varargs args) {
            try {
                if (method == null) {
                    method = object.getClass().getMethod(methodName, parameterTypes);
                }
                Object result = method.invoke(object, args);
                if (result instanceof Varargs) {
                    return (Varargs)result;
                } else if (result == null && method.getReturnType() == Void.TYPE) {
                    return LuaConstants.NONE;
                } else {
                    // This may happen if the methods return type changed (can happen due to serialization)
                    throw new LuaError("Java method (" + method + ") returned non-varargs: "
                            + (result != null ? result.getClass().getName() : "null"));
                }
            } catch (InvocationTargetException ite) {
                Throwable cause = ite.getCause();
                throw new LuaError(invokeErrorMessage(args, cause), cause);
            } catch (Exception e) {
                throw new LuaError(invokeErrorMessage(args, e), e);
            }
        }

        private String invokeErrorMessage(Varargs args, Throwable cause) {
            String error = StringUtil.formatRoot("Error invoking Java method: %s%s",
                    methodName, args);
            if (cause != null) {
                error += " :: " + cause;
            }
            return error;
        }
    }

}
