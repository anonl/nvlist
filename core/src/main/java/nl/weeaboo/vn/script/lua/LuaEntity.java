package nl.weeaboo.vn.script.lua;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaUserdata;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

import nl.weeaboo.entity.Entity;
import nl.weeaboo.entity.Part;
import nl.weeaboo.entity.PartRegistry;
import nl.weeaboo.entity.PartType;
import nl.weeaboo.lua2.lib.LuajavaLib;

public class LuaEntity {

    public static LuaUserdata toUserdata(Entity e, PartRegistry pr) {
        LuaUserdata userdata = LuajavaLib.toUserdata(e, Entity.class);

        LuaValue classMeta = userdata.getmetatable().checktable();
        LuaValue classGet = classMeta.rawget(LuaValue.INDEX);
        LuaValue classSet = classMeta.rawget(LuaValue.NEWINDEX);

        LuaTable newMeta = new LuaTable();
        newMeta.rawset(LuaValue.INDEX, new LuaEntityIndexFunction(pr, classGet));
        newMeta.rawset(LuaValue.NEWINDEX, classSet);
        userdata.setmetatable(newMeta);

        return userdata;
    }

    private static class LuaEntityIndexFunction extends VarArgFunction {

        private static final long serialVersionUID = 1L;

        private final PartRegistry pr;
        private final LuaValue delegate;

        public LuaEntityIndexFunction(PartRegistry pr, LuaValue delegate) {
            this.pr = pr;
            this.delegate = delegate;
        }

        @Override
        public Varargs invoke(Varargs args) {
            Entity e = args.checkuserdata(1, Entity.class);
            LuaValue key = args.arg(2);

            PartType<?> keyPartType = pr.get(key.tojstring());
            if (keyPartType != null) {
                Part part = e.getPart(keyPartType);
                if (part != null) {
                    return LuajavaLib.toUserdata(part, keyPartType.getPartClass());
                }
            }

            return delegate.invoke(args);
        }

    }

}
