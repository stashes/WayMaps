package com.waymaps.util;

import android.os.Bundle;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by Admin on 03.02.2018.
 */

public class JSONUtil {
    private static final ObjectMapper ourInstance = new ObjectMapper();

    public static ObjectMapper getObjectMapper() {
        return ourInstance;
    }

}
