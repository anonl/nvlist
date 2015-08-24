/*******************************************************************************
 * Copyright (c) 2009 Luaj.org. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 ******************************************************************************/
package org.luaj.vm2.lib;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.Serializable;

import org.luaj.vm2.LoadState;
import org.luaj.vm2.Lua;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaThread;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.ResourceFinder.Resource;

import nl.weeaboo.lua2.io.LuaSerializable;
import nl.weeaboo.lua2.lib.J2sePlatform;

/**
 * Subclass of {@link LibFunction} which implements the lua basic library
 * functions.
 * <p>
 * This contains all library functions listed as "basic functions" in the lua
 * documentation for JME. The functions dofile and loadfile use the
 * {@link #FINDER} instance to find resource files. Since JME has no file system
 * by default, {@link BaseLib} implements {@link ResourceFinder} using
 * {@link Class#getResource(String)}, which is the closest equivalent on JME.
 * The default loader chain in {@link PackageLib} will use these as well.
 * <p>
 * To use basic library functions that include a {@link ResourceFinder} based on
 * directory lookup, use {@code JseBaseLib} instead.
 * <p>
 * Typically, this library is included as part of a call to either
 * JmePlatform.standardGlobals()
 * <p>
 * To instantiate and use it directly, link it into your globals table via
 * {@link LuaValue#load(LuaValue)} using code such as:
 *
 * <pre>
 * {
 * 	&#064;code
 * 	LuaTable _G = new LuaTable();
 * 	LuaThread.setGlobals(_G);
 * 	_G.load(new BaseLib());
 * 	_G.get(&quot;print&quot;).call(LuaValue.valueOf(&quot;hello, world&quot;));
 * }
 * </pre>
 *
 * Doing so will ensure the library is properly initialized and loaded into the
 * globals table.
 * <p>
 * This is a direct port of the corresponding library in C.
 *
 * @see ResourceFinder
 * @see #FINDER
 * @see LibFunction
 * @see J2sePlatform
 * @see <a
 *      href="http://www.lua.org/manual/5.1/manual.html#5.1">http://www.lua.org/manual/5.1/manual.html#5.1</a>
 */
@LuaSerializable
public class BaseLib extends OneArgFunction {

	private static final long serialVersionUID = -7626584035951180651L;

	private static InputStream STDIN = null;
	private static PrintStream STDOUT = System.out;

	/**
	 * Singleton file opener for this Java ClassLoader realm.
	 *
	 * Unless set or changed elsewhere, will be set by the BaseLib that is
	 * created.
	 */
	public static ResourceFinder FINDER;

	private static final String[] LIB2_KEYS = { "collectgarbage", // ( opt
																	// [,arg] )
																	// -> value
			"error", // ( message [,level] ) -> ERR
			"setfenv", // (f, table) -> void
	};
	private static final String[] LIBV_KEYS = { "assert", // ( v [,message] ) ->
															// v, message | ERR
			"dofile", // ( filename ) -> result1, ...
			"getfenv", // ( [f] ) -> env
			"getmetatable", // ( object ) -> table
			"load", // ( func [,chunkname] ) -> chunk | nil, msg
			"loadfile", // ( [filename] ) -> chunk | nil, msg
			"loadstring", // ( string [,chunkname] ) -> chunk | nil, msg
			"pcall", // (f, arg1, ...) -> status, result1, ...
			"xpcall", // (f, err) -> result1, ...
			"print", // (...) -> void
			"select", // (f, ...) -> value1, ...
			"unpack", // (list [,i [,j]]) -> result1, ...
			"type", // (v) -> value
			"rawequal", // (v1, v2) -> boolean
			"rawget", // (table, index) -> value
			"rawset", // (table, index, value) -> table
			"setmetatable", // (table, metatable) -> table
			"tostring", // (e) -> value
			"tonumber", // (e [,base]) -> value
			"pairs", // "pairs" (t) -> iter-func, t, nil
			"ipairs", // "ipairs", // (t) -> iter-func, t, 0
			"next", // "next" ( table, [index] ) -> next-index, next-value
			"__inext", // "inext" ( table, [int-index] ) -> next-index,
						// next-value
	};

	/**
	 * Construct a base libarary instance.
	 */
	public BaseLib() {
	}

	@Override
	public LuaValue call(LuaValue arg) {
		env.set("_G", env);
		env.set("_VERSION", Lua._VERSION);
		bind(env, BaseLib2.class, LIB2_KEYS);
		bind(env, BaseLibV.class, LIBV_KEYS);
		if (FINDER == null) FINDER = new ClassLoaderResourceFinder();
		return env;
	}

	@LuaSerializable
	static final class BaseLib2 extends TwoArgFunction {

		private static final long serialVersionUID = 7837837970113493134L;

		@Override
		public LuaValue call(LuaValue arg1, LuaValue arg2) {
			switch (opcode) {
			case 0: // "collectgarbage", // ( opt [,arg] ) -> value
				String s = arg1.checkjstring();
				if ("collect".equals(s)) {
					System.gc();
					return ZERO;
				} else if ("count".equals(s)) {
					Runtime rt = Runtime.getRuntime();
					long used = rt.totalMemory() - rt.freeMemory();
					return valueOf(used / 1024.);
				} else if ("step".equals(s)) {
					System.gc();
					return LuaValue.TRUE;
				} else {
					argerror(1, "gc op");
				}
				return NIL;
			case 1: // "error", // ( message [,level] ) -> ERR
				throw new LuaError(arg1.isnil() ? null : arg1.tojstring(), null, arg2.optint(1));
			case 2: { // "setfenv", // (f, table) -> void
				LuaTable t = arg2.checktable();
				LuaValue f = getfenvobj(arg1);
				if (!f.isfunction() && !f.isclosure()) error("'setfenv' cannot change environment of given object");
				f.setfenv(t);
				return f.isthread() ? NONE : f;
			}
			}
			return NIL;
		}
	}

	private static LuaValue getfenvobj(LuaValue arg) {
		if (arg.isfunction()) return arg;
		int level = arg.optint(1);
		arg.argcheck(level >= 0, 1, "level must be non-negative");
		if (level == 0) return LuaThread.getRunning();
		LuaThread running = LuaThread.getRunning();
		LuaValue f = running.getCallstackFunction(level);
		arg.argcheck(f != null, 1, "invalid level");
		return f;
	}

	@LuaSerializable
	static final class BaseLibV extends VarArgFunction {

		private static final long serialVersionUID = 6591094592581303456L;

		private static final LuaString NEXT = valueOf("next");
		private static final LuaString INEXT = valueOf("__inext");

		@Override
		public Varargs invoke(Varargs args) {
			switch (opcode) {
			case 0: // "assert", // ( v [,message] ) -> v, message | ERR
				if (!args.arg1().toboolean()) error(args.narg() > 1 ? args.optjstring(2, "assertion failed!")
						: "assertion failed!");
				return args;
			case 1: // "dofile", // ( filename ) -> result1, ...
			{
				Varargs v;
				if (args.isnil(1)) {
					v = BaseLib.loadStream(STDIN, "=stdin");
				} else {
					v = BaseLib.loadFile(args.checkjstring(1));
				}

				if (v.isnil(1)) {
					return error(v.tojstring(2));
				}
				return v.arg1().invoke();
			}
			case 2: // "getfenv", // ( [f] ) -> env
			{
				LuaValue f = getfenvobj(args.arg1());
				LuaValue e = f.getfenv();
				return e != null ? e : NIL;
			}
			case 3: // "getmetatable", // ( object ) -> table
			{
				LuaValue mt = args.checkvalue(1).getmetatable();
				return mt != null ? mt.rawget(METATABLE).optvalue(mt) : NIL;
			}
			case 4: // "load", // ( func [,chunkname] ) -> chunk | nil, msg
			{
				LuaValue func = args.checkfunction(1);
				String chunkname = args.optjstring(2, "function");
                StringInputStream in = new StringInputStream(func);
                try {
                    return BaseLib.loadStream(in, chunkname);
                } finally {
                    try {
                        in.close();
                    } catch (IOException e) {
                        // Ignore
                    }
                }
			}
			case 5: // "loadfile", // ( [filename] ) -> chunk | nil, msg
			{
				return args.isnil(1) ? BaseLib.loadStream(STDIN, "stdin") : BaseLib.loadFile(args
						.checkjstring(1));
			}
			case 6: // "loadstring", // ( string [,chunkname] ) -> chunk | nil,
					// msg
			{
				LuaString script = args.checkstring(1);
				String chunkname = args.optjstring(2, "string");
				return BaseLib.loadStream(script.toInputStream(), chunkname);
			}
			case 7: // "pcall", // (f, arg1, ...) -> status, result1, ...
			{
				LuaValue func = args.checkvalue(1);
				//LuaThread running = LuaThread.getRunning();
				//running.preCall(this);
				try {
					return pcall(func, args.subargs(2), null);
				} finally {
					//running.postReturn();
				}
			}
			case 8: // "xpcall", // (f, err) -> result1, ...
			{
				//LuaThread running = LuaThread.getRunning();
				//running.preCall(this);
				try {
					return pcall(args.arg1(), NONE, args.checkvalue(2));
				} finally {
					//running.postReturn();
				}
			}
			case 9: // "print", // (...) -> void
			{
				LuaThread running = LuaThread.getRunning();
				LuaValue tostring = running.getfenv().get("tostring");
				for (int i = 1, n = args.narg(); i <= n; i++) {
					if (i > 1) STDOUT.write('\t');
					LuaString s = tostring.call(args.arg(i)).strvalue();
					int z = s.indexOf((byte) 0, 0);
					try {
						s.write(STDOUT, 0, z >= 0 ? z : s.length());
					} catch (IOException e) {
						//Ignore
					}
				}
				STDOUT.println();
				return NONE;
			}
			case 10: // "select", // (f, ...) -> value1, ...
			{
				int n = args.narg() - 1;
				if (args.arg1().equals(valueOf("#"))) return valueOf(n);
				int i = args.checkint(1);
				if (i == 0 || i < -n) argerror(1, "index out of range");
				return args.subargs(i < 0 ? n + i + 2 : i + 1);
			}
			case 11: // "unpack", // (list [,i [,j]]) -> result1, ...
			{
				int na = args.narg();
				LuaTable t = args.checktable(1);
				int n = t.length();
				int i = na >= 2 ? args.checkint(2) : 1;
				int j = na >= 3 ? args.checkint(3) : n;
				n = j - i + 1;
				if (n < 0) return NONE;
				if (n == 1) return t.get(i);
				if (n == 2) return varargsOf(t.get(i), t.get(j));
				LuaValue[] v = new LuaValue[n];
				for (int k = 0; k < n; k++)
					v[k] = t.get(i + k);
				return varargsOf(v);
			}
			case 12: // "type", // (v) -> value
				return valueOf(args.checkvalue(1).typename());
			case 13: // "rawequal", // (v1, v2) -> boolean
				return valueOf(args.checkvalue(1) == args.checkvalue(2));
			case 14: // "rawget", // (table, index) -> value
				return args.checktable(1).rawget(args.checkvalue(2));
			case 15: { // "rawset", // (table, index, value) -> table
				LuaTable t = args.checktable(1);
				t.rawset(args.checknotnil(2), args.checkvalue(3));
				return t;
			}
			case 16: { // "setmetatable", // (table, metatable) -> table
				final LuaValue t = args.arg1();
				final LuaValue mt0 = t.getmetatable();
				if (mt0 != null && !mt0.rawget(METATABLE).isnil()) error("cannot change a protected metatable");
				final LuaValue mt = args.checkvalue(2);
				return t.setmetatable(mt.isnil() ? null : mt.checktable());
			}
			case 17: { // "tostring", // (e) -> value
				LuaValue arg = args.checkvalue(1);
				LuaValue h = arg.metatag(TOSTRING);
				if (!h.isnil()) return h.call(arg);
				LuaValue v = arg.tostring();
				if (!v.isnil()) return v;
				return valueOf(arg.tojstring());
			}
			case 18: { // "tonumber", // (e [,base]) -> value
				LuaValue arg1 = args.checkvalue(1);
				final int base = args.optint(2, 10);
				if (base == 10) { /* standard conversion */
					return arg1.tonumber();
				} else {
					if (base < 2 || base > 36) argerror(2, "base out of range");
					return arg1.checkstring().tonumber(base);
				}
			}
			case 19: // "pairs" (t) -> iter-func, t, nil
				LuaValue next = env.get(NEXT);
				return varargsOf(next, args.checktable(1), NIL);
			case 20: // "ipairs", // (t) -> iter-func, t, 0
				LuaValue inext = env.get(INEXT);
				return varargsOf(inext, args.checktable(1), ZERO);
			case 21: // "next" ( table, [index] ) -> next-index, next-value
				return args.checktable(1).next(args.arg(2));
			case 22: // "inext" ( table, [int-index] ) -> next-index, next-value
				return args.checktable(1).inext(args.arg(2));
			}
			return NONE;
		}
	}

    /**
     * @param errfunc is ignored, should replace thread's errfunc which it doesn't have anymore
     */
	public static Varargs pcall(LuaValue func, Varargs args, LuaValue errfunc) {
		try {
			return varargsOf(LuaValue.TRUE, func.invoke(args));
		} catch (LuaError le) {
			String m = le.getMessage();
			return varargsOf(FALSE, m != null ? valueOf(m) : NIL);
		} catch (Exception e) {
			String m = e.getMessage();
			return varargsOf(FALSE, valueOf(m != null ? m : e.toString()));
		}
	}

	/**
	 * Load from a named file, returning the chunk or nil,error of can't load
	 *
	 * @return Varargs containing chunk, or NIL,error-text on error
	 */
	public static Varargs loadFile(String filename) {
		Resource r = FINDER.findResource(filename);
		if (r == null) {
			return varargsOf(NIL, valueOf("cannot open " + filename));
		}

		try {
			return loadStream(r.in, "@" + r.canonicalName);
		} finally {
			try {
                r.close();
			} catch (IOException e) {
				//Ignore
			}
		}
	}

	public static Varargs loadStream(InputStream is, String chunkname) {
		try {
			if (is == null) return varargsOf(NIL, valueOf("not found: " + chunkname));
			LuaThread running = LuaThread.getRunning();
			return LoadState.load(is, chunkname, running.getfenv());
		} catch (Exception e) {
			String message = e.getMessage();
			if (message == null) message = "";
			return varargsOf(NIL, valueOf(message));
		}
	}

	@LuaSerializable
	private static class StringInputStream extends InputStream implements Serializable {

		private static final long serialVersionUID = 1030049277211647411L;

		LuaValue func;
		byte[] bytes;
		int offset;

		StringInputStream(LuaValue func) {
			this.func = func;
		}

		@Override
		public int read() throws IOException {
			if (func == null) return -1;

			if (bytes == null) {
				LuaValue s = func.call();
				if (s.isnil()) {
					func = null;
					bytes = null;
					return -1;
				}
				bytes = s.tojstring().getBytes();
				offset = 0;
			}
			if (offset >= bytes.length) {
				return -1;
			}
			return bytes[offset++];
		}
	}
}
