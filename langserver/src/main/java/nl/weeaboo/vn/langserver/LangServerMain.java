package nl.weeaboo.vn.langserver;

import java.io.IOException;

import nl.weeaboo.vn.impl.InitConfig;

final class LangServerMain {

    public static void main(String[] args) throws IOException {
        InitConfig.init();

        NvlistLangServer.start(System.in, System.out);
    }

}
