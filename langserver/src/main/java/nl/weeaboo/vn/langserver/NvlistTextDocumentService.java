package nl.weeaboo.vn.langserver;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.eclipse.lsp4j.DefinitionParams;
import org.eclipse.lsp4j.DidChangeTextDocumentParams;
import org.eclipse.lsp4j.DidCloseTextDocumentParams;
import org.eclipse.lsp4j.DidOpenTextDocumentParams;
import org.eclipse.lsp4j.DidSaveTextDocumentParams;
import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.LocationLink;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageClientAware;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class NvlistTextDocumentService implements TextDocumentService, LanguageClientAware {

    private static final Logger LOG = LoggerFactory.getLogger(NvlistTextDocumentService.class);

    @SuppressWarnings("unused")
    private LanguageClient peer;

    NvlistTextDocumentService() {
    }

    @Override
    public void connect(LanguageClient client) {
        this.peer = client;
    }

    @Override
    public void didOpen(DidOpenTextDocumentParams params) {
        LOG.debug("didOpen({})", params);

        // TODO: Modify LvnParser to return (optionally) a full AST instead of just a string
    }

    @Override
    public void didChange(DidChangeTextDocumentParams params) {
        LOG.debug("didChange({})", params);
    }

    @Override
    public void didClose(DidCloseTextDocumentParams params) {
        LOG.debug("didClose({})", params);
    }

    @Override
    public void didSave(DidSaveTextDocumentParams params) {
        LOG.debug("didSave({})", params);
    }

    @Override
    public CompletableFuture<Either<List<? extends Location>, List<? extends LocationLink>>> definition(
            DefinitionParams params) {

        List<Location> locations = new ArrayList<>();

        Location loc = new Location();
        // TODO: Implement properly
        loc.setUri(params.getTextDocument().getUri());
        loc.setRange(new Range(new Position(0, 0), new Position(0, 10)));
        locations.add(loc);

        return CompletableFuture.completedFuture(Either.forLeft(locations));
    }

}
