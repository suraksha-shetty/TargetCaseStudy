package com.casestudy.folderhealthmonitoring.processors.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import com.casestudy.folderhealthmonitoring.impl.MonitorProcess;
import com.casestudy.folderhealthmonitoring.processors.HealthMonitorProcessResult;
import com.casestudy.folderhealthmonitoring.processors.HealthMonitorProcessor;
import com.casestudy.folderhealthmonitoring.utils.FileUtils;

public class ArchivalProcessor implements HealthMonitorProcessor {

	private static volatile HealthMonitorProcessor instance = new ArchivalProcessor();

	synchronized static public HealthMonitorProcessor getInstance() {
		return instance;
	}

	public static class ArchivalResult implements HealthMonitorProcessResult {
		private List<File> filesToArchive;

		public ArchivalResult(List<File> filesToArchive) {
			this.filesToArchive = filesToArchive;
		}

		@Override
		public String getFormattedResult() {
			String message = "";
			if (filesToArchive.size() > 0) {
				message = filesToArchive.size() + " files were Archived: " + Arrays.toString(filesToArchive.toArray());
			} else {
				message = "No Files to Archive!";
			}
			return message;
		}
	}

	private static HealthMonitorProcessResult archiveOldFiles(MonitorProcess request) {
		List<File> files = request.getDirectoryDetails().getFiles();
		files.sort(new Comparator<File>() {
			@Override
			public int compare(File f1, File f2) {
				return -1 * Long.compare(f1.lastModified(), f2.lastModified());
			}
		});
		long maxFileSizeBytesAllowed = request.getMaxFileSize() * 1024 * 1024;
		long sizeCounter = 0;
		int filesToRetain = 0;
		while (sizeCounter + files.get(filesToRetain).length() <= maxFileSizeBytesAllowed) {
			sizeCounter += files.get(filesToRetain).length();
			filesToRetain++;
		}
		List<File> filesToArchive = new ArrayList<>(files.subList(filesToRetain, files.size()));
		FileUtils.archive(filesToArchive, request.getArchiveFolderName());
		return new ArchivalResult(filesToArchive);
	}

	@Override
	public HealthMonitorProcessResult process(MonitorProcess request) {
		long currentSizeInMB = request.getDirectoryDetails().getSizeInMB();
		if (currentSizeInMB <= request.getMaxFileSize()) {
			return new ArchivalResult(new ArrayList());
		} else {
			return archiveOldFiles(request);
		}
	}

}
