package nl.weeaboo.io;

public final class Filenames {

	private Filenames() {		
	}
	
	/** Returns the file extension */
	public static String getExtension(String path) {
		int index = path.lastIndexOf('.');
		if (index > 0 && index < path.length()) {
			return path.substring(index+1, path.length());
		}
		return "";
	}

	/** Strips the file extension */
	public static String stripExtension(String path) {
		int index = path.lastIndexOf('.');
		if (index > 0 && index < path.length()) {
			return path.substring(0, index);
		}
		return path;
	}

	public static String replaceExt(String filename, String ext) {
		return stripExtension(filename) + "." + ext;
	}
	
}
