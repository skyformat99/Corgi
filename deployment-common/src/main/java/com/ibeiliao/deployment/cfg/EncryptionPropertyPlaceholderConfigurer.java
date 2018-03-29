package com.ibeiliao.deployment.cfg;


import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * properties文件加密、解密类
 * @author linyi
 */
public class EncryptionPropertyPlaceholderConfigurer extends
        PropertyPlaceholderConfigurer {

    private PropertiesEncoder encoder;
    
    private static Map<String, String> propMap = new HashMap<>();

    public PropertiesEncoder getEncoder() {
        return encoder;
    }

    public void setEncoder(PropertiesEncoder encoder) {
        this.encoder = encoder;
    }

    @Override
    protected void convertProperties(Properties props) {
        Enumeration<?> propertyNames = props.propertyNames();
        while (propertyNames.hasMoreElements()) {
            String propertyName = (String) propertyNames.nextElement();
            String lowerName = propertyName.toLowerCase();
            if (lowerName.indexOf("username") >= 0
                    || lowerName.indexOf("password") >= 0
                    || lowerName.indexOf("encryption") >= 0) {
                String propertyValue = props.getProperty(propertyName);
                if (propertyValue == null) {
                    propertyValue = "";
                }
                String decodedValue = encoder.decode(propertyValue);
                props.setProperty(propertyName, decodedValue);
            }
            propMap.put(propertyName, props.getProperty(propertyName));
        }
        super.convertProperties(props);
    }

	public static Map<String, String> getConfig() {
		return propMap;
	}

	public static String getConfig(String key) {
		return propMap.get(key);
	}

}
