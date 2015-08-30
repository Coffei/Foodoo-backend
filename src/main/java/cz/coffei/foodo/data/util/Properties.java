package cz.coffei.foodo.data.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Created by jtrantin on 29.8.15.
 */
public class Properties {
    private Map<String, String> properties;
    private static Properties instance;
    private Logger log = Logger.getLogger(this.getClass().getName());

    private Properties() {
        this.properties = new HashMap<>();

        // load the properties file
        String configFilename = "config.properties";
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(configFilename);
        java.util.Properties props = new java.util.Properties();
        try {
            props.load(inputStream);
        } catch (IOException e) {
            log.warning("Unable to load properties file!");
        }

        Set<String> keys = props.stringPropertyNames();
        for(String key : keys) {
            properties.put(key, props.getProperty(key));
        }
    }

    public String get(String key) {
        return this.properties.get(key);
    }

    public String get(String key, String defaultValue) {
        return this.properties.getOrDefault(key, defaultValue);
    }

    public Map<String, String> getProperties() {
        return Collections.unmodifiableMap(this.properties);
    }



    public static Properties getInstance() {
        if(instance == null) instance = new Properties();

        return instance;
    }
}
