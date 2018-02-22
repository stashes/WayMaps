package com.waymaps.util;

import android.os.Bundle;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;

/**
 * Created by Admin on 11.02.2018.
 */

public class ApplicationUtil {

    public static Bundle setValueToBundle(Bundle bundle, String key, Object value) throws JsonProcessingException {
        bundle.putString(key, JSONUtil.getObjectMapper().writeValueAsString(value));
        return bundle;
    }

    public static  <T> T getObjectFromBundle(Bundle bundle, String key, Class<T> tClass) throws IOException {
        return JSONUtil.getObjectMapper().readValue(bundle.getString(key), tClass);
    }
}
