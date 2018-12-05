package com.github.misterchangray.libs.binance.api.client.domain.event;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.misterchangray.libs.binance.api.client.domain.account.AssetBalance;

import java.io.IOException;

/**
 * Custom deserializer for an AssetBalance, since the streaming API returns an object in the format {"a":"symbol","f":"free","l":"locked"},
 * which is different than the format used in the REST API.
 */
public class AssetBalanceDeserializer extends JsonDeserializer<AssetBalance> {

  @Override
  public AssetBalance deserialize(JsonParser jp, DeserializationContext ctx) throws IOException {
    ObjectCodec oc = jp.getCodec();
    JsonNode node = oc.readTree(jp);
    final String asset = node.get("a").asText();
    final String free = node.get("f").asText();
    final String locked = node.get("l").asText();

    AssetBalance assetBalance = new AssetBalance();
    assetBalance.setAsset(asset);
    assetBalance.setFree(free);
    assetBalance.setLocked(locked);
    return assetBalance;
  }
}