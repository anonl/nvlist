package nl.weeaboo.vn.script.impl.lvn;

import static nl.weeaboo.lua2.LuaUtil.unescape;
import static nl.weeaboo.lua2.vm.LuaValue.valueOf;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.List;

import nl.weeaboo.collections.IntMap;
import nl.weeaboo.io.CustomSerializable;
import nl.weeaboo.lua2.vm.LuaTable;
import nl.weeaboo.lua2.vm.LuaValue;
import nl.weeaboo.lua2.vm.Varargs;
import nl.weeaboo.styledtext.MutableStyledText;
import nl.weeaboo.styledtext.StyledText;
import nl.weeaboo.styledtext.TextStyle;
import nl.weeaboo.vn.script.impl.lvn.TextParser.Token;

@CustomSerializable
public class RuntimeTextParser implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final LuaValue F_STRINGIFY = valueOf("stringify");
	public static final LuaValue F_TAG_OPEN  = valueOf("textTagOpen");
	public static final LuaValue F_TAG_CLOSE = valueOf("textTagClose");

	private final LuaValue globals;

	private transient TextParser parser;

	public RuntimeTextParser(LuaValue globs) {
		globals = globs;

		initTransients();
	}

	private void initTransients() {
		parser = new TextParser();
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();

		initTransients();
	}

	public ParseResult parse(String line) {
		// Process non-command tokens into StyledText objects.
		List<Object> processed = new ArrayList<Object>();
		StyleStack styleStack = new StyleStack();
		for (Token token : parser.tokenize(line)) {
			processToken(processed, token, styleStack);
		}

		// Second pass: collapse whitespace and process command tokens
		IntMap<String> commandMap = new IntMap<String>();
		MutableStyledText mts = new MutableStyledText();
		boolean lastCharCollapsible = true;
		for (Object obj : processed) {
			if (obj instanceof StyledText) {
				StyledText stext = (StyledText)obj;

				//Append to result string
				for (int n = 0; n < stext.length(); n++) {
					char c = stext.charAt(n);
					if (ParserUtil.isCollapsibleSpace(c)) {
						if (lastCharCollapsible) {
							continue; //Skip
						}
						lastCharCollapsible = true;
					} else {
						lastCharCollapsible = false;
					}
					mts.append(c, stext.getStyle(n));
				}
			} else if (obj instanceof Token) {
				Token token = (Token)obj;
				if (token.getType() == TextParser.ETokenType.COMMAND) {
					commandMap.put(mts.length(), token.getText());
				}
			}
		}

		return new ParseResult(mts.immutableCopy(), commandMap);
	}

	private void processToken(List<Object> out, Token token, StyleStack styleStack) {
		switch (token.getType()) {
		case COMMAND: {
			out.add(token);
		} break;
		case TEXT: {
			out.add(new StyledText(token.getText(), styleStack.getCalculatedStyle()));
		} break;
		case STRINGIFIER: {
			StyledText stext = processStringifier(token.getText());
			stext = changeBaseStyle(stext, styleStack.getCalculatedStyle());
			out.add(stext);
		} break;
		case TAG: {
			StringBuilder sb = new StringBuilder();
			CharacterIterator itr = new StringCharacterIterator(token.getText());
			ParserUtil.findBlockEnd(itr, ' ', sb);

			String tag = sb.toString().trim();
			sb.delete(0, sb.length());

			boolean isOpenTag = true;
			if (tag.startsWith("/")) {
				tag = tag.substring(1);
				isOpenTag = false;
			}

			List<String> args = new ArrayList<String>();
			if (isOpenTag) {
				//Parse list of values aaa,bbb,ccc
				int start = itr.getIndex();
				while (start < itr.getEndIndex()) {
					int end = ParserUtil.findBlockEnd(itr, ',', sb);
					if (end <= start) {
						break;
					}

					String arg = unescape(sb.toString().trim());
					sb.delete(0, sb.length());
					args.add(arg);

					start = end + 1;
				}
			}

			StyledText stext = processTextTag(tag, isOpenTag, args.toArray(new String[args.size()]), styleStack);
			stext = changeBaseStyle(stext, styleStack.getCalculatedStyle());
			out.add(stext);
		} break;
		default:
			//Ignore
		}
	}

	private static StyledText changeBaseStyle(StyledText stext, TextStyle style) {
		if (style == TextStyle.defaultInstance()) {
			return stext;
		}
		MutableStyledText mts = stext.mutableCopy();
		mts.setBaseStyle(style);
		return mts.immutableCopy();
	}

	private StyledText processStringifier(String str) {
		LuaValue func = globals.get(F_STRINGIFY);
		if (func.isnil()) {
			return StyledText.EMPTY_STRING;
		}

		Varargs result = func.invoke(valueOf(str));
		return optStyledText(result, 1);
	}

	private StyledText processTextTag(String tag, boolean isOpenTag, String[] args, StyleStack styleStack) {
		LuaValue func = globals.get(isOpenTag ? F_TAG_OPEN : F_TAG_CLOSE);
		if (func.isnil()) {
			return StyledText.EMPTY_STRING;
		}

		LuaTable argsTable = new LuaTable();
		for (int n = 0; n < args.length; n++) {
			argsTable.rawset(n+1, args[n]);
		}

		Varargs result = func.invoke(valueOf(tag), argsTable);
		if (isOpenTag) {
			TextStyle style = TextStyle.defaultInstance();
			if (result.narg() >= 2) {
				style = result.touserdata(2, TextStyle.class);
			}
			styleStack.pushWithTag(tag, style);
		} else {
			styleStack.popWithTag(tag);
		}

		return optStyledText(result, 1);
	}

	private static StyledText optStyledText(Varargs args, int index) {
		LuaValue raw = args.arg(index);
		if (raw.isnil()) {
			return StyledText.EMPTY_STRING;
		}

		if (raw.isuserdata(StyledText.class)) {
			return raw.touserdata(StyledText.class);
		}
		String str = raw.tojstring();
		return (str.equals("") ? StyledText.EMPTY_STRING : new StyledText(str));
	}

	public static final class ParseResult {

		private StyledText stext;
		private IntMap<String> commands;

		public ParseResult(StyledText stext, IntMap<String> commands) {
			this.stext = stext;
			this.commands = commands;
		}

        public StyledText getText() {
            return stext;
        }

        public IntMap<String> getCommands() {
            return commands;
        }

	}

}
