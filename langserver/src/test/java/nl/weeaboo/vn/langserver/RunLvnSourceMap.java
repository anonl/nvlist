package nl.weeaboo.vn.langserver;

import java.io.File;
import java.io.IOException;

import org.eclipse.lsp4j.Position;

import com.google.common.io.Files;

import nl.weeaboo.common.StringUtil;
import nl.weeaboo.vn.impl.script.lvn.LvnParseException;

final class RunLvnSourceMap {

    public static void main(String[] args) throws LvnParseException, IOException {
        String contents = StringUtil.fromUTF8(Files.toByteArray(new File("../template/res/script/main.lvn")));
        SourceMap sourceMap = LvnSourceMap.fromFile("main.lvn", contents);
        System.out.println(sourceMap.getDefinitionAt(new Position(9, 15)));
    }

}
