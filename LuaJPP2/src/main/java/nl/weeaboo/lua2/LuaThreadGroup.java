package nl.weeaboo.lua2;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.luaj.vm2.LuaClosure;
import org.luaj.vm2.Varargs;

import nl.weeaboo.lua2.io.LuaSerializable;
import nl.weeaboo.lua2.link.LuaFunctionLink;
import nl.weeaboo.lua2.link.LuaLink;

@LuaSerializable
public final class LuaThreadGroup implements Externalizable {
	
	//--- Uses manual serialization, don't add variables ---
	private LuaRunState luaRunState;
	private boolean destroyed;	
	private boolean suspended;
	
	private LuaLink[] threads;
	private int threadsCount;
	private ArrayList<LuaLink> standbyList;
	//--- Uses manual serialization, don't add variables ---
	
	/**
	 * Do not use. Required for efficient serialization. 
	 */
	@Deprecated
	public LuaThreadGroup() {
		this(null);
	}
	public LuaThreadGroup(LuaRunState lrs) {
		luaRunState = lrs;
		threads = new LuaLink[16];
		standbyList = new ArrayList<LuaLink>(2);
	}
	
	//Functions
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeObject(luaRunState);
		out.writeBoolean(destroyed);
		out.writeBoolean(suspended);
		out.writeInt(threadsCount);
		for (int n = 0; n < threadsCount; n++) {
			out.writeObject(threads[n]);
		}
		out.writeInt(standbyList.size());
		for (LuaLink link : standbyList) {
			out.writeObject(link);
		}
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		luaRunState = (LuaRunState)in.readObject();
		destroyed = in.readBoolean();
		suspended = in.readBoolean();
		threadsCount = in.readInt();
		for (int n = 0; n < threadsCount; n++) {
			threads[n] = (LuaLink)in.readObject();
		}
		int standbyL = in.readInt();
		for (int n = 0; n < standbyL; n++) {
			standbyList.add((LuaLink)in.readObject());
		}
	}
	
	private void checkDestroyed() {
		if (isDestroyed()) {
			throw new IllegalStateException("Attempted to change a disposed thread group");
		}
	}
	
	public void destroy() {
		if (destroyed) return;
		
		destroyed = true;
		
		LuaLink[] garbage1 = standbyList.toArray(new LuaLink[standbyList.size()]);
		standbyList.clear();
		for (LuaLink link : garbage1) link.destroy();

		LuaLink[] garbage2 = new LuaLink[threadsCount];
		System.arraycopy(threads, 0, garbage2, 0, threadsCount);
		Arrays.fill(threads, 0, threadsCount, null);
		threadsCount = 0;
		for (LuaLink link : garbage2) link.destroy();
	}
	
	public LuaFunctionLink newThread(LuaClosure func, Varargs args) {
		checkDestroyed();
		
		LuaFunctionLink thread = new LuaFunctionLink(luaRunState, func, args);
		add(thread);
		return thread;
	}

	public LuaFunctionLink newThread(String func, Object... args) {
		checkDestroyed();
		
		LuaFunctionLink thread = new LuaFunctionLink(luaRunState, func, args);
		add(thread);
		return thread;
	}
	
	protected void ensureCapacity(int minCapacity) {
		int oldLen = (threads != null ? threads.length : -1);
		if (oldLen < 0 || oldLen < minCapacity) {
			LuaLink[] newThreads = new LuaLink[Math.max(oldLen + 8, minCapacity)];
			System.arraycopy(threads, 0, newThreads, 0, threadsCount);
			threads = newThreads;
		}
	}
	
	public void add(LuaLink link) {
		checkDestroyed();

		standbyList.add(link);
	}
	
	public void addAll(LuaThreadGroup tp) {
		checkDestroyed();

		for (int n = 0; n < tp.threadsCount; n++) {
			add(tp.threads[n]);
		}
		for (LuaLink link : tp.standbyList) {
			add(link);
		}
	}
		
	public boolean update() throws LuaException {
		checkDestroyed();

		boolean changed = false;
		
		if (!standbyList.isEmpty()) {
			ensureCapacity(threadsCount + standbyList.size());
			
			for (LuaLink link : standbyList) {
				boolean contains = false;
				for (int n = 0; n < threadsCount; n++) {
					if (threads[n] == link) {
						contains = true;
						break;
					}
				}
				if (!contains && !link.isFinished()) {
					threads[threadsCount++] = link;
				}
			}
			standbyList.clear();
		}
		
		int d = 0;
		for (int s = 0; s < threadsCount; s++) {
			LuaLink link = threads[s];
			if (!suspended && !link.isFinished()) {
				changed |= link.update();
				if (isDestroyed() && threadsCount == 0) {
					return changed;
				}
			}
			if (!link.isFinished()) {
				threads[d++] = link;
			} else {
				link.destroy();				
			}
		}
		for (int n = d; n < threadsCount; n++) {
			threads[n] = null; //Clear leftover references
		}
		threadsCount = d;
		
		return changed;
	}
	
	public void suspend() {
		setSuspended(true);
	}
	
	public void resume() {
		setSuspended(false);
	}
	
	//Getters
	public boolean isSuspended() {
		return suspended;
	}
	
	public boolean isDestroyed() {
		return destroyed;
	}
	
	public int getThreads(Collection<? super LuaLink> out) {
		int t = 0;
		for (int n = 0; n < threadsCount; n++) {
			if (threads[n] != null) {
				out.add(threads[n]);
				t++;
			}
		}
		return t;
	}
	
	public boolean isFinished() {
		for (LuaLink link : threads) {
			if (!link.isFinished()) return false;
		}
		for (LuaLink link : standbyList) {
			if (!link.isFinished()) return false;
		}
		return true;
	}
	
	//Setters
	public void setSuspended(boolean s) {
		checkDestroyed();

		suspended = s;
	}
	
}
