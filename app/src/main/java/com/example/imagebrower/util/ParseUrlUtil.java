package com.example.imagebrower.util;

/**
 * 取图片名
 */
public final class ParseUrlUtil {
    private ParseUrlUtil() {}

    public static String getImageNameFromUrl(String url) {
        String[] results = url.split("/");
        return results[results.length - 1];
    }
}
