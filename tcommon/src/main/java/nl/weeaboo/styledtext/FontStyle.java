package nl.weeaboo.styledtext;

public enum FontStyle {
	
	PLAIN(0), BOLD(1), ITALIC(2), BOLDITALIC(3);
	
	private int id;
	
	private FontStyle(int id) {
		this.id = id;
	}
	
	public int intValue() { return id; }

	public static FontStyle fromInt(int id) {
		for (FontStyle s : values()) {
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
	
	public static FontStyle fromString(String str) {
		for (FontStyle style : values()) {
			if (style.toString().equals(str)) {
				return style;
			}
		}
		return FontStyle.PLAIN;
	}
	
}
