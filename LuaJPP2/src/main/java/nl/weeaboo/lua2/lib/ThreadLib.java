package nl.weeaboo.lua2.lib;

import org.luaj.vm2.LuaClosure;
import org.luaj.vm2.LuaThread;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.BaseLib;

import nl.weeaboo.lua2.LuaRunState;
import nl.weeaboo.lua2.LuaThreadGroup;
import nl.weeaboo.lua2.io.LuaSerializable;
import nl.weeaboo.lua2.link.LuaLink;

@LuaSerializable
public class ThreadLib extends LuaLibrary {

	private static final long serialVersionUID = 449470590558474872L;

	private static final String[] NAMES = {
		"new",
		"newGroup",
		"yield",
		"endCall",
		"jump"
	};

	private static final int INIT      = 0;
	private static final int NEW       = 1;
	private static final int NEW_GROUP = 2;
	private static final int YIELD     = 3;
	private static final int END_CALL  = 4;
	private static final int JUMP      = 5;

	@Override
	protected LuaLibrary newInstance() {
		return new ThreadLib();
	}

	@Override
	public Varargs invoke(Varargs args) {
		switch (opcode) {
		case INIT: return initLibrary("Thread", NAMES, 1);
		case NEW: return newThread(args);
		case NEW_GROUP: return newThreadGroup(args);
		case YIELD: return yield(args);
		case END_CALL: return endCall(args);
		case JUMP: return jump(args);
		default: return super.invoke(args);
		}
	}

    /**
     * @param args Lua arguments
     */
	protected Varargs newThread(Varargs args) {
		LuaRunState lrs = LuaRunState.getCurrent();
		LuaClosure func = args.arg1().checkclosure();
		LuaLink result = lrs.newThread(func, args.subargs(2));
		return LuajavaLib.toUserdata(result, result.getClass());
	}

    /**
     * @param args Lua arguments
     */
	protected Varargs newThreadGroup(Varargs args) {
		LuaRunState lrs = LuaRunState.getCurrent();
		LuaThreadGroup result = lrs.newThreadGroup();
		return LuajavaLib.toUserdata(result, result.getClass());
	}

    /**
     * @param args Lua arguments
     */
	protected Varargs yield(Varargs args) {
		LuaRunState lrs = LuaRunState.getCurrent();
		LuaLink link = lrs.getCurrentLink();

		if (link != null && args.arg1() != NIL) {
			int w = args.arg1().toint();
			link.setWait(w <= 0 ? w : w - 1);
		}

		final LuaThread running = LuaThread.getRunning();
		return running.yield(args);
	}

    /**
     * @param args Lua arguments
     */
	protected Varargs endCall(Varargs args) {
		LuaRunState lrs = LuaRunState.getCurrent();
		LuaLink link = lrs.getCurrentLink();

		if (link != null && args.arg1() != NIL) {
			int w = args.arg1().toint();
			link.setWait(w <= 0 ? w : w - 1);
		}

		final LuaThread running = LuaThread.getRunning();
		return running.endCall(args);
	}

    /**
     * @param args Lua arguments
     */
	protected Varargs jump(Varargs args) {
		Varargs v = BaseLib.loadFile(args.checkjstring(1));
		if (v.isnil(1)) {
			return error(v.tojstring(2));
		}
		v = v.arg1();

		if (v instanceof LuaClosure) {
			LuaRunState lrs = LuaRunState.getCurrent();
			final LuaLink link = lrs.getCurrentLink();
			link.jump((LuaClosure)v, NONE);
			return NONE;
		} else {
			return error("Error running non-closure Lua function: " + v);
		}
	}

}
