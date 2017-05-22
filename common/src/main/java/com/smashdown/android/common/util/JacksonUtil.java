package com.smashdown.android.common.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * Created by smashdown on 2016. 5. 7..
 */
public class JacksonUtil {
    public static final String SYSTEM_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";

    private static JacksonUtil instance;

    private ObjectMapper jackson;

    private JacksonUtil() {
        jackson = new ObjectMapper();
        jackson.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        jackson.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        //        DateFormat df = new SimpleDateFormat(SYSTEM_DATE_FORMAT);
        //        jackson.setDateFormat(df);
    }

    public static JacksonUtil getInstance() {
        if (instance == null)
            instance = new JacksonUtil();

        return instance;
    }

    public ObjectMapper getJackson() {
        return jackson;
    }
}
