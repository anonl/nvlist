package nl.weeaboo.vn.core;

public enum ErrorLevel {

	VERBOSE(100),
	DEBUG(200),
	WARNING(300),
	MESSAGE(400),
	ERROR(500);
	
	private int level;
	
	private ErrorLevel(int lvl) {
		level = lvl;
	}
	
	public int getLevel() {
		return level;
	}
			
	@Override
	public String toString() {
		return name().toLowerCase();
	}
	
	public static ErrorLevel fromString(String string) {
		for (ErrorLevel el : values()) {
			if (el.toString().equalsIgnoreCase(string)) {
				return el;
			}
		}
		return null;
	}
	
}
