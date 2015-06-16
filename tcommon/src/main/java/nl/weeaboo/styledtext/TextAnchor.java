package nl.weeaboo.styledtext;

public enum TextAnchor {
	
	DEFAULT(0),
	BOTTOMLEFT(1), BOTTOM(2), BOTTOMRIGHT(3),
	LEFT(4), CENTER(5), RIGHT(6),
	TOPLEFT(7), TOP(8), TOPRIGHT(9);
	
	private int id;
	
	private TextAnchor(int id) {
		this.id = id;
	}
	
	public int intValue() { return id; }

	public static TextAnchor fromInt(int id) {
		for (TextAnchor s : values()) {
			if (s.intValue() == id) {
				return s;
			}
		}
		return null;
	}
	
	@Override
	public String toString() {
		return name().toLowerCase();
	}
	
	public static TextAnchor fromString(String str) {
		for (TextAnchor a : values()) {
			if (a.toString().equals(str)) {
				return a;
			}
		}
		
		try {
			int i = Integer.parseInt(str);
			return fromInt(i);
		} catch (NumberFormatException nfe) {
			//Not an int either
		}
		
		return null;
	}
	
}
