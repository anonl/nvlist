package nl.weeaboo.lua2;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.luaj.vm2.LuaClosure;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaThread;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.DebugLib;
import org.luaj.vm2.lib.PackageLib;

import nl.weeaboo.lua2.io.LuaSerializable;
import nl.weeaboo.lua2.lib.J2sePlatform;
import nl.weeaboo.lua2.link.LuaFunctionLink;
import nl.weeaboo.lua2.link.LuaLink;

@LuaSerializable
public final class LuaRunState implements Serializable {

	private static final long serialVersionUID = 6685783138764897120L;

	private static ThreadLocal<LuaRunState> threadInstance = new ThreadLocal<LuaRunState>();

	//--- Uses manual serialization, don't add variables ---
	private boolean destroyed;
	private PackageLib packageLib;
	private LuaThread mainThread;
	private LuaThreadGroup[] threadGroups; //Uses an array to reduce garbage from iterators (it makes a little sense when trying to run at 60 fps on a smartphone).
	private int threadGroupsCount;
	private LuaTable globalEnvironment;
	private int instructionCountLimit = 1000000;
	//--- Uses manual serialization, don't add variables ---

	private transient LuaLink current;
	private transient LuaThread currentThread;
	private transient int instructionCount;

	public LuaRunState() {
		registerOnThread();

		globalEnvironment = J2sePlatform.debugGlobals();
		mainThread = LuaThread.createMainThread(this, globalEnvironment);
		threadGroups = new LuaThreadGroup[4];

		newThreadGroup();
	}

	//Functions
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		registerOnThread();

		in.defaultReadObject();
	}

	public void destroy() {
		if (destroyed) {
			return;
		}

		destroyed = true;

		for (LuaThreadGroup tg : threadGroups) {
			if (tg != null) tg.destroy();
		}
		threadGroups = null;
		threadGroupsCount = 0;

		current = null;
		currentThread = null;

		if (threadInstance.get() == this) {
			threadInstance.set(null);
		}
	}

	public void registerOnThread() {
		threadInstance.set(this);
	}

	private LuaThreadGroup findFirstThreadGroup() {
		for (int n = 0; n < threadGroupsCount; n++) {
			LuaThreadGroup g = threadGroups[n];
			if (!g.isDestroyed()) {
				return g;
			}
		}
		return null;
	}

	public LuaFunctionLink newThread(LuaClosure func, Varargs args) {
		LuaThreadGroup group = getDefaultThreadGroup();
		if (group == null) {
			throw new IllegalStateException("Attempted to spawn a new thread, but all thread groups are destroyed");
		}
		return group.newThread(func, args);
	}

	public LuaFunctionLink newThread(String func, Object... args) {
		LuaThreadGroup group = getDefaultThreadGroup();
		if (group == null) {
			throw new IllegalStateException("Attempted to spawn a new thread, but all thread groups are destroyed");
		}
		return group.newThread(func, args);
	}

	public LuaThreadGroup newThreadGroup() {
		LuaThreadGroup result = new LuaThreadGroup(this);
		if (threadGroupsCount >= threadGroups.length) {
			LuaThreadGroup[] newGroups = new LuaThreadGroup[threadGroups.length << 1];
			System.arraycopy(threadGroups, 0, newGroups, 0, threadGroupsCount);
			threadGroups = newGroups;
		}
		threadGroups[threadGroupsCount++] = result;
		return result;
	}

	public boolean update() throws LuaException {
		registerOnThread();

		boolean changed = false;
		if (destroyed) {
			return changed;
		}

		//Update
		int d = 0;
		for (int s = 0; s < threadGroupsCount; s++) {
			LuaThreadGroup tg = threadGroups[s];
			if (!tg.isDestroyed()) {
				threadGroups[d++] = tg;
				changed |= tg.update();

				if (destroyed) {
					return changed;
				}
			}
		}
		for (int n = d; n < threadGroupsCount; n++) {
			threadGroups[n] = null; //Clear leftover references
		}
		threadGroupsCount = d;

		return changed;
	}

	public void printStackTrace(PrintStream pout) {
		List<LuaLink> temp = new ArrayList<LuaLink>();

		pout.println("=== Printing stack traces ===");
		for (int n = 0; n < threadGroupsCount; n++) {
			LuaThreadGroup group = threadGroups[n];
			pout.println("+ Threadgroup " + group);

			temp.clear();
			group.getThreads(temp);
			for (LuaLink link : temp) {
				LuaThread thread = link.getThread();
				if (thread != null) {
					pout.println(" - Thread " + link);
					for (StackTraceElement ste : DebugLib.stackTrace(thread, 0, 32)) {
						pout.println("    at " + ste);
					}
				}
			}
		}
	}

    /**
     * @param pc The current program counter
     */
	public void onInstruction(int pc) throws LuaError {
		instructionCount++;
		if (currentThread != null && instructionCount > instructionCountLimit) {
			throw new LuaError("Lua thread instruction limit exceeded (is there an infinite loop somewhere)?");
		}
	}

	//Getters
	public static LuaRunState getCurrent() {
		return threadInstance.get();
	}

	public boolean isDestroyed() {
		return destroyed;
	}
	public LuaLink getCurrentLink() {
		return current;
	}
	public LuaThreadGroup getDefaultThreadGroup() {
		return findFirstThreadGroup();
	}
	public LuaThread getRunningThread() {
		return (currentThread != null ? currentThread : mainThread);
	}
	public PackageLib getPackageLib() {
		return packageLib;
	}
	public LuaTable getGlobalEnvironment() {
		return globalEnvironment;
	}
	public int getInstructionCountLimit() {
		return instructionCountLimit;
	}

	//Setters
	public void setInstructionCountLimit(int lim) {
		instructionCountLimit = lim;
	}

	public void setCurrentLink(LuaLink cur) {
		current = cur;
	}

	public void setRunningThread(LuaThread t) {
		if (currentThread == t) {
			return;
		}

		currentThread = t;
		instructionCount = 0;
	}

	public void setPackageLib(PackageLib plib) {
		packageLib = plib;
	}

}
