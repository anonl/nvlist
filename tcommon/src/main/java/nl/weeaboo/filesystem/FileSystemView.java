package nl.weeaboo.filesystem;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

import nl.weeaboo.common.Checks;

public class FileSystemView implements IFileSystem {

	private final IFileSystem fileSystem;
	private final String prefix;
	
	public FileSystemView(IFileSystem fileSystem, String prefix) {
		this.fileSystem = Checks.checkNotNull(fileSystem);
		this.prefix = Checks.checkNotNull(prefix);
	}
	
	@Override
	public void close() {
		fileSystem.close();
	}

	@Override
	public InputStream openInputStream(String path) throws IOException {
		return fileSystem.openInputStream(prefix + path);
	}

	@Override
	public boolean isOpen() {
		return fileSystem.isOpen();
	}

	@Override
	public boolean isReadOnly() {
		return fileSystem.isReadOnly();
	}

	public String getPrefix() {
		return prefix;
	}
	
	@Override
	public boolean getFileExists(String path) {
		return fileSystem.getFileExists(prefix + path);
	}

	@Override
	public long getFileSize(String path) throws IOException {
		return fileSystem.getFileSize(prefix + path);
	}

	@Override
	public long getFileModifiedTime(String path) throws IOException {
		return fileSystem.getFileModifiedTime(prefix + path);
	}

	@Override
	public void getFiles(Collection<String> out, String path, boolean recursive) throws IOException {
		Collection<String> temp = new ArrayList<String>();
		fileSystem.getFiles(temp, prefix + path, recursive);
		temp = FileSystemUtil.withoutPathPrefix(temp, prefix);
		out.addAll(temp);
	}

	@Override
	public void getSubFolders(Collection<String> out, String path, boolean recursive) throws IOException {
		Collection<String> temp = new ArrayList<String>();
		fileSystem.getSubFolders(temp, prefix + path, recursive);
		temp = FileSystemUtil.withoutPathPrefix(temp, prefix);
		out.addAll(temp);
	}
	
}
