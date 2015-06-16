package nl.weeaboo.lua2.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

public class ObjectDeserializer extends ObjectInputStream {

	private static final boolean COLLECT_STATS = false;

	private final Environment env;

	private int maxDepth = 0;

	public ObjectDeserializer(InputStream in, Environment e) throws IOException {
		super(in);

		env = (e.size() > 0 ? e : null);

		updateEnableReplace();
	}

	//Functions
	private void updateEnableReplace() {
		boolean replace = (env != null || COLLECT_STATS);

		try {
			enableResolveObject(replace);
		} catch (SecurityException se) {
			//Ignore
		}
	}

    protected Thread createThread(ThreadGroup g, Runnable r, String name, int stackSizeHint) {
		return new Thread(g, r, name, stackSizeHint);
	}
	public Object readObjectOnNewThread(int stackSizeHint) throws IOException {
		final Throwable errs[] = new Throwable[1];
		final Object res[] = new Object[1];

		Thread t = createThread(null, new Runnable() {
			@Override
			public void run() {
				try {
					res[0] = readObject();
				} catch (Throwable t) {
					errs[0] = t;
				}
			}
		}, getClass() + "-ReaderThread", stackSizeHint);
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			throw new IOException(e.toString());
		}

		if (errs[0] instanceof IOException) {
			throw (IOException)errs[0];
		} else if (errs[0] instanceof RuntimeException) {
			throw (RuntimeException)errs[0];
		} else if (errs[0] instanceof Error) {
			throw (Error)errs[0];
		}

		return res[0];
	}

	@Override
	protected Object resolveObject(Object obj) throws IOException {
		if (COLLECT_STATS) {
			int depth = Thread.currentThread().getStackTrace().length;
			if (depth > maxDepth) {
				maxDepth = depth;
				if (depth >= 100) {
					System.out.println("--------------------");
					for (StackTraceElement se : Thread.currentThread().getStackTrace()) {
						System.out.println(se);
					}
				}
			}
			System.out.println(depth + " " + maxDepth + " " + (obj != null ? obj.getClass() : null));
		}

		Class<?> clazz = obj.getClass();
		if (clazz == RefEnvironment.class) {
			return env.get(((RefEnvironment)obj).id);
		}

		return obj;
	}

	//Getters

	//Setters

}
