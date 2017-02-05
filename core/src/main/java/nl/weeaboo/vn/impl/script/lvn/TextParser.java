package nl.weeaboo.vn.impl.script.lvn;

import static nl.weeaboo.lua2.LuaUtil.unescape;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

final class TextParser {

    private List<Token> tokens;
    private CharacterIterator input;

    public TextParser() {
        reset();
    }

    private void init(CharacterIterator itr) {
        input = itr;
        tokens = new ArrayList<>();
    }

    private void reset() {
        input = null;
        tokens = null;
    }

    public Collection<Token> tokenize(String str) {
        return tokenize(new StringCharacterIterator(str));
    }

    public Collection<Token> tokenize(CharacterIterator itr) {
        init(itr);

        doTokenize();

        // Return result and clear internal state
        Collection<Token> result = Collections.unmodifiableCollection(tokens);
        reset();
        return result;
    }

    private void doTokenize() {
        StringBuilder sb = new StringBuilder(input.getEndIndex() - input.getIndex());
        for (char c = input.current(); c != CharacterIterator.DONE; c = input.next()) {
            switch (c) {
            case '\\': {
                c = input.next();
                if (c != CharacterIterator.DONE) {
                    sb.append(unescape(c));
                } else {
                    sb.append('\\');
                }
            } break;
            case '[': {
                flushBuffered(tokens, sb);
                input.next();
                tokenizeBlock(ETokenType.COMMAND, ']');
            } break;
            case '$': {
                flushBuffered(tokens, sb);
                char next = input.next();
                if (next == '{') {
                    input.next();
                    tokenizeBlock(ETokenType.STRINGIFIER, '}');
                } else {
                    tokenizeBlock(ETokenType.STRINGIFIER, ' ');
                    if (input.current() == ' ') {
                        input.previous(); //Don't consume trailing space
                    }
                }
            } break;
            case '{': {
                flushBuffered(tokens, sb);
                input.next();
                tokenizeBlock(ETokenType.TAG, '}');
            } break;
            default:
                sb.append(c);
            }
        }
        flushBuffered(tokens, sb);
    }

    private void tokenizeBlock(ETokenType tokenType, char endChar) {
        StringBuilder sb = new StringBuilder();
        ParserUtil.findBlockEnd(input, endChar, sb);
        tokens.add(newToken(tokenType, sb));
    }

    /**
     * Adds the contents of the buffered text as a text token, then clears the string buffer.
     */
    private static void flushBuffered(List<? super Token> out, StringBuilder sb) {
        if (sb.length() == 0) {
            return; //Don't add empty tokens
        }
        Token token = newToken(ETokenType.TEXT, sb);
        sb.delete(0, sb.length());
        out.add(token);
    }

    private static Token newToken(ETokenType tokenType, CharSequence cs) {
        return new Token(tokenType, cs.toString());
    }

    public enum ETokenType {
        TEXT,
        STRINGIFIER,
        TAG,
        COMMAND;
    }

    public static class Token {

        private final ETokenType type;
        private final String text;

        public Token(ETokenType type, String text) {
            this.type = type;
            this.text = text;
        }

        public ETokenType getType() {
            return type;
        }

        public String getText() {
            return text;
        }

        @Override
        public String toString() {
            return String.format("TK(%s:%s)", type, text);
        }
    }

}
