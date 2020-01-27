package com.casestudy.folderhealthmonitoring.processors;

import com.casestudy.folderhealthmonitoring.impl.MonitorProcess;

public interface HealthMonitorProcessor {

	public HealthMonitorProcessResult process(MonitorProcess request);

}
