package com.casestudy.folderhealthmonitoring.processors.impl;

import com.casestudy.folderhealthmonitoring.impl.MonitorProcess;
import com.casestudy.folderhealthmonitoring.processors.HealthMonitorProcessor;
import com.casestudy.folderhealthmonitoring.processors.HealthMonitorProcessResult;
import com.casestudy.folderhealthmonitoring.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CleanupProcessor implements HealthMonitorProcessor {

	private static volatile HealthMonitorProcessor instance = new CleanupProcessor();
	private static final List<String> UNACCEPTABLE_FILE_EXTENSIONS = Arrays.asList("bat", "sh");

	synchronized static public HealthMonitorProcessor getInstance() {
		return instance;
	}
	
	private static boolean filesToBeCleaned(String fileName) {
		return UNACCEPTABLE_FILE_EXTENSIONS.contains(FileUtils.getFileExtension(fileName));
	}

	public static class CleanupFiles implements HealthMonitorProcessResult {
		List<File> files;

		public CleanupFiles(List<File> files) {
			this.files = files;
		}

		@Override
		public String getFormattedResult() {
			String message = "";
			if (this.files.size() > 0) {
				message = this.files.size() + " files were cleaned-up: " + Arrays.toString(this.files.toArray());
			} else {
				message = "No Files to cleanup!";
			}
			return message;
		}
	}

	@Override
	public HealthMonitorProcessResult process(MonitorProcess request) {
		List<File> allFiles = request.getDirectoryDetails().getFiles();
		List<File> filesToCleanUp = new ArrayList<>();
		for (File f : allFiles) {
			if (filesToBeCleaned(f.getName())) {
				filesToCleanUp.add(f);
			}
		}
		FileUtils.deleteFiles(filesToCleanUp);
		return new CleanupFiles(filesToCleanUp);
	}

}
