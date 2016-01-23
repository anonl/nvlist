package nl.weeaboo.lua2.link;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ConcurrentModificationException;

import org.luaj.vm2.LuaClosure;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaThread;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import nl.weeaboo.lua2.LuaException;
import nl.weeaboo.lua2.LuaRunState;
import nl.weeaboo.lua2.io.DelayedReader;
import nl.weeaboo.lua2.io.LuaSerializable;
import nl.weeaboo.lua2.io.LuaSerializer;
import nl.weeaboo.lua2.lib.CoerceJavaToLua;

@LuaSerializable
public class LuaLink implements Serializable {

	private static final long serialVersionUID = -6946289878490242267L;

	protected LuaRunState luaRunState;
	protected transient LuaThread thread;

	protected int wait;
	private boolean inited;
	private boolean ignoreMissing;
	private boolean persistent;

	public LuaLink(LuaRunState lrs) {
		luaRunState = lrs;
		thread = new LuaThread(luaRunState, luaRunState.getGlobalEnvironment());
	}

	//Functions
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();

		LuaSerializer ls = LuaSerializer.getThreadLocal();
		if (ls == null) {
			out.writeObject(thread);
		} else {
			ls.writeDelayed(thread);
		}
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();

		LuaSerializer ls = LuaSerializer.getThreadLocal();
		if (ls == null) {
			thread = (LuaThread)in.readObject();
		} else {
			ls.readDelayed(new DelayedReader() {
				@Override
				public void onRead(Object obj) {
					thread = (LuaThread)obj;
				}
			});
		}
	}

	public void destroy() {
		persistent = false;
		thread.destroy();
	}

	protected LuaClosure getFunction(String funcName) {
		LuaValue table = thread.getfenv();

		//Resolve a.b.c.d, ends with table=c
		int index;
		while (table != null && !table.isnil() && (index = funcName.indexOf('.')) >= 0) {
			String part = funcName.substring(0, index);
			table = table.get(LuaString.valueOf(part));
			funcName = funcName.substring(index+1);
		}

		LuaValue func = null;
		if (table != null && !table.isnil()) {
			func = table.get(LuaString.valueOf(funcName));
		}
		return (func instanceof LuaClosure ? (LuaClosure)func : null);
	}

	protected Varargs getImplicitArgs() {
		return LuaValue.NONE;
	}

	public void pushCall(String funcName, Object... args) throws LuaException {
        pushCall(false, ignoreMissing, getFunction(funcName), concatArgs(getImplicitArgs(), args));
	}
	public void pushCall(String funcName, Varargs args) throws LuaException {
        pushCall(getFunction(funcName), concatArgs(getImplicitArgs(), args));
	}
	public void pushCall(LuaClosure func, Varargs args) throws LuaException {
        pushCall(false, ignoreMissing, func, concatArgs(getImplicitArgs(), args));
	}

    private void pushCall(boolean ignoreConcurrentError, boolean ignoreMissing, LuaClosure func,
            Varargs args) throws LuaException
	{
		if (!ignoreConcurrentError && thread.isRunning()) {
			throw new ConcurrentModificationException("Attempted to use pushCall() on a running thread.");
		}

        if (func == null) {
			if (!ignoreMissing) {
				throw new LuaException(String.format("function \"%s\" not found", String.valueOf(func)));
			} else {
				return;
			}
		}

        thread.pushPending(func, args);
	}

    private static Varargs concatArgs(Varargs implicitArgs, Varargs extraArgs) {
        if (extraArgs == null) {
            return implicitArgs;
        }

        int implicitArgsCount = implicitArgs.narg();
        LuaValue[] merged = new LuaValue[implicitArgsCount + extraArgs.narg()];
        for (int n = 0; n < implicitArgsCount; n++) {
            merged[n] = implicitArgs.arg(1 + n);
        }
        for (int n = 0; n < extraArgs.narg(); n++) {
            merged[implicitArgsCount + n] = extraArgs.arg(1 + n);
        }
        return LuaValue.varargsOf(merged);
    }

    private static Varargs concatArgs(Varargs implicitArgs, Object[] javaArgs) {
        if (javaArgs == null) {
            return implicitArgs;
        }

        int implicitArgsCount = implicitArgs.narg();
        LuaValue[] merged = new LuaValue[implicitArgsCount + javaArgs.length];
        for (int n = 0; n < implicitArgsCount; n++) {
            merged[n] = implicitArgs.arg(1 + n);
        }
        for (int n = 0; n < javaArgs.length; n++) {
            merged[implicitArgsCount + n] = CoerceJavaToLua.coerce(javaArgs[n]);
        }
        return LuaValue.varargsOf(merged);
    }

    /**
     * Calls a Lua function and returns its result.
     */
    public Varargs call(String funcName, Object... args) throws LuaException {
        LuaClosure function = getFunction(funcName);
        if (function == null && !ignoreMissing) {
            throw new LuaException(String.format("function \"%s\" not found", funcName));
        }
        return call(function, args);
    }

    /**
     * Calls a Lua function and returns its result.
     */
	public Varargs call(LuaClosure func, Object... args) throws LuaException {
        return call(ignoreMissing, func, concatArgs(getImplicitArgs(), args));
	}

    private Varargs call(boolean ignoreMissing, LuaClosure func, Varargs args) throws LuaException {
		Varargs result = LuaValue.NONE;

		LuaLink oldLink = luaRunState.getCurrentLink();
		luaRunState.setCurrentLink(this);
		try {
            pushCall(true, ignoreMissing, func, args);
			result = thread.resume(1);
		} catch (LuaError e) {
			if (ignoreMissing && e.getCause() instanceof NoSuchMethodException) {
				//Ignore methods that don't exist
			} else {
				throw new LuaException(e.getMessage(), e.getCause() != null ? e.getCause() : e);
			}
		} catch (RuntimeException e) {
			throw new LuaException(e);
		} finally {
			luaRunState.setCurrentLink(oldLink);
		}

		return result;
	}

    /**
     * @throws LuaException
     */
	protected void init() throws LuaException {
	}

	public boolean update() throws LuaException {
		boolean changed = false;

		if (!inited) {
			inited = true;
			changed = true;
			init();
		}

		if (isFinished()) {
			return changed;
		}

		if (wait != 0) {
			if (wait > 0) wait--;
			return changed;
		}

		LuaLink oldLink = luaRunState.getCurrentLink();
		luaRunState.setCurrentLink(this);
		try {
			changed = true;
			thread.resume(-1);
		} catch (RuntimeException e) {
			if (e.getCause() instanceof NoSuchMethodException) {
				throw new LuaException(e.getCause().getMessage());
			} else {
				LuaException le = new LuaException(e.getMessage(), e);
				le.setStackTrace(e.getStackTrace());
				throw le;
			}
		} finally {
			luaRunState.setCurrentLink(oldLink);
		}

		return changed;
	}

	public void jump(LuaClosure c, Varargs args) {
		thread.destroy();
		setWait(0);

		thread = new LuaThread(luaRunState, luaRunState.getGlobalEnvironment());
		thread.pushPending(c, args);
	}

	//Getters
	public boolean isRunnable() {
	    if (!inited) return true;
	    if (thread == null) return false;
	    return !thread.isFinished();
	}

	public final boolean isFinished() {
		if (!inited) return false;
		if (thread == null) return true;
		return (persistent ? thread.isDead() : thread.isFinished());
	}
	public int getWait() {
		return wait;
	}
	public LuaThread getThread() {
		return thread;
	}

	//Setters
	public void setWait(int w) {
		wait = w;
	}
	public void setMinimumWait(int w) {
		if (wait >= 0 && (w < 0 || w > wait)) {
			setWait(w);
		}
	}
	public void addWait(int dt) {
		if (dt < 0) throw new IllegalArgumentException("Can't call addWait with a negative number: " + dt);

		if (wait >= 0) {
			setWait(wait += dt);
		}
	}
	public void setIgnoreMissing(boolean e) {
		ignoreMissing = e;
	}

	/**
	 * A persistent LuaLink will not destroy itself when its thread finishes.
	 */
	public void setPersistent(boolean p) {
		persistent = p;
	}

}
