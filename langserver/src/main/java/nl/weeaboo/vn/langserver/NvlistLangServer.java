package nl.weeaboo.vn.langserver;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import org.eclipse.lsp4j.DocumentLinkOptions;
import org.eclipse.lsp4j.InitializeParams;
import org.eclipse.lsp4j.InitializeResult;
import org.eclipse.lsp4j.ServerCapabilities;
import org.eclipse.lsp4j.TextDocumentIdentifier;
import org.eclipse.lsp4j.TextDocumentSyncKind;
import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;
import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageClientAware;
import org.eclipse.lsp4j.services.LanguageServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

import nl.weeaboo.vn.impl.core.BuiltinAssets;

/**
 * Main language server protocol implementation for NVList.
 */
public final class NvlistLangServer implements LanguageServer, LanguageClientAware, Closeable {

    static final String NVLIST_BUILTIN_SCHEME = "nvlist-builtin";

    private static final Logger LOG = LoggerFactory.getLogger(NvlistLangServer.class);

    private final NvlistTextDocumentService textDocumentService = new NvlistTextDocumentService();
    private final NvlistWorkspaceService workspaceService = new NvlistWorkspaceService();
    private Future<Void> messageHandler;

    public static void start(InputStream in, OutputStream out) throws IOException {
        LOG.info("Starting NVList language server...");

        NvlistLangServer server = new NvlistLangServer();
        Launcher<LanguageClient> launcher = new LSPLauncher.Builder<LanguageClient>()
                .setLocalService(server)
                .setRemoteInterface(LanguageClient.class)
                .setInput(in)
                .setOutput(out)
                .traceMessages(new PrintWriter(Files.newBufferedWriter(Paths.get("nvlist-langserver-trace.log"))))
                .create();
        LanguageClient peer = launcher.getRemoteProxy();
        server.connect(peer);
        server.messageHandler = launcher.startListening();
    }

    @Override
    public void connect(LanguageClient client) {
        textDocumentService.connect(client);
    }


    @Override
    public void close() {
        LOG.info("Closing NVList language server...");

        messageHandler.cancel(true);
    }

    @Override
    public CompletableFuture<InitializeResult> initialize(InitializeParams params) {
        LOG.info("Received initialize request ({})", params);

        ServerCapabilities caps = new ServerCapabilities();
        caps.setTextDocumentSync(TextDocumentSyncKind.Full);
        caps.setDefinitionProvider(true);
        caps.setHoverProvider(true);
        caps.setDocumentLinkProvider(new DocumentLinkOptions(true));

        InitializeResult result = new InitializeResult();
        result.setCapabilities(caps);
        return CompletableFuture.completedFuture(result);
    }

    @Override
    public CompletableFuture<Object> shutdown() {
        LOG.info("Received shutdown request");

        // TODO: Implement
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public void exit() {
        close();
    }

    @Override
    public NvlistTextDocumentService getTextDocumentService() {
        return textDocumentService;
    }

    @Override
    public NvlistWorkspaceService getWorkspaceService() {
        return workspaceService;
    }

    /**
     * Requests the source code of a built-in NVList script.
     */
    @JsonRequest("nvlist/builtinSource")
    public CompletableFuture<String> builtinSource(TextDocumentIdentifier docId) {
        String uri = docId.getUri();

        String expectedPrefix = NVLIST_BUILTIN_SCHEME + "://";
        Preconditions.checkArgument(uri.startsWith(expectedPrefix));
        uri = uri.substring(expectedPrefix.length());

        String contents = "";
        try {
            contents = BuiltinAssets.readString(uri);
        } catch (IOException e) {
            LOG.warn("Unable to resolve built-in script: {}", uri, e);
        }
        return CompletableFuture.completedFuture(contents);
    }

}
