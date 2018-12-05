/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.misterchangray.libs.bitmex.client;

import com.github.misterchangray.libs.bitmex.data.SumZeroException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author RobTerpilowski
 */
public class BitmexSignatureGenerator implements ISignatureGenerator {

    @Override
    public String generateSignature(String apiKey, String verb, String path, int expires, String data) {
        String keyString =  verb + path + expires + data;
        
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(apiKey.getBytes("UTF-8"), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            String hash = DatatypeConverter.printHexBinary(sha256_HMAC.doFinal(keyString.getBytes()));
            return hash;
        } catch (Exception e) {
            throw new SumZeroException(e);
        }
    }
    
    
    
    
    
    
}
