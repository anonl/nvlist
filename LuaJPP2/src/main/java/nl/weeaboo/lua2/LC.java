package nl.weeaboo.lua2;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.luaj.vm2.Lua;
import org.luaj.vm2.Print;
import org.luaj.vm2.Prototype;
import org.luaj.vm2.compiler.DumpState;
import org.luaj.vm2.compiler.LuaC;

/**
 * Compiler for lua files to lua bytecode.
 */
public class LC {

	private static final String version = Lua._VERSION + "Copyright (C) 2009 luaj.org";

	private static final String usage = "usage: java -cp luajpp2.jar luac [options] [filenames].\n"
			+ "Available options are:\n"
			+ "  -        process stdin\n"
			+ "  -l       list\n"
			+ "  -o name  output to file 'name' (default is \"luac.out\")\n"
			+ "  -w       (nonstandard) overwrite input files with output (ignores -o option)"
			+ "  -p       parse only\n"
			+ "  -s       strip debug information\n"
			+ "  -e       little endian format for numbers\n"
			+ "  -i<n>    number format 'n', (n=0,1 or 4, default=" + DumpState.NUMBER_FORMAT_DEFAULT + ")\n"
			+ "  -v       show version information\n" + "  --       stop handling options\n";

	private static void usageExit() {
		System.out.println(usage);
		System.exit(1);
	}

	private boolean list;
	private String output;
	private boolean parseonly;
	private boolean stripdebug;
	private boolean littleendian;
	private int numberformat;
	private boolean versioninfo;
	private boolean processing;
	private boolean multiwrite;

	private List<Throwable> errors;

	private LC() {
		errors = new ArrayList<Throwable>();

		reset0();
	}

    public static void main(String[] args) {
		LC lc = new LC();
		lc.run(args);
	}

	protected void reset() {
		reset0();
	}
	private void reset0() {
		list = false;
		output = "luac.out";
		parseonly = false;
		stripdebug = false;
		littleendian = false;
		numberformat = DumpState.NUMBER_FORMAT_DEFAULT;
		versioninfo = false;
		processing = true;
		multiwrite = false;

		errors.clear();
	}

	private void run(String[] args) {
		reset();

		// process args
		try {
			// get stateful args
			for (int i = 0; i < args.length; i++) {
				if (!processing || !args[i].startsWith("-")) {
					// input file - defer to next stage
				} else if (args[i].length() <= 1) {
					// input file - defer to next stage
				} else {
					switch (args[i].charAt(1)) {
					case 'l':
						list = true;
						break;
					case 'o':
						if (++i >= args.length) usageExit();
						output = args[i];
						break;
					case 'w':
						multiwrite = true;
						break;
					case 'p':
						parseonly = true;
						break;
					case 's':
						stripdebug = true;
						break;
					case 'e':
						littleendian = true;
						break;
					case 'i':
						if (args[i].length() <= 2) usageExit();
						numberformat = Integer.parseInt(args[i].substring(2));
						break;
					case 'v':
						versioninfo = true;
						break;
					case '-':
						if (args[i].length() > 2) usageExit();
						processing = false;
						break;
					default:
						usageExit();
						break;
					}
				}
			}

			// echo version
			if (versioninfo) System.out.println(version);

			processing = true;

			// open output file
			LuaRunState lrs = new LuaRunState();
			OutputStream fos = null;
			try {
				for (int i = 0; i < args.length; i++) {
					boolean isFileInput = !processing || !args[i].startsWith("-");
					boolean isStdIn = !isFileInput && args[i].length() <= 1;

					InputStream in = null;
					if (isFileInput) {
						File file = new File(args[i]);
						byte[] data = new byte[(int)file.length()];
						in = new FileInputStream(file);
						int read = 0;
						while (read < data.length) {
							int r = in.read(data, read, data.length-read);
							if (r <= 0) break;
							read += r;
						}
						in.close();
						in = new ByteArrayInputStream(data);
					} else if (isStdIn) {
						in = System.in;
					}

					if ((isFileInput || isStdIn) && (multiwrite || fos == null)) {
						if (fos != null) {
							fos.close();
						}

						String fn = output;
						if (multiwrite && isFileInput) {
							fn = args[i];
						}
						fos = new BufferedOutputStream(new FileOutputStream(fn));
					}

					if (isFileInput) {
						String chunkname = args[i].substring(0, args[i].length() - 4);
						System.out.println("Compiling Lua: " + chunkname);
						processScript(in, chunkname, fos);
					} else if (isStdIn) {
						System.out.println("Compiling Lua: stdin");
						processScript(System.in, "=stdin", fos);
					} else {
						switch (args[i].charAt(1)) {
						case 'o':
							++i;
							break;
						case '-':
							processing = false;
							break;
						}
					}
				}
			} finally {
				if (fos != null) fos.close();

				lrs.destroy();
			}
		} catch (IOException ioe) {
			errors.add(ioe);
			ioe.printStackTrace();
		}

		if (!errors.isEmpty()) {
			System.exit(2);
		}
		errors.clear();
	}

	private void processScript(InputStream script, String chunkname, OutputStream out) throws IOException {
		try {
			script.mark(4<<20);

			try {
				// create the chunk
				Prototype chunk = LuaC.compile(script, chunkname);

				// list the chunk
				if (list) Print.printCode(chunk);

				// write out the chunk
				if (!parseonly) {
					DumpState.dump(chunk, out, stripdebug, numberformat, littleendian);
				}
			} catch (Throwable t) {
				errors.add(t);
				t.printStackTrace(System.err);

				//Copy script to output without compiling.
				script.reset();
				int b;
				while ((b = script.read()) >= 0) {
					out.write(b);
				}
			}
		} finally {
			script.close();
		}
	}
}
