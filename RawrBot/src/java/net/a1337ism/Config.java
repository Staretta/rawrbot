package net.a1337ism;

import java.io.FileInputStream;
import java.util.Properties;

public class Config {
    Properties configFile;

    public Config() {
        configFile = new java.util.Properties();
        try {
            FileInputStream file;
            String path = "./config.properties";
            file = new FileInputStream(path);
            configFile.load(file);
        } catch (Exception eta) {
            eta.printStackTrace();
        }
    }

    public String getProperty(String key) {
        String value = this.configFile.getProperty(key);
        return value;
    }
}