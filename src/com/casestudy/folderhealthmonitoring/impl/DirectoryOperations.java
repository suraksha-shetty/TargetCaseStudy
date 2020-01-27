package com.casestudy.folderhealthmonitoring.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DirectoryOperations {

	private List<File> files;

	public DirectoryOperations(List<File> files) {
		super();
		this.files = files;
	}

	public List<File> getFiles() {
		return files;
	}

	public List<String> getFileNames() {
		List<String> fileNames = new ArrayList();
		for (File f : this.files) {
			fileNames.add(f.getAbsolutePath());
		}
		return fileNames;
	}

	public static DirectoryOperations getAllFilesInDirectoryRecursively(String watchDirName) {
		return new DirectoryOperations(getAllFilesInDirectoryRecursively(watchDirName, true));
	}

	private static List<File> getAllFilesInDirectoryRecursively(String dirName, boolean recursive) {
		List<File> results = new ArrayList();
		File[] files = new File(dirName).listFiles();
		files = files == null ? new File[] {} : files;
		for (File file : files) {
			if (file.isFile()) {
				results.add(file);
			} else if (recursive)
				results.addAll(getAllFilesInDirectoryRecursively(file.getAbsolutePath(), true));
		}
		return results;
	}

	public long getFormattedSize() {
		long size = this.getSizeInMB();
		return size;
	}

	public long getSizeInMB() {
		long totalSizeInBytes = 0;
		for (File f : this.files) {
			totalSizeInBytes += f.length();
		}
		return totalSizeInBytes / (1024 * 1024);
	}
}
