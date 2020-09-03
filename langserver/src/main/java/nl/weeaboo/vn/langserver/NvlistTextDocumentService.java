package nl.weeaboo.vn.langserver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.lsp4j.DefinitionParams;
import org.eclipse.lsp4j.DidChangeTextDocumentParams;
import org.eclipse.lsp4j.DidCloseTextDocumentParams;
import org.eclipse.lsp4j.DidOpenTextDocumentParams;
import org.eclipse.lsp4j.DidSaveTextDocumentParams;
import org.eclipse.lsp4j.DocumentLink;
import org.eclipse.lsp4j.DocumentLinkParams;
import org.eclipse.lsp4j.Hover;
import org.eclipse.lsp4j.HoverParams;
import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.LocationLink;
import org.eclipse.lsp4j.MarkupContent;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.TextDocumentItem;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageClientAware;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import nl.weeaboo.vn.impl.core.BuiltinAssets;
import nl.weeaboo.vn.impl.script.lvn.LvnParseException;
import nl.weeaboo.vn.langserver.SourceMap.Function;

final class NvlistTextDocumentService implements TextDocumentService, LanguageClientAware {

    private static final Logger LOG = LoggerFactory.getLogger(NvlistTextDocumentService.class);

    private final Map<String, SourceMap> filesByUri = new ConcurrentHashMap<>();

    @SuppressWarnings("unused")
    private LanguageClient peer;

    NvlistTextDocumentService() {
    }

    @Override
    public void connect(LanguageClient client) {
        this.peer = client;

        // Parse built-in scripts
        for (String relPath : BuiltinAssets.getScripts()) {
            openBuiltInScript(relPath);
        }
    }

    private void openBuiltInScript(String relPath) {
        try {
            String uri = NvlistLangServer.NVLIST_BUILTIN_SCHEME + "://" + relPath;
            filesByUri.put(uri, LvnSourceMap.fromFile(uri, BuiltinAssets.readString(relPath)));
        } catch (IOException | LvnParseException e) {
            LOG.warn("Error reading built-in script: " + relPath, e);
        }
    }

    @Override
    public CompletableFuture<List<DocumentLink>> documentLink(DocumentLinkParams params) {
        LOG.debug("documentLink({})", params);

        return CompletableFuture.completedFuture(ImmutableList.of());
    }

    @Override
    public CompletableFuture<DocumentLink> documentLinkResolve(DocumentLink params) {
        LOG.debug("documentLinkResolve({})", params);

        DocumentLink link = new DocumentLink();
        return CompletableFuture.completedFuture(link);
    }

    @Override
    public void didOpen(DidOpenTextDocumentParams params) {
        LOG.debug("didOpen({})", params);

        TextDocumentItem doc = params.getTextDocument();
        if (!doc.getUri().startsWith("nvlist-builtin://")) {
            try {
                filesByUri.put(doc.getUri(), LvnSourceMap.fromFile(doc.getUri(), doc.getText()));
            } catch (LvnParseException | IOException e) {
                LOG.warn("Unable to parse file: {}", doc.getUri(), e);
            }
        }
    }

    @Override
    public void didChange(DidChangeTextDocumentParams params) {
        LOG.debug("didChange({})", params);
    }

    @Override
    public void didClose(DidCloseTextDocumentParams params) {
        LOG.debug("didClose({})", params);

        String uri = params.getTextDocument().getUri();
        if (!uri.startsWith("nvlist-builtin://")) {
            filesByUri.remove(uri);
        }
    }

    @Override
    public void didSave(DidSaveTextDocumentParams params) {
        LOG.debug("didSave({})", params);
    }

    @Override
    public CompletableFuture<Either<List<? extends Location>, List<? extends LocationLink>>> definition(
            DefinitionParams params) {

        List<Location> locations = new ArrayList<>();

        SourceMap sourceMap = filesByUri.get(params.getTextDocument().getUri());
        if (sourceMap != null) {
            Position pos = params.getPosition();

            Range localDefinitionRange = sourceMap.getDefinitionAt(pos);
            if (localDefinitionRange != null) {
                // Local definition
                Location loc = new Location();
                loc.setUri(params.getTextDocument().getUri());
                loc.setRange(localDefinitionRange);
                locations.add(loc);
            } else {
                // External definition
                String word = sourceMap.getWordAt(pos);
                for (SourceMap file : filesByUri.values()) {
                    Function function = file.getFunction(word);
                    if (function != null) {
                        Location loc = new Location();
                        loc.setUri(file.getUri());
                        loc.setRange(function.bodyRange);
                        locations.add(loc);
                    }
                }
            }
        }

        return CompletableFuture.completedFuture(Either.forLeft(locations));
    }

    @Override
    public CompletableFuture<Hover> hover(HoverParams params) {
        Hover result = new Hover();

        String hover = "";
        SourceMap sourceMap = filesByUri.get(params.getTextDocument().getUri());
        if (sourceMap != null) {
            Position pos = params.getPosition();
            String word = sourceMap.getWordAt(pos);
            for (SourceMap file : filesByUri.values()) {
                Function function = file.getFunction(word);
                if (function != null) {
                    hover = Markdown.toCodeBlock("lua", "function " + file.getText(function.headerRange))
                            + "\n---\n" + Markdown.toMarkdown(function.headerComment);
                }
            }
        }

        result.setContents(new MarkupContent("markdown", hover));
        return CompletableFuture.completedFuture(result);
    }

}
