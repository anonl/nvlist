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
			//System.err.println(part + " " + table);
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
		pushCall(false, ignoreMissing, funcName, args);
	}
	public void pushCall(String funcName, Varargs args) throws LuaException {
		pushCall(false, ignoreMissing, funcName, args);
	}
	public void pushCall(LuaClosure func, Varargs args) throws LuaException {
		pushCall(false, ignoreMissing, func, args);
	}

	protected void pushCall(boolean ignoreConcurrentError, boolean ignoreMissing,
			Object func, Object args) throws LuaException
	{
		if (!ignoreConcurrentError && thread.isRunning()) {
			throw new ConcurrentModificationException("Attempted to use pushCall() on a running thread.");
		}

		LuaClosure lfunc;
		if (func instanceof LuaClosure) {
			lfunc = (LuaClosure)func;
		} else {
			lfunc = getFunction(String.valueOf(func));
		}

		if (lfunc == null) {
			if (!ignoreMissing) {
				throw new LuaException(String.format("function \"%s\" not found", String.valueOf(func)));
			} else {
				return;
			}
		}

		Varargs vargs = getImplicitArgs();
		if (args != null) {
			int narg = vargs.narg();
			if (args instanceof Object[]) {
				//Convert and append to implicit args
				Object[] b = (Object[])args;
				if (b.length >= 0) {
			 		LuaValue[] largs = new LuaValue[narg + b.length];
			 		for (int n = 0; n < b.length; n++) {
						largs[narg+n] = CoerceJavaToLua.coerce(b[n]);
					}
			 		vargs = LuaValue.varargsOf(largs);
				}
			} else {
				Varargs b = (Varargs)args;
				int blen = b.narg();
				if (blen > 0) { //We only need to append if there's something to append
					if (narg <= 0) {
						//We can return the append part if the implicit part is empty
						vargs = b;
					} else {
						//Append args to implicit args
						LuaValue[] largs = new LuaValue[narg + blen];
				 		for (int n = 0; n < blen; n++) {
				 			largs[narg+n] = b.arg(1+n);
				 		}
				 		vargs = LuaValue.varargsOf(largs);
					}
				}
			}
		}

		thread.pushPending(lfunc, vargs);
	}

	/**
	 * Calls a Lua function and returns its result.
	 */
	public Varargs call(LuaClosure func, Object... args) throws LuaException {
		return call(ignoreMissing, func, args);
	}

	/**
	 * Calls a Lua function and returns its result.
	 */
	public Varargs call(String funcName, Object... args) throws LuaException {
		return call(ignoreMissing, funcName, args);
	}

	protected Varargs call(boolean ignoreMissing, Object func, Object... args) throws LuaException {
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
