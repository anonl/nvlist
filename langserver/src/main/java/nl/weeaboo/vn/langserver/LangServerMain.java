package nl.weeaboo.vn.langserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Closer;

import nl.weeaboo.vn.impl.InitConfig;

final class LangServerMain {

    private static final Logger LOG = LoggerFactory.getLogger(LangServerMain.class);

    public static void main(String[] args) throws IOException {
        InitConfig.init();

        Closer closer = Closer.create();

        ServerSocket serverSocket = new ServerSocket(12345);
        closer.register(serverSocket);

        Socket socket = serverSocket.accept();
        closer.register(socket);

        NvlistLangServer.start(socket.getInputStream(), socket.getOutputStream());

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                closer.close();
            } catch (IOException e) {
                LOG.warn("Error closing socket", e);
            }
        }));

        // NvlistLangServer.start(System.in, System.out);
    }

}
