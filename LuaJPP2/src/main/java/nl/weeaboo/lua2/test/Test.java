package nl.weeaboo.lua2.test;

import static org.luaj.vm2.LuaValue.valueOf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import org.luaj.vm2.lib.BaseLib;
import org.luaj.vm2.lib.ClassLoaderResourceFinder;

import nl.weeaboo.lua2.LuaException;
import nl.weeaboo.lua2.LuaRunState;
import nl.weeaboo.lua2.io.LuaSerializer;
import nl.weeaboo.lua2.io.ObjectDeserializer;
import nl.weeaboo.lua2.io.ObjectSerializer;
import nl.weeaboo.lua2.link.LuaLink;

class Test {

	static {
		//System.setProperty("CALLS", "true");
		//System.setProperty("TRACE", "true");
	}

	//Functions
	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException, LuaException {
		LuaRunState lrs = new LuaRunState();
		BaseLib.FINDER = new ClassLoaderResourceFinder() {

			private static final long serialVersionUID = 1986140110559674826L;

            @SuppressWarnings("resource")
			@Override
			public Resource findResource(String filename) {
				File file = new File(filename);
				if (file.exists()) {
					try {
                        return new Resource(filename, new FileInputStream(file));
					} catch (FileNotFoundException e) {
						//Ignore
					}
				}
				return super.findResource(filename);
			}
		};
		lrs.getGlobalEnvironment().get("dofile").call(valueOf("test.lua"));
		//LuaUtil.printGlobals(lrs.getGlobalEnvironment());

		LuaLink thread = lrs.newThread("test");

		final int UPDATE_LIMIT = 1;
		for (int n = 0; n < UPDATE_LIMIT && !thread.isFinished(); n++) {
			System.err.println("*** pulse ***");
			lrs.update();
			Thread.sleep(1000);
		}

		LuaSerializer ls = new LuaSerializer();
		final boolean deflate = false;

		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		double millis = 0;
		for (int n = 0; n < 1; n++) {
			long t0 = System.nanoTime();

			bout.reset();
			ObjectSerializer out = ls.openSerializer(deflate ? new DeflaterOutputStream(bout) : bout);
			//out.setPackageLimit(PackageLimit.NONE);
			out.writeObject(lrs);
			out.flush();
			out.close();

			millis = (System.nanoTime() - t0) / 1000000.0;
			String[] errors = out.checkErrors();
			for (String error : errors) {
				System.err.println(error);
			}
		}

		System.err.printf("***** %d bytes, %.2fms *************************************************************\n", bout.size(), millis);
		write("serialized.bin", bout.toByteArray());

		ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
		ObjectDeserializer in = ls.openDeserializer(deflate ? new InflaterInputStream(bin) : bin);
		lrs = (LuaRunState)in.readObject();
		in.close();

		//LuaUtil.printGlobals(lrs.getGlobalEnvironment());
		//lrs.update();
	}

	private static void write(String filename, byte[] data) throws IOException {
		FileOutputStream fout = new FileOutputStream(filename);
		try {
			 fout.write(data);
		} finally {
			fout.close();
		}
	}

}
