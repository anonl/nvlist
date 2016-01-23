package nl.weeaboo.lua2.io;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

public class ObjectSerializer extends ObjectOutputStream {

	public enum PackageLimit {
		NONE, WARNING, ERROR;
	}

	private static final boolean COLLECT_STATS = false;

	private final Environment env;
	private final Set<String> validPackages;
	private final Set<Class<?>> validClasses;

	private PackageLimit packageLimit = PackageLimit.ERROR;
	private boolean checkTypes;

	private List<String> errors;
	private List<String> warnings;
	private final Map<Class<?>, Stats> classCounter;
	//private int maxStackDepth = 0;

	protected ObjectSerializer(OutputStream out, Environment e) throws IOException {
		super(out);

		env = (e.size() != 0 ? e : null);

		validPackages = new HashSet<String>();
		validClasses = new HashSet<Class<?>>();
		resetValidClasses();

		errors = new ArrayList<String>();
		warnings = new ArrayList<String>();

		if (COLLECT_STATS) {
			classCounter = new IdentityHashMap<Class<?>, Stats>();
		} else {
			classCounter = null;
		}

		onPackageLimitChanged();
	}

	//Functions
	public static String toErrorString(Collection<String> errors) {
		StringBuilder sb = new StringBuilder();
		sb.append(errors.size() + " error(s) occurred while writing objects:");

		int t = 1;
		for (String err : errors) {
			sb.append(String.format("\n%d: %s", t, err));
			t++;
		}
		return sb.toString();
	}

	/**
	 * @return An array containing all warnings encountered during serialization.
	 */
	public String[] checkErrors() {
		if (errors.size() > 0) {
			String errorString = toErrorString(errors);
			errors.clear();
			throw new RuntimeException(errorString);
		}

		if (COLLECT_STATS) {
			Entry<Class<?>, Stats> entries[] = classCounter.entrySet().toArray(new Entry[classCounter.size()]);
			Arrays.sort(entries, new Comparator<Entry<Class<?>, Stats>>() {
				@Override
				public int compare(Entry<Class<?>, Stats> e1, Entry<Class<?>, Stats> e2) {
					return -e1.getValue().compareTo(e2.getValue());
				}
			});
			for (Entry<Class<?>, Stats> entry : entries) {
				System.out.printf("%s :: %s\n", entry.getKey().getName(), entry.getValue());
			}
		}

		String warn[] = warnings.toArray(new String[warnings.size()]);
		warnings.clear();
		return warn;
	}

	@Override
	protected Object replaceObject(Object obj) {
		Class<?> clazz = obj.getClass();

		//System.out.print(Thread.currentThread().getStackTrace().length + " " + clazz);

		//Updating stats
		if (COLLECT_STATS) {
			Stats stats = classCounter.get(clazz);
			if (stats == null) {
				stats = new Stats();
			}
			stats.count++;
			classCounter.put(clazz, stats);
		}

		//Environment
		if (env != null) {
			Long id = env.getId(obj);
			if (id != null) {
				return new RefEnvironment(id);
			}
		}

		if (checkTypes) {
			//Whitelisted types
			if (clazz.getAnnotation(LuaSerializable.class) != null) {
				return obj; //Whitelist types with the LuaSerializable annotation
			} else if (clazz.isArray()) {
				return obj; //Whitelist array types
			} else if (clazz.isEnum()) {
				return obj; //Whitelist enum types
			}

			String className = clazz.getName();
			if (className.startsWith("java.util") &&
				(Collection.class.isAssignableFrom(clazz)) || Map.class.isAssignableFrom(clazz))
			{
				//Whitelist collections from java.util
				return obj;
			}

			String packageName = className;
			for (int n = packageName.length()-1; n >= 0; n--) {
				if (packageName.charAt(n) == '.') {
					packageName = packageName.substring(0, n);
					break;
				}
			}

            if (packageLimit != PackageLimit.NONE && !validClasses.contains(clazz)
                    && !validPackages.contains(packageName)) {

				String message = "Class outside valid packages: " + clazz.getName() + " :: " + obj;
				if (packageLimit == PackageLimit.ERROR) {
					errors.add(message);
					return null; //Don't serialize object in case of error
				} else if (packageLimit == PackageLimit.WARNING) {
					warnings.add(message);
				}
			}
		}

		return obj;
	}

    protected Thread createThread(ThreadGroup g, Runnable r, String name, int stackSizeHint) {
		return new Thread(g, r, name, stackSizeHint);
	}
	public void writeObjectOnNewThread(final Object obj, int stackSizeHint) throws IOException {
		final Throwable errs[] = new Throwable[1];
		Thread t = createThread(null, new Runnable() {
			@Override
			public void run() {
				try {
					writeObject(obj);
				} catch (Throwable t) {
					errs[0] = t;
				}
			}
		}, getClass() + "-WriterThread", stackSizeHint);
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
	}

	private void resetValidPackages() {
		validPackages.clear();
	}

	private void resetValidClasses() {
		validClasses.clear();

		final Class<?> prims[] = {Boolean.class, Byte.class, Short.class, Integer.class,
				Long.class, Float.class, Double.class, String.class};

		validClasses.addAll(Arrays.asList(prims));
		validClasses.add(Class.class);
		validClasses.add(Random.class);
		validClasses.add(BitSet.class);
	}

	private void onPackageLimitChanged() {
		checkTypes = (packageLimit == PackageLimit.WARNING || packageLimit == PackageLimit.ERROR);

		updateEnableReplace();
	}

	private void updateEnableReplace() {
		boolean replace = (env != null || checkTypes || COLLECT_STATS);
		try {
			enableReplaceObject(replace);
		} catch (SecurityException se) {
			//Ignore
		}
	}

	//Getters
	public PackageLimit getPackageLimit() {
		return packageLimit;
	}
	public String[] getAllowedPackages() {
		return validPackages.toArray(new String[validPackages.size()]);
	}
	public Class<?>[] getAllowedClasses() {
		return validClasses.toArray(new Class<?>[validClasses.size()]);
	}

	//Setters
	public void setPackageLimit(PackageLimit pl) {
		if (packageLimit != pl) {
			packageLimit = pl;

			onPackageLimitChanged();
		}
	}
	public void setAllowedPackages(String... packages) {
		resetValidPackages();
		for (String pkgString : packages) {
			validPackages.add(pkgString);
		}
	}
	public void setAllowedClasses(Class<?>... classes) {
		resetValidClasses();
		validClasses.addAll(Arrays.asList(classes));
	}

	//Inner Classes
	private static class Stats implements Comparable<Stats> {

		public int count;

		@Override
		public int compareTo(Stats s) {
			return count - s.count;
		}

		@Override
		public String toString() {
			return String.format("%d", count);
		}

	}

}
