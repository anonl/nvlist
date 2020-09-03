package nl.weeaboo.vn.langserver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import nl.weeaboo.vn.impl.script.lvn.gen.LuaBaseVisitor;
import nl.weeaboo.vn.impl.script.lvn.gen.LuaLexer;
import nl.weeaboo.vn.impl.script.lvn.gen.LuaParser;
import nl.weeaboo.vn.impl.script.lvn.gen.LuaParser.FunctionContext;
import nl.weeaboo.vn.impl.script.lvn.gen.LuaParser.LocalFunctionContext;
import nl.weeaboo.vn.impl.script.lvn.gen.LuaParser.NamelistContext;
import nl.weeaboo.vn.impl.script.lvn.gen.LuaParser.ParlistContext;

final class LuaSourceMap extends SourceMap {

    private final List<LuaLine> lines = new ArrayList<>();
    private final ParseTree parseTree = new ParseTree();

    public LuaSourceMap(String uri, String luaCode) {
        super(uri);

        int lastLineEnd = 0;
        StringBuilder sb = new StringBuilder();
        for (int n = 0; n < luaCode.length(); n++) {
            char c = luaCode.charAt(n);
            sb.append(c);

            if (c == '\n') {
                lines.add(new LuaLine(lines.size(), lastLineEnd, sb.toString()));
                lastLineEnd = n + 1;
                sb.delete(0, sb.length());
            }
        }
        if (sb.length() > 0) {
            lines.add(new LuaLine(lines.size(), lastLineEnd, sb.toString()));
        }
    }

    public static LuaSourceMap from(String uri, String luaCode) {
        LuaSourceMap sourceMap = new LuaSourceMap(uri, luaCode);

        LuaLexer lexer = new LuaLexer(CharStreams.fromString(luaCode));
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        LuaParser parser = new LuaParser(tokenStream);
        parser.addParseListener(new ParseTreeListener() {
            @Override
            public void visitTerminal(TerminalNode node) {
            }

            @Override
            public void visitErrorNode(ErrorNode node) {
            }

            @Override
            public void enterEveryRule(ParserRuleContext ctx) {
            }

            @Override
            public void exitEveryRule(ParserRuleContext ctx) {
                int startIndex = ctx.getSourceInterval().a;
                int stopIndex = ctx.getSourceInterval().b;

                String ruleName = parser.getRuleNames()[ctx.getRuleIndex()];
                for (int n = startIndex; n <= stopIndex; n++) {
                    LuaLine line = lineAtCharOffsetInFile(sourceMap.lines, n);
                    if (line != null) {
                        int i = n - line.charOffsetInFile;
                        if (Strings.isNullOrEmpty(line.ruleNames[i])) {
                            // Don't overwrite so we always store the most specific name
                            line.ruleNames[i] = ruleName;
                        }
                    }
                }
            }

        });

        LuaVisitor visitor = new LuaVisitor(sourceMap, tokenStream);
        visitor.visit(parser.chunk());

        return sourceMap;
    }

    @Override
    protected @Nullable LuaLine lineAt(int lineOffset) {
        if (lineOffset < 0 || lineOffset >= lines.size()) {
            return null;
        }
        return lines.get(lineOffset);
    }

    @Override
    protected List<LuaLine> getLines() {
        return lines;
    }

    @Override
    protected @Nullable Range getDefinitionAt(Position pos) {
        LuaLine line = lineAt(pos.getLine());
        if (line == null) {
            return null;
        }

        String wordAt = line.getWordAt(pos.getCharacter());
        Function function = getFunction(wordAt);
        if (function != null) {
            return function.headerRange;
        }

        return null;
    }

    @Override
    protected @Nullable Function getFunction(String wordAt) {
        return parseTree.getFunction(wordAt);
    }


    private static final class LuaLine extends Line {

        final String[] ruleNames;

        LuaLine(int lineIndex, int charOffsetInFile, String contents) {
            super(lineIndex, charOffsetInFile, contents);

            ruleNames = new String[contents.length()];
            Arrays.fill(ruleNames, "");
        }

    }

    private static final class LuaVisitor extends LuaBaseVisitor<ParseTree> {

        private final LuaSourceMap sourceMap;
        private final ParseTree parseTree;
        private final CommonTokenStream tokenStream;

        public LuaVisitor(LuaSourceMap sourceMap, CommonTokenStream tokenStream) {
            this.sourceMap = sourceMap;
            this.parseTree = sourceMap.parseTree;
            this.tokenStream = tokenStream;
        }

        @Override
        public ParseTree visitFunction(FunctionContext ctx) {
            Range headerRange = SourceMap.mergeRanges(sourceMap.range(ctx.funcname()),
                    sourceMap.range(ctx.funcbody().funcparams()));
            Range bodyRange = sourceMap.range(ctx);

            Function f = new Function(ctx.funcname().getText(), headerRange, bodyRange);

            // Include preceding comments as part of the function
            StringBuilder headerComments = new StringBuilder();
            List<Token> headerCommentTokens = tokenStream.getHiddenTokensToLeft(ctx.start.getTokenIndex());
            if (headerCommentTokens != null) {
                for (Token token : headerCommentTokens) {
                    String text = token.getText();
                    headerComments.append(text);
                }
            }
            f.headerComment = cleanupCommentBlock(headerComments.toString());

            ParlistContext parlist = ctx.funcbody().funcparams().parlist();
            if (parlist != null) {
                NamelistContext namelist = parlist.namelist();
                if (namelist != null) {
                    for (TerminalNode paramName : namelist.NAME()) {
                        f.paramNames.add(paramName.getText());
                    }
                }
            }
            parseTree.functions.add(f);

            return super.visitFunction(ctx);
        }

        @Override
        public ParseTree visitLocalFunction(LocalFunctionContext ctx) {
            return super.visitLocalFunction(ctx);
        }

    }

    private static String cleanupCommentBlock(String commentBlock) {
        // TODO: Parse this using a dedicated Lua comment parser
        List<String> result = new ArrayList<>();
        for (String line : Splitter.on('\n').split(commentBlock)) {
            // Strip leading --/---, then trim the line
            line = line.replaceAll("^-+", "").trim();
            result.add(line);
        }
        return Joiner.on('\n').join(result);
    }

    private static final class ParseTree {

        private final List<Function> functions = Lists.newArrayList();

        @Nullable Function getFunction(String name) {
            return Iterables.find(functions, f -> f.name.equals(name), null);
        }

    }

}
