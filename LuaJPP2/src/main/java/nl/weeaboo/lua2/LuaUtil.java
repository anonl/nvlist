package nl.weeaboo.lua2;

import static org.luaj.vm2.LuaValue.NIL;
import static org.luaj.vm2.LuaValue.NONE;
import static org.luaj.vm2.LuaValue.valueOf;
import static org.luaj.vm2.LuaValue.varargsOf;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import org.luaj.vm2.LoadState;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.BaseLib;

import nl.weeaboo.lua2.lib.LuajavaLib;
import nl.weeaboo.lua2.link.LuaLink;

public final class LuaUtil {

	private static final LuaString NEW = valueOf("new");

	private LuaUtil() {
	}

	public static void registerClass(LuaValue globals, Class<?> c) {
		registerClass(globals, c, c.getSimpleName());
	}
	public static <T> void registerClass(LuaValue globals, Class<T> c, String tableName) {
		LuaTable table = new LuaTable();
		if (c.isEnum()) {
			for (T val : c.getEnumConstants()) {
				Enum<?> e = (Enum<?>)val;
				table.rawset(e.name(), LuajavaLib.toUserdata(e, c));
			}
		} else {
			table.rawset(NEW, new LuajavaLib.ConstrFunction(c));
		}
		globals.rawset(tableName, table);
	}

	public static Varargs initModule(InputStream in, String filename) throws LuaException {
		try {
			Varargs result = BaseLib.loadStream(in, filename);
			if (!result.arg1().isfunction()) {
				throw new LuaException(result.arg(2).tojstring());
			}
			return result.arg1().invoke();
		} catch (LuaError le) {
			throw new LuaException(le);
		}
	}

    public static Varargs eval(LuaLink thread, String code) throws LuaException {
		final String chunkName = "(eval)";
		final LuaValue env = thread.getThread().getfenv();

		try {
			ByteArrayInputStream bin = new ByteArrayInputStream(("return " + code).getBytes("UTF-8"));

			Varargs result = NONE;
			try {
				//Try to evaluate as an expression
				result = LoadState.load(bin, chunkName, env);
			} catch (LuaError err) {
				//Try to evaluate as a statement, no value to return
				bin.reset();
				bin.skip(7); //Skip "return "
				result = LoadState.load(bin, chunkName, env);
			}

			LuaValue f = result.arg1();
			if (!f.isclosure()) {
				throw new LuaException(result.arg(2).tojstring());
			}
			return thread.call(f.checkclosure());
		} catch (RuntimeException e) {
			throw new LuaException(e);
		} catch (IOException e) {
			throw new LuaException(e);
		}
	}

	public static Varargs copyArgs(LuaValue[] stack, int offset, int length) {
		if (length <= 0) return NONE;

		LuaValue[] array = new LuaValue[length];
		for (int n = 0; n < length; n++) {
			array[n] = stack[offset+n];
		}
		return varargsOf(array);
	}

	public static Varargs copyArgs(LuaValue[] stack, int offset, int length, Varargs extra) {
		if (length <= 0) {
			return copyArgs(extra);
		}

        if (extra == null) {
            extra = NONE;
        }
        int extraL = extra.narg();

		LuaValue[] array = new LuaValue[length + extraL];
		for (int n = 0; n < length; n++) {
			array[n] = stack[offset+n];
		}
        for (int n = 0; n < extraL; n++) {
            array[length + n] = extra.arg(1 + n);
		}
		return varargsOf(array);
	}

	public static Varargs copyArgs(Varargs in) {
		if (in == null) return null;
		final int inL = in.narg();
		if (inL == 0) return NONE;

		LuaValue[] array = new LuaValue[inL];
		for (int n = 0; n < array.length; n++) {
			array[n] = in.arg(1+n);
		}
		return LuaValue.varargsOf(array);
	}

	public static void printGlobals(LuaTable table) {
		printGlobals0(new HashSet<LuaTable>(), table, "");
	}
	private static void printGlobals0(Set<LuaTable> printedTables, LuaTable t, String prefix) {
		Varargs n;
		LuaValue k = NIL;
		while (!(k = ((n = t.next(k)).arg1())).isnil()) {
			LuaValue v = n.arg(2);
			System.out.println(prefix + k + "=" + v);
			if (v.istable() && printedTables.add(v.checktable())) {
				printGlobals0(printedTables, v.checktable(), prefix + "  ");
			}
		}
	}

	/**
	 * Turns the given string containing Lua source code declaring a simple
	 * constant of type boolean, number or string into the proper LuaValue
	 * subclass.
	 */
	public static LuaValue parseLuaLiteral(String code) {
		if ("true".equals(code)) {
			return valueOf(true);
		} else if ("false".equals(code)) {
			return valueOf(false);
		} else if (code.length() >= 2 && code.startsWith("\"") && code.endsWith("\"")) {
			return valueOf(unescape(code.substring(1, code.length()-1)));
		} else if (code.length() >= 2 && code.startsWith("'") && code.endsWith("'")) {
			return valueOf(unescape(code.substring(1, code.length()-1)));
		}

		try {
			if (code.startsWith("0x")) {
				return valueOf(Long.parseLong(code.substring(2), 16));
			} else {
				return valueOf(Double.parseDouble(code));
			}
		} catch (NumberFormatException nfe) {
			//Can't be parsed as a number
		}

		return NIL;
	}

	static final char escapeList[] = new char[] {
		'\"', '\"',
		'\'', '\'',
		'\\', '\\',
		'n', '\n',
		'r', '\r',
		't', '\t',
		'f', '\f',
		'a', '\u0007',
		'b', '\b',
		'v', '\u000B',
	};

	public static String escape(String s) {
		StringBuilder sb = new StringBuilder(s.length());
		escape(sb, s);
		return sb.toString();
	}
	public static void escape(StringBuilder out, String s) {
		if (s == null || s.length() == 0) {
			return;
		}

		for (int n = 0; n < s.length(); n++) {
			char c = s.charAt(n);

			int t;
			for (t = 0; t < escapeList.length; t+=2) {
				if (c == escapeList[t+1]) {
					out.append('\\');
					out.append(escapeList[t]);
					break;
				}
			}
			if (t >= escapeList.length) {
				out.append(c);
			}
		}
	}

	public static String unescape(String s) {
		char chars[] = new char[s.length()];
		s.getChars(0, chars.length, chars, 0);

		int t = 0;
		for (int n = 0; n < chars.length; n++) {
			if (chars[n] == '\\') {
				n++;
				chars[t] = unescape(chars[n]);
			} else {
				chars[t] = chars[n];
			}
			t++;
		}
		return new String(chars, 0, t);
	}

	public static char unescape(char c) {
		for (int n = 0; n < escapeList.length; n+=2) {
			if (c == escapeList[n]) {
				return escapeList[n+1];
			}
		}
		return c;
	}

}
