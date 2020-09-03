package nl.weeaboo.vn.langserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Closeables;
import com.google.common.io.Closer;

import nl.weeaboo.vn.impl.InitConfig;

final class LangServerMain {

    private static final Logger LOG = LoggerFactory.getLogger(LangServerMain.class);

    public static void main(String[] args) throws IOException {
        InitConfig.init();

        LOG.info("LangServerMain({})", Arrays.asList(args));

        Closer closer = Closer.create();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                closer.close();
            } catch (IOException e) {
                LOG.warn("Error closing socket", e);
            }
        }));

        startAsSocketClient(closer, Integer.parseInt(args[0]));
        // startAsSocketServer(closer);
        // NvlistLangServer.start(System.in, System.out);
    }

    private static void startAsSocketClient(Closer closer, int port) throws IOException {
        for (int attempt = 1; attempt <= 3; attempt++) {
            Socket socket = new Socket();
            closer.register(socket);
            try {
                socket.connect(new InetSocketAddress(port));

                NvlistLangServer.start(socket.getInputStream(), socket.getOutputStream());
                break;
            } catch (IOException ioe) {
                LOG.warn("Connection error (port={})", port, ioe);
                Closeables.close(socket, true);
            }

            try {
                Thread.sleep(1_000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    //    private static void startAsSocketServer(Closer closer) throws IOException {
    //        ServerSocket serverSocket = new ServerSocket(12345);
    //        closer.register(serverSocket);
    //
    //        Socket socket = serverSocket.accept();
    //        closer.register(socket);
    //
    //        NvlistLangServer.start(socket.getInputStream(), socket.getOutputStream());
    //    }

}
