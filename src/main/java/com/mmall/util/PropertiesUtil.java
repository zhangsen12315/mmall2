package com.mmall.util;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

public class PropertiesUtil {

    private static Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);
    private static Properties props;

    static {
        String fileName = "mmall.properties";
        props = new Properties();
        try {
            props.load(new InputStreamReader(PropertiesUtil.class.getClassLoader().getResourceAsStream(fileName),"UTF-8"));
        } catch (IOException e) {
            logger.error("读取配置文件异常",e);
        }
   }

   public static String getProperty(String key){
       String property = props.getProperty(key);
       if (StringUtils.isBlank(property)){
           return null;
       }
       return property.trim();
   }

    public static String getProperty(String key,String defaultValue){
        String property = props.getProperty(key);
        if (StringUtils.isBlank(property)){
            return defaultValue;
        }
        return property.trim();
    }

}
