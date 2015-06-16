package nl.weeaboo.entity;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import nl.weeaboo.common.StringUtil;

import org.junit.Assert;

public final class TestUtil {

	private static final int COMPRESSION_LEVEL = Deflater.BEST_SPEED;
	
	private TestUtil() {		
	}
	
	public static void serializeWorld(File file, boolean compress, World world) throws IOException {
		serialize(file, compress, world);
	}
	private static <T> void serialize(File file, boolean compress, T obj) throws IOException {		
		OutputStream raw = new BufferedOutputStream(new FileOutputStream(file));
		raw.write(compress ? 1 : 0);
		if (compress) {
			raw = new DeflaterOutputStream(raw, new Deflater(COMPRESSION_LEVEL, true));
		}
		
		ObjectOutputStream out = new ObjectOutputStream(raw);
		try {			
			out.writeObject(obj);
		} finally {
			out.close();
		}
		
		System.out.println("Serialized file size (" + file + "): " + StringUtil.formatMemoryAmount(file.length()));
	}	

	public static World deserializeWorld(File file) throws IOException, ClassNotFoundException {
		return deserialize(file, World.class);
	}
	private static <T> T deserialize(File file, Class<T> clazz) throws IOException, ClassNotFoundException {		
		InputStream raw = new BufferedInputStream(new FileInputStream(file));
		int compress = raw.read();
		if (compress == 1) {
			raw = new InflaterInputStream(raw, new Inflater(true));
		}

		T result;
		ObjectInputStream in = new ObjectInputStream(raw);
		try {
			result = clazz.cast(in.readObject());
		} finally {
			in.close();
		}
		return result;
	}
	
	public static void assertEntitiesEqual(List<Entity> aEntities, List<Entity> bEntities) {
		Assert.assertEquals(aEntities.size(), bEntities.size());
		for (int n = 0; n < Math.min(aEntities.size(), bEntities.size()); n++) {
			assertEntitiesEqual(aEntities.get(n), bEntities.get(n));
		}
	}
	
	public static void assertEntitiesEqual(Entity a, Entity b) {
		List<Part> aParts = new ArrayList<Part>();
		Collections.addAll(aParts, a.parts());
		
		List<Part> bParts = new ArrayList<Part>();
		Collections.addAll(bParts, b.parts());
		
		Assert.assertEquals(aParts.size(), bParts.size());
		for (int n = 0; n < Math.min(aParts.size(), bParts.size()); n++) {
			assertPartsEqual(aParts.get(n), bParts.get(n));
		}
	}
	
	public static void assertPartsEqual(Part a, Part b) {
		Assert.assertEquals(a.getClass(), b.getClass());
		Assert.assertEquals(a.refcount, b.refcount);
	}
	
}
