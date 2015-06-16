package nl.weeaboo.styledtext.render;

public final class TextRenderUtil {

	private TextRenderUtil() {		
	}
		
	public static int mixColor(int src, int dst) {
		int ai = ((src>>24)&0xFF) * ((dst>>24)&0xFF) / 255;
		int ri = ((src>>16)&0xFF) * ((dst>>16)&0xFF) / 255;
		int gi = ((src>> 8)&0xFF) * ((dst>> 8)&0xFF) / 255;
		int bi = ((src    )&0xFF) * ((dst    )&0xFF) / 255;		
		return (ai<<24)|(ri<<16)|(gi<<8)|(bi);
	}
	
}
