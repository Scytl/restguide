package com.scytl.rest.upload;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TemporaryFolder {

	private final File _parentFolder;
	private File _folder;

	public TemporaryFolder() {
		this(null);
	}

	public TemporaryFolder(final File parentFolder) {
		_parentFolder = parentFolder;
		try {
			_folder = createTemporaryFolderIn(_parentFolder);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * Returns a new fresh file with the given name under the temporary folder.
	 */
	public Path newFile(final String fileName) throws IOException {
		File file = new File(getRoot(), fileName);
		if (!file.createNewFile()) {
			throw new IOException("a file with the name \'" + fileName
					+ "\' already exists in the test folder");
		}
		return file.toPath();
	}

	/**
	 * Returns a new fresh file with a random name under the temporary folder.
	 */
	public Path newFile() throws IOException {
		return File.createTempFile("temp", null, getRoot()).toPath();
	}

	/**
	 * @return the location of this temporary folder.
	 */
	public File getRoot() {
		if (_folder == null) {
			throw new IllegalStateException(
					"the temporary folder has not yet been created");
		}
		return _folder;
	}

	private File createTemporaryFolderIn(File parentFolder) throws IOException {
		File createdFolder = File.createTempFile("junit", "", parentFolder);
		createdFolder.delete();
		createdFolder.mkdir();
		return createdFolder;
	}

}
