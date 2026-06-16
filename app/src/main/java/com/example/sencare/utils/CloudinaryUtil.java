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
            config.put("cloud_name", "YOUR_CLOUD_NAME"); // Thay bằng cloud_name của bạn
            config.put("api_key", "YOUR_API_KEY");       // Thay bằng api_key của bạn
            config.put("api_secret", "YOUR_API_SECRET"); // Thay bằng api_secret của bạn
            MediaManager.init(context, config);
            isInitialized = true;
        }
    }
}
