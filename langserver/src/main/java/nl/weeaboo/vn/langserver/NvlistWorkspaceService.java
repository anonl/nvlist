package nl.weeaboo.vn.langserver;

import org.eclipse.lsp4j.DidChangeConfigurationParams;
import org.eclipse.lsp4j.DidChangeWatchedFilesParams;
import org.eclipse.lsp4j.services.WorkspaceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class NvlistWorkspaceService implements WorkspaceService {

    private static final Logger LOG = LoggerFactory.getLogger(NvlistWorkspaceService.class);

    NvlistWorkspaceService() {
    }

    @Override
    public void didChangeConfiguration(DidChangeConfigurationParams params) {
        LOG.debug("disChangeConfiguration({})", params);
    }

    @Override
    public void didChangeWatchedFiles(DidChangeWatchedFilesParams params) {
        LOG.debug("didChangeWatchedFiles({})", params);
    }

}
