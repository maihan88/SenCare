package com.example.sencare.utils;

import android.content.Context;
import com.cloudinary.android.MediaManager;
import java.util.HashMap;
import java.util.Map;

public class CloudinaryUtil {
    private static boolean isInitialized = false;

    public static void init(Context context) {
        if (!isInitialized) {
            Map<String, Object> config = new HashMap<>();
            config.put("cloud_name", "dzgdqf5vs");
            config.put("api_key", "916138796395978");
            config.put("api_secret", "v9ygh40SMrWMXXzJqUm1bEzqb6Q");
            MediaManager.init(context, config);
            isInitialized = true;
        }
    }
}
