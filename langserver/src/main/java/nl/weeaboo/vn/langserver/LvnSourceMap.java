package nl.weeaboo.vn.langserver;

import java.io.IOException;
import java.util.List;

import javax.annotation.Nullable;

import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.impl.script.lvn.ICompiledLvnFile;
import nl.weeaboo.vn.impl.script.lvn.LvnLine;
import nl.weeaboo.vn.impl.script.lvn.LvnMode;
import nl.weeaboo.vn.impl.script.lvn.LvnParseException;
import nl.weeaboo.vn.impl.script.lvn.LvnParser4;

final class LvnSourceMap extends SourceMap {

    private final ICompiledLvnFile lvnFile;
    private final LuaSourceMap luaSourceMap;

    public LvnSourceMap(String uri, ICompiledLvnFile lvnFile, LuaSourceMap luaSourceMap) {
        super(uri);

        this.lvnFile = lvnFile;
        this.luaSourceMap = luaSourceMap;
    }

    public static SourceMap fromFile(String uri, String contents) throws LvnParseException, IOException {
        if (uri.endsWith(".lua")) {
            return LuaSourceMap.from(uri, contents);
        } else if (uri.endsWith(".lvn")) {
            ICompiledLvnFile lvnFile = new LvnParser4().parseFile(FilePath.of(uri), contents);

            LuaSourceMap luaSourceMap = LuaSourceMap.from(uri, lvnFile.getCompiledContents());
            return new LvnSourceMap(uri, lvnFile, luaSourceMap);
        }

        throw new IOException("Unsupported file ext: " + uri);
    }

    private LvnMode lvnModeAt(int lineOffset) {
        LvnLine lvnLine = lvnFile.getLines().get(lineOffset);
        if (lvnLine == null) {
            return LvnMode.TEXT;
        }
        return lvnLine.getType();
    }

    @Override
    protected @Nullable Line lineAt(int lineOffset) {
        LvnMode type = lvnModeAt(lineOffset);
        if (type == LvnMode.CODE || type == LvnMode.MULTILINE_CODE) {
            return luaSourceMap.lineAt(lineOffset);
        }

        // TODO: Don't ignore non-Lua lines
        return null;
    }

    @Override
    protected List<? extends Line> getLines() {
        return luaSourceMap.getLines();
    }

    @Override
    public String getWordAt(Position pos) {
        LvnMode type = lvnModeAt(pos.getLine());
        if (type == LvnMode.CODE || type == LvnMode.MULTILINE_CODE) {
            return luaSourceMap.getWordAt(pos);
        } else {
            return super.getWordAt(pos);
        }
    }

    @Override
    protected @Nullable Range getDefinitionAt(Position pos) {
        LvnMode type = lvnModeAt(pos.getLine());
        if (type == LvnMode.CODE || type == LvnMode.MULTILINE_CODE) {
            return luaSourceMap.getDefinitionAt(pos);
        }
        return null;
    }

}
