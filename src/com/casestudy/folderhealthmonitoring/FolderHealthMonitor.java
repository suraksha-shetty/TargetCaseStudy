package com.casestudy.folderhealthmonitoring;

import java.util.logging.Logger;

import com.casestudy.folderhealthmonitoring.impl.DirectoryHealthMonitor;

public class FolderHealthMonitor {

	private static final Logger logger = Logger.getLogger(FolderHealthMonitor.class.getSimpleName());

	public static void main(String[] args) {

		String secured = "/Users/Desktop/TestingMonitorHealth";
		logger.info("Monitoring the security folder: " + secured);

		long maxFileSize = Long.valueOf(100);
		logger.info("Maximum size of file allowed (in MB) : " + maxFileSize);

		long timeInterval = Long.valueOf(300);
		logger.info("Time Interval : " + timeInterval + " seconds");

		String archive = "/Users/Desktop/Archive";
		logger.info("Using archive directory: " + archive);

		DirectoryHealthMonitor.schedule(secured, maxFileSize, timeInterval, archive);
	}

}
