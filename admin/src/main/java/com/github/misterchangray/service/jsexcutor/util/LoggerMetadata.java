package com.github.misterchangray.service.jsexcutor.util;

import java.util.ArrayList;
import java.util.List;

public class LoggerMetadata {
    private List<Logger> logger = new ArrayList<>(1000);
    private long dumpCount;



    public List<Logger> getLogger() {
        return logger;
    }

    public void setLogger(List<Logger> logger) {
        this.logger = logger;
    }

    public long getDumpCount() {
        return dumpCount;
    }

    public void setDumpCount(long dumpCount) {
        this.dumpCount = dumpCount;
    }
}
