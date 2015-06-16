package nl.weeaboo.lua2.interpreter;

import static org.luaj.vm2.LuaValue.NONE;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.luaj.vm2.Lua;
import org.luaj.vm2.LuaClosure;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Prototype;
import org.luaj.vm2.UpValue;
import org.luaj.vm2.Varargs;

import nl.weeaboo.lua2.io.LuaSerializable;

@LuaSerializable
public final class StackFrame implements Externalizable {

	enum Status {
		FRESH, RUNNING, PAUSED, DEAD
	}

	private static final ThreadLocal<StackFrameAllocator> allocator = new ThreadLocal<StackFrameAllocator>() {
        @Override
        protected StackFrameAllocator initialValue() {
			return new StackFrameAllocator();
		}
	};

	// --- Uses manual serialization, don't add variables ---
	Status status;
	LuaClosure c;      //The closure that's being called
	Varargs args;      //The args given
	Varargs varargs;   //The varargs part of the arguments given

	StackFrame parent; //Link to calling context
	int parentCount;   //Number of parents
	int returnBase;    //Stack offset in parent to write return values to
	int returnCount;   //Number of return values to write in parent stack

	LuaValue[] stack;
	UpValue[] openups;
	Varargs v;
	int top;
	int pc;
	// --- Uses manual serialization, don't add variables ---

	@Deprecated
	public StackFrame() {
	}

	//Functions
	/**
	 * Returns a stack frame object, possibly a re-used one from an object pool.
	 */
	public static StackFrame newInstance() {
		return allocator.get().takeFrame();
	}
	/**
	 * @see #newInstance()
	 */
	public static StackFrame newInstance(LuaClosure c, Varargs args,
			StackFrame parent, int returnBase, int returnCount)
	{
		StackFrame frame = newInstance();
		frame.prepareCall(c, args, parent, returnBase, returnCount);
		return frame;
	}

	/**
	 * Gives the stack frame back to the object pool.
	 */
	public static void release(StackFrame frame) {
		allocator.get().giveFrame(frame);
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeObject(status);
		out.writeObject(c);
		out.writeObject(args);
		out.writeObject(varargs);

		//System.out.println("stack("+top+")=" + Arrays.toString(stack));

		out.writeObject(stack);
		out.writeObject(openups);
		out.writeObject(v);
		out.writeInt(top);
		out.writeInt(pc);

		out.writeObject(parent);
		out.writeInt(parentCount);
		out.writeInt(returnBase);
		out.writeInt(returnCount);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		status = (Status)in.readObject();
		c = (LuaClosure)in.readObject();
		args = (Varargs)in.readObject();
		varargs = (Varargs)in.readObject();

		stack = (LuaValue[])in.readObject();
		openups = (UpValue[])in.readObject();
		v = (Varargs)in.readObject();
		top = in.readInt();
		pc = in.readInt();

		parent = (StackFrame)in.readObject();
		parentCount = in.readInt();
		returnBase = in.readInt();
		returnCount = in.readInt();
	}

	public void close() {
		status = Status.DEAD;

		closeUpValues();

		if (stack != null) {
			allocator.get().giveArray(stack);
			stack = null;
		}
	}

	public void closeUpValues() {
		if (openups != null) {
			for (int u = openups.length; --u >= 0;) {
				if (openups[u] != null) {
					openups[u].close();
					openups[u] = null;
				}
			}
		}
	}

	private void resetExecutionState(int minStackSize, int subFunctionCount) {
		if (stack == null || stack.length < minStackSize) {
			stack = allocator.get().takeArray(minStackSize);
		} else {
			StackFrameAllocator.clearArray(stack);
		}

		if (subFunctionCount == 0) {
			openups = null;
		} else {
			openups = new UpValue[minStackSize];
		}

		v = NONE;
		top = 0;
		pc = 0;
	}

	public int size() {
		return parentCount + 1; //(parent != null ? parentCount + 1 : 1);
	}

	//Getters
	public LuaFunction getCallstackFunction(int level) {
		StackFrame sf = this;
		while (--level >= 1) {
			sf = sf.parent;
			if (sf == null) {
				return null;
			}
		}
		return sf.c;
	}

	//Setters
	public final void prepareCall(LuaClosure c, Varargs args,
			StackFrame parent, int returnBase, int returnCount)
	{
		final Prototype p = c.getPrototype();

		this.status = Status.FRESH;
		this.c = c;
		this.args = args;
		this.varargs = (p.is_vararg != 0 ? args.subargs(p.numparams + 1) : NONE);

		this.parent = parent;
		this.parentCount = (parent != null ? parent.size() : 0);
		this.returnBase = returnBase;
		this.returnCount = returnCount;

		resetExecutionState(p.maxstacksize, p.p.length);

		//Push params on stack
		for (int i = 0; i < p.numparams; i++) {
			stack[i] = args.arg(i + 1);
		}
		if (p.is_vararg >= Lua.VARARG_NEEDSARG) {
			stack[p.numparams] = new LuaTable(args.subargs(p.numparams + 1));
		}
	}

    public final void prepareTailcall(LuaClosure c, Varargs args) {
		closeUpValues(); //We're clobbering the stack, save the upvalues first

		final Prototype p = c.getPrototype();

		//Don't change status
		this.c = c;
		this.args = args;
		this.varargs = (p.is_vararg != 0 ? args.subargs(p.numparams + 1) : NONE);

		//Don't change parent

		resetExecutionState(p.maxstacksize, p.p.length);

		//Push params on stack
		for (int i = 0; i < p.numparams; i++) {
			stack[top + i] = args.arg(i + 1);
		}
		if (p.is_vararg >= Lua.VARARG_NEEDSARG) {
			stack[p.numparams] = new LuaTable(args.subargs(p.numparams + 1));
		}
	}

}
