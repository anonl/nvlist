package nl.weeaboo.lua2.interpreter;

import static org.luaj.vm2.LuaValue.NIL;
import static org.luaj.vm2.LuaValue.NILS;
import static org.luaj.vm2.LuaValue.NONE;

import org.luaj.vm2.LuaValue;

final class StackFrameAllocator {

	/*
	private static long time0 = System.nanoTime();
	private static int stackCreated = 0;
	private static int stackReused = 0;
	*/
	
	private static final int DEFAULT_FRAME_CACHE_SIZE = 4;
	private static final int DEFAULT_ARRAY_CACHE_SIZE = 4;
	
	private StackFrame[] cachedFrames;
	private LuaValue[][] cachedArrays;
	
	public StackFrameAllocator() {
		cachedFrames = new StackFrame[DEFAULT_FRAME_CACHE_SIZE];
		cachedArrays = new LuaValue[DEFAULT_ARRAY_CACHE_SIZE][];
	}
	
	/*
	private static void logCreateStack(int len) {
		stackCreated += len;
		long dt = System.nanoTime() - time0;
		System.out.printf("create stack: %d (total %d, avg=%.2f/s)\n", len, stackCreated, stackCreated / (dt / 1000000000.0));
	}
	
	private static void logReuseStack(int len) {
		stackReused += len;
		long dt = System.nanoTime() - time0;
		System.out.printf("reuse stack: %d (total %d, avg=%.2f/s)\n", len, stackReused, stackReused / (dt / 1000000000.0));
	}
	*/
	
	static void clearFrame(StackFrame frame) {
		frame.close();
		
		//Clear any remaining references
		frame.c = null;
		frame.args = NONE;
		frame.varargs = NONE;
		frame.parent = null;
		frame.v = NONE;		
	}
	
	int reuse = 0;
	int alloc = 0;
	
	@SuppressWarnings("deprecation")
	public StackFrame takeFrame() {
		for (int n = 0; n < cachedFrames.length; n++) {
			if (cachedFrames[n] != null) {
				StackFrame frame = cachedFrames[n];
				cachedFrames[n] = null;
				reuse++;
				return frame;
			}
		}
		alloc++;
		return new StackFrame();
	}
	
	public void giveFrame(StackFrame frame) {
		for (int n = 0; n < cachedFrames.length; n++) {
			if (cachedFrames[n] == null) {
				clearFrame(frame);
				cachedFrames[n] = frame;
				return;
			}
		}
		
		//Cache full
	}
	
	static void clearArray(LuaValue[] stack) {
		if (stack.length < 32) {
			for (int n = 0; n < stack.length; n++) {
				stack[n] = NIL;
			}
		} else {
			for (int n = 0; n < stack.length; n += NILS.length) {
				System.arraycopy(NILS, 0, stack, n, Math.min(stack.length-n, NILS.length));
			}
		}		
	}
	
	public LuaValue[] takeArray(int size) {
		//Find best cached array (if any)
		int bestIndex = -1;
		int bestLength = Integer.MAX_VALUE;
		for (int n = 0; n < cachedArrays.length; n++) {
			LuaValue[] c = cachedArrays[n];
			if (c != null && c.length >= size && c.length < bestLength) {
				bestIndex = n;
				bestLength = c.length;
			}
		}
		
		//Return result, or allocate a new stack if necessary
		if (bestIndex >= 0) {
			//logReuseStack(size);
			LuaValue[] result = cachedArrays[bestIndex];
			cachedArrays[bestIndex] = null;
			return result;
		} else {
			//logCreateStack(size);
			LuaValue[] result = new LuaValue[size];
			clearArray(result);
			return result;
		}
	}
	
	public void giveArray(LuaValue[] v) {
		int worstIndex = -1;
		int worstLength = Integer.MAX_VALUE;
		for (int n = 0; n < cachedArrays.length; n++) {
			LuaValue[] c = cachedArrays[n];
			if (c == null) {
				worstIndex = n;
				break; //Nothing is better than an empty slot
			} else if (c.length < v.length && c.length < worstLength) {
				worstIndex = n;
				worstLength = c.length;
			}
		}
		
		if (worstIndex >= 0) {
			//We want to store the array in our reuse-cache
			clearArray(v);
			cachedArrays[worstIndex] = v;
		}
	}
	
}
