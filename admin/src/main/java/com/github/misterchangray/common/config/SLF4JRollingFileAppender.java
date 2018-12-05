package com.github.misterchangray.common.config;

import org.apache.log4j.Priority;
import org.apache.log4j.RollingFileAppender;

public class SLF4JRollingFileAppender extends RollingFileAppender {
    @Override
    public boolean isAsSevereAsThreshold(Priority priority) {
        return this.getThreshold().equals(priority);
    }
}
