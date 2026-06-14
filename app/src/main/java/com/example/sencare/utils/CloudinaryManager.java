package com.example.sencare.utils;

import android.content.Context;

import com.cloudinary.android.MediaManager;

import java.util.HashMap;
import java.util.Map;

public class CloudinaryManager {

    private static boolean isInitialized = false;

    public static void init(Context context) {
        if (isInitialized) return;

        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "dzgdqf5vs");

        MediaManager.init(context, config);
        isInitialized = true;
    }
}