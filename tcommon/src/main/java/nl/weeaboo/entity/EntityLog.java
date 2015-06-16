package nl.weeaboo.entity;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.LoggingPermission;

public final class EntityLog {

	private static final Logger INSTANCE;
	
	static {
		Logger log = null;
		
		try {
			SecurityManager sm = System.getSecurityManager();
			if (sm != null) {
				sm.checkPermission(new LoggingPermission("control", ""));
			}
			
			log = Logger.getLogger("nl.weeaboo.game.entity");
			log.setLevel(Level.ALL);
		} catch (SecurityException se) {
			//Ignore
		} catch (Exception e) {
			System.err.println(e);
		}
		
		INSTANCE = (log != null ? log : Logger.getAnonymousLogger());
	}
	
	public static Logger getInstance() {
		return INSTANCE;
	}
	
	public static void v(String message) {
		v(message, null);
	}
	public static void v(String message, Throwable t) {
		INSTANCE.logp(Level.CONFIG, null, null, message, t);
	}

	public static void d(String message) {
		d(message, null);
	}
	public static void d(String message, Throwable t) {
		INSTANCE.logp(Level.INFO, null, null, message, t);
	}
	
	public static void w(String message) {
		w(message, null);
	}
	public static void w(String message, Throwable t) {
		INSTANCE.logp(Level.WARNING, null, null, message, t);
	}

	public static void e(String message) {
		e(message, null);
	}
	public static void e(String message, Throwable t) {
		INSTANCE.logp(Level.SEVERE, null, null, message, t);
	}
		
}
