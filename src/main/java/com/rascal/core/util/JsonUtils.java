package com.rascal.core.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class JsonUtils {

    private static Logger logger = LoggerFactory.getLogger(JsonUtils.class);

    private final static ObjectMapper objectMapper = new ObjectMapper();

    public static String writeValueAsString(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> readValue(String content) {
        try {
            return objectMapper.readValue(content, Map.class);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return Maps.newHashMap();
    }

    public static void main(String[] args) {
        Map<String, Object> data = Maps.newHashMap();
        data.put("id", 123);
        data.put("time", DateUtils.formatTime(DateUtils.currentDate()));
        System.out.println(JsonUtils.writeValueAsString(data));
    }
}
