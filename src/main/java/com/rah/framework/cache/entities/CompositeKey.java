package com.rah.framework.cache.entities;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class CompositeKey implements Serializable {
    String              key;
    Map<String, Object> map;

    public CompositeKey() {
        map = new HashMap<>();
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getParam(String param) {
        return map.get(param);
    }

    public void setParam(String param, Object obj) {
        map.put(param, obj);
    }

    @Override
    public String toString() {
        return "CompositeKey{" +
            "key='" + key + '\'' +
            '}';
    }
}
