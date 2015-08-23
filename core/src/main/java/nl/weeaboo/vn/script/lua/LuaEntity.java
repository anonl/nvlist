package nl.weeaboo.vn.script.lua;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

import org.luaj.vm2.LuaUserdata;

import com.google.common.collect.Lists;
import com.google.common.reflect.AbstractInvocationHandler;

import nl.weeaboo.entity.Entity;
import nl.weeaboo.entity.Part;
import nl.weeaboo.entity.PartRegistry;
import nl.weeaboo.entity.PartType;
import nl.weeaboo.lua2.lib.LuajavaLib;

public class LuaEntity {

    public static LuaUserdata toUserdata(Entity e, PartRegistry pr) {
        Object proxy = createProxy(e, pr);
        return LuajavaLib.toUserdata(proxy, proxy.getClass());
    }

    private static Object createProxy(Entity e, PartRegistry pr) {
        List<Class<?>> interfaces = Lists.newArrayList();
        for (PartType<?> partType : pr.getAll()) {
            Class<?> partInterface = partType.getPartInterface();
            if (partInterface.isInterface()) {
                interfaces.add(partInterface);
            }
        }
        Class<?>[] interfacesArray = interfaces.toArray(new Class<?>[interfaces.size()]);

        final ClassLoader classLoader = LuaEntity.class.getClassLoader();
        return Proxy.newProxyInstance(classLoader, interfacesArray, new EntityProxyHandler(e, pr));
    }

    private static class EntityProxyHandler extends AbstractInvocationHandler implements Serializable {

        private static final long serialVersionUID = 1L;

        private final Entity entity;
        private final PartRegistry pr;

        public EntityProxyHandler(Entity e, PartRegistry pr) {
            this.entity = e;
            this.pr = pr;
        }

        @Override
        protected Object handleInvocation(Object proxy, Method method, Object[] args)
                throws IllegalArgumentException, IllegalAccessException, InvocationTargetException,
                NoSuchMethodException {

            Class<?> methodClass = method.getDeclaringClass();
            for (PartType<?> partType : pr.getAll()) {
                if (partType.getPartInterface().isAssignableFrom(methodClass)) {
                    Part part = (Part)entity.getPart(partType);
                    return method.invoke(part, args);
                }
            }

            throw new NoSuchMethodException(method.getName());
        }

    }

}
