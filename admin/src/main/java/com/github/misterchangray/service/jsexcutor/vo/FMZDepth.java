package com.github.misterchangray.service.jsexcutor.vo;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

public class FMZDepth {
    public JsonNode Info; //	交易所返回的原始结构
    public List<Depth> Asks;
    public List<Depth> Bids;


}
