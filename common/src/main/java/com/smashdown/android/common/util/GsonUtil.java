package com.smashdown.android.common.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


/**
 * Created by smashdown on 2016. 5. 7..
 */
public class GsonUtil {
    public static final String SYSTEM_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";

    private static GsonUtil instance;

    private Gson gson;

    private GsonUtil() {
        gson = makeGson();
    }

    public static GsonUtil getInstance() {
        if (instance == null)
            instance = new GsonUtil();

        return instance;
    }

    private Gson makeGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();

        // Register custom parser for Date type
        gsonBuilder.setDateFormat(SYSTEM_DATE_FORMAT);
        //        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);

        return gsonBuilder.create();
    }

    public Gson getGson() {
        return gson;
    }
}
