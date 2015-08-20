package nl.weeaboo.vn.script.lua;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

import nl.weeaboo.vn.script.ScriptException;
import nl.weeaboo.vn.script.ScriptFunction;

/** Base class for modules of Lua functions */
public abstract class LuaLib implements Serializable, ILuaScriptEnvInitializer {

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
    public void initEnv(LuaScriptEnv env) throws ScriptException {
        LuaTable globals = env.getGlobals();

        if (tableName != null) {
            LuaTable table = new LuaTable();
            initTable(table, env);
            globals.rawset(tableName, table);
        } else {
            initTable(globals, env);
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

            if (!method.getReturnType().equals(Varargs.class)) {
                throw new ScriptException("Return type must be Varargs");
            }

            if (!Arrays.equals(method.getParameterTypes(), new Class<?>[] { Varargs.class })) {
                throw new ScriptException("Method must have a single parameter of type Varargs");
            }

            String name = method.getName();
            if (table.rawget(name) != LuaValue.NIL) {
                throw new ScriptException("There's already a table entry named: " + name + " :: " + table.rawget(name));
            }
            table.rawset(name, wrapFunction(method.getName(), method.getParameterTypes()));
        }
    }

    protected VarArgFunction wrapFunction(String methodName, Class<?>[] parameterTypes) {
        return new FunctionWrapper(this, methodName, parameterTypes);
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
                return (Varargs)method.invoke(object, args);
            } catch (InvocationTargetException ite) {
                Throwable cause = ite.getCause();
                throw new LuaError(invokeErrorMessage(args, cause), cause);
            } catch (Exception e) {
                throw new LuaError(invokeErrorMessage(args, e), e);
            }
        }

        private String invokeErrorMessage(Varargs args, Throwable cause) {
            String error = "Error invoking Java method: " + method.getName() + "(" + args + ")";
            if (cause != null) {
                error += " :: " + cause;
            }
            return error;
        }
    }

}
