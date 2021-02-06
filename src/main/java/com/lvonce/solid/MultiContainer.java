package com.lvonce.solid;


import java.util.LinkedHashMap;
import java.util.Map;

public class MultiContainer<T> {

    private final Map<String, T> elements = new LinkedHashMap<>();

    public void set(String key, T element) {
        elements.put(key, element);
    }

    public T get(String key) {
        return elements.get(key);
    }

    public void addAll(MultiContainer<T> elements) {
        this.elements.putAll(elements.getAll());
    }
    public Map<String, T> getAll() {
        return elements;
    }


}
