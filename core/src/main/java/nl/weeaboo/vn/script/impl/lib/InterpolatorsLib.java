package nl.weeaboo.vn.script.impl.lib;

import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.weeaboo.lua2.luajava.LuajavaLib;
import nl.weeaboo.lua2.vm.LuaFunction;
import nl.weeaboo.lua2.vm.LuaTable;
import nl.weeaboo.lua2.vm.LuaUserdata;
import nl.weeaboo.lua2.vm.LuaValue;
import nl.weeaboo.lua2.vm.Varargs;
import nl.weeaboo.reflect.ReflectUtil;
import nl.weeaboo.vn.core.IInterpolator;
import nl.weeaboo.vn.core.Interpolators;
import nl.weeaboo.vn.core.LUTInterpolator;
import nl.weeaboo.vn.script.ScriptException;
import nl.weeaboo.vn.script.ScriptFunction;
import nl.weeaboo.vn.script.impl.lua.LuaScriptEnv;

public class InterpolatorsLib extends LuaLib {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(InterpolatorsLib.class);

    public InterpolatorsLib() {
        super("Interpolators");
    }

    @Override
    protected void initTable(LuaTable table, LuaScriptEnv env) throws ScriptException {
        // Add well-known interpolators to table
        try {
            Map<String, IInterpolator> map = ReflectUtil.getConstants(Interpolators.class, IInterpolator.class);
            for (Entry<String, IInterpolator> entry : map.entrySet()) {
                LuaUserdata interpolator = LuajavaLib.toUserdata(entry.getValue(), IInterpolator.class);
                table.rawset(entry.getKey(), interpolator);
            }
        } catch (IllegalAccessException iae) {
            LOG.error("Illegal access while trying to retrieve interpolators", iae);
        }

        super.initTable(table, env);
    }

    public static IInterpolator getInterpolator(LuaValue lval, IInterpolator defaultValue) {
        if (lval.isuserdata()) {
            LuaUserdata udata = (LuaUserdata)lval;
            Object jval = udata.touserdata();
            if (jval instanceof IInterpolator) {
                return (IInterpolator)jval;
            }
        } else if (lval instanceof LuaFunction) {
            return getLuaInterpolator((LuaFunction)lval, 256);
        }
        return defaultValue;
    }

    public static IInterpolator getLuaInterpolator(LuaFunction func, int lutSize) {
        float[] lut = new float[lutSize];
        float scale = 1f / (lutSize - 1);
        for (int n = 0; n < lutSize; n++) {
            lut[n] = func.call(LuaValue.valueOf(n * scale)).tofloat();
        }
        return new LUTInterpolator(lut);
    }

    /**
     * @param args
     *        <ol>
     *        <li>interpolation function
     *        </ol>
     * @return An interpolator based on the given interpolation function.
     */
    @ScriptFunction
    public Varargs get(Varargs args) {
        IInterpolator interpolator = getInterpolator(args.arg1(), null);
        if (interpolator == null) {
            return args.arg(2);
        }
        return LuajavaLib.toUserdata(interpolator, IInterpolator.class);
    }

}
