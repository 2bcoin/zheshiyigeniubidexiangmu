/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.misterchangray.libs.bitmex.client;

import com.github.misterchangray.service.common.data.SumZeroException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author RobTerpilowski
 */
public class BitmexApiKey {

    public static String PROPKEY_API_KEY_NAME = "api.key.name";
    public static String PROPKEY_API_KEY = "api.key";
    public static String PROPKEY_USE_PRODUCTION = "use.production";

    protected String apiKeyName = "";
    protected String apiKey = "";
    protected boolean useProduction = false;

    public BitmexApiKey(String apiKeyName, String apiKey, boolean useProduction) {
        this.apiKeyName = apiKeyName;
        this.apiKey = apiKey;
        this.useProduction = useProduction;
    }

    public String getApiKeyName() {
        return apiKeyName;
    }

    public void setApiKeyName(String apiKeyName) {
        this.apiKeyName = apiKeyName;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public boolean isUseProduction() {
        return useProduction;
    }

    public void setUseProduction(boolean useProduction) {
        this.useProduction = useProduction;
    }

    
    public static BitmexApiKey readApiKey(String propFile) {
        Properties props = new Properties();
        try {
            props.load(new FileInputStream(propFile));
            String name  = props.getProperty(PROPKEY_API_KEY_NAME);
            String value = props.getProperty(PROPKEY_API_KEY);
            boolean useProd = Boolean.parseBoolean(props.getProperty(PROPKEY_USE_PRODUCTION, "false"));

            return new BitmexApiKey(name, value, useProd);
        } catch (IOException ex) {
            throw new SumZeroException(ex.getMessage(), ex);
        }

    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.apiKeyName);
        hash = 59 * hash + Objects.hashCode(this.apiKey);
        hash = 59 * hash + (this.useProduction ? 1 : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BitmexApiKey other = (BitmexApiKey) obj;
        if (this.useProduction != other.useProduction) {
            return false;
        }
        if (!Objects.equals(this.apiKeyName, other.apiKeyName)) {
            return false;
        }
        if (!Objects.equals(this.apiKey, other.apiKey)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "BitmexApiKey{" + "apiKeyName=" + apiKeyName + ", apiKey=" + apiKey + ", useProduction=" + useProduction + '}';
    }

    
    
}
