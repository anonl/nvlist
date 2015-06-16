package nl.weeaboo.lua2.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class LuaSerializer {

	private static ThreadLocal<LuaSerializer> serializers = new ThreadLocal<LuaSerializer>();

	private Environment env;

	private List<Object> writeDelayed = new ArrayList<Object>();
	private List<DelayedReader> readDelayed = new ArrayList<DelayedReader>();

	public LuaSerializer() {
		env = new Environment();
	}

	//Functions
	protected void serializerInitAsync() {
		serializers.set(LuaSerializer.this);
	}

	public int writeDelayed(Object obj) {
		writeDelayed.add(obj);
		return writeDelayed.size();
	}

	public int readDelayed(DelayedReader reader) {
		readDelayed.add(reader);
		return readDelayed.size();
	}

	public ObjectSerializer openSerializer(OutputStream out) throws IOException {
		ObjectSerializer oout = new ObjectSerializer(out, env) {

			int delayedWritten = 0;

			private void writeDelayed() throws IOException {
				while (delayedWritten < writeDelayed.size()) {
					writeObject(writeDelayed.get(delayedWritten++));
				}
			}

			@Override
            protected Thread createThread(ThreadGroup g, final Runnable r, String name, int stackSizeHint) {
				Runnable r2 = new Runnable() {
					@Override
					public void run() {
						serializerInitAsync();
						r.run();
						try {
							writeDelayed();
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
					}
				};
				return super.createThread(g, r2, name, stackSizeHint);
			}

			@Override
			public void close() throws IOException {
				try {
					writeDelayed();
				} finally {
					try {
						super.close();
					} finally {
						writeDelayed.clear();
						serializers.set(null);
					}
				}
			}
		};
		serializerInitAsync();
		return oout;
	}

	public ObjectDeserializer openDeserializer(InputStream in) throws IOException {
		ObjectDeserializer oin = new ObjectDeserializer(in, env) {

			int delayedRead = 0;

			private void readDelayed() throws IOException {
				try {
					while (delayedRead < readDelayed.size()) {
						DelayedReader reader = readDelayed.get(delayedRead++);
						reader.onRead(readObject());
					}
				} catch (ClassNotFoundException cnfe) {
					throw new IOException(cnfe.toString());
				}
			}

			@Override
            protected Thread createThread(ThreadGroup g, final Runnable r, String name, int stackSizeHint) {
				Runnable r2 = new Runnable() {
					@Override
					public void run() {
						serializerInitAsync();
						r.run();
						try {
							readDelayed();
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
					}
				};
				return super.createThread(g, r2, name, stackSizeHint);
			}

			@Override
			public void close() throws IOException {
				try {
					readDelayed();
				} finally {
					try {
						super.close();
					} finally {
						readDelayed.clear();
						serializers.set(null);
					}
				}
			}
		};
		serializerInitAsync();
		return oin;
	}

	//Getters
	public static LuaSerializer getThreadLocal() {
		return serializers.get();
	}

	public Environment getEnvironment() {
		return env;
	}

	//Setters

}
