package com.myasishchev.wheelytest.util;

import android.os.Bundle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by MyasishchevA on 25.09.2014.
 */
public class BundleUtils {

    public static <T extends Serializable> List<T> getArguments(Bundle bundle, String tag, Class<T> clazz) {
        List<T> result = new ArrayList<T>();
        if (bundle != null) {
            T item;
            int i = 0;
            while ((item = getArgument(bundle, String.format("%s%s", tag, i), clazz)) != null) {
                result.add(item);
                i++;
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Serializable> T getArgument(Bundle bundle, String key, Class<T> clazz) {
        if (bundle != null)
            return (T) bundle.getSerializable(key);
        return null;
    }

    public static <T extends Serializable> Bundle putArgument(Bundle bundle, String key, T object) {
        bundle.putSerializable(key, object);
        return bundle;
    }

    public static <T extends Serializable> Bundle putArguments(Bundle bundle, String key, List<T> objects) {
        for (int i = 0; i < objects.size(); i++) {
            bundle.putSerializable(String.format("%s%s", key, i), objects.get(i));
        }
        return bundle;
    }
}
