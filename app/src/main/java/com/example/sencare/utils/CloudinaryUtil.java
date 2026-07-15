package com.example.sencare.utils;

import android.content.Context;
import com.cloudinary.android.MediaManager;
import com.example.sencare.BuildConfig;

import java.util.HashMap;
import java.util.Map;

public class CloudinaryUtil {
    private static boolean isInitialized = false;

    public static void init(Context context) {
        if (!isInitialized) {
            Map<String, Object> config = new HashMap<>();
            config.put("cloud_name", BuildConfig.CLOUDINARY_CLOUD_NAME);
            config.put("api_key", BuildConfig.CLOUDINARY_API_KEY);
            config.put("api_secret", BuildConfig.CLOUDINARY_API_SECRET);
            MediaManager.init(context, config);
            isInitialized = true;
        }
    }
}
