package com.casestudy.folderhealthmonitoring.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.casestudy.folderhealthmonitoring.processors.HealthMonitorProcessResult;
import com.casestudy.folderhealthmonitoring.processors.HealthMonitorProcessor;
import com.casestudy.folderhealthmonitoring.processors.impl.ArchivalProcessor;
import com.casestudy.folderhealthmonitoring.processors.impl.CleanupProcessor;

public class DirectoryHealthMonitor {

	private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	private static final List<HealthMonitorProcessor> dirHealthProcessors = new ArrayList();
	private static final Logger logger = Logger.getLogger(DirectoryHealthMonitor.class.getSimpleName());

	static {
		dirHealthProcessors.add(CleanupProcessor.getInstance());
		dirHealthProcessors.add(ArchivalProcessor.getInstance());
	}

	public static void schedule(final String secured, final long maxFileSize, final long timeInterval,
			final String archive) {
		Runnable task = new Runnable() {
			@Override
			public void run() {
				logger.info("Starting folder health monitor process...");
				process(secured, maxFileSize, timeInterval, archive);
				logger.info("Completion of folder health monitoring...");
			}
		};
		scheduler.scheduleAtFixedRate(task, 0, timeInterval, TimeUnit.SECONDS);
	}

	private static void process(final String watchDirName, final long maxSizeMBs, final long watchIntervalSec,
			final String archiveDirName) {
		DirectoryOperations initialFiles = DirectoryOperations.getAllFilesInDirectoryRecursively(watchDirName);
		MonitorProcess request = new MonitorProcess(watchDirName).withMaxSizeMB(maxSizeMBs)
				.withArchiveDirName(archiveDirName);
		List<HealthMonitorProcessResult> results = new ArrayList();
		for (HealthMonitorProcessor processor : dirHealthProcessors) {
			HealthMonitorProcessResult result = processor.process(request);
			results.add(result);
		}
		for (HealthMonitorProcessResult result : results) {
			logger.info(result.getFormattedResult());
		}
		DirectoryOperations finalDetailSnapshot = DirectoryOperations.getAllFilesInDirectoryRecursively(watchDirName);
		long size = finalDetailSnapshot.getFormattedSize();
		logger.info("Directory Contents Size is: " + size + "MB");
	}

}
