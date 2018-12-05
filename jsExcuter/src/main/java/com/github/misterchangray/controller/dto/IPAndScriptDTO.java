package com.github.misterchangray.controller.dto;

import com.github.misterchangray.controller.common.vo.IPAndScript;

import java.util.ArrayList;
import java.util.List;

public class IPAndScriptDTO extends IPAndScript {
    private List<ConfigSignDTO> configSignList = new ArrayList<>();

    public void setConfigSignList(List<ConfigSignDTO> configSignList) {
        this.configSignList = configSignList;
    }

    public List<ConfigSignDTO> getConfigSignList() {
        return configSignList;
    }
}
