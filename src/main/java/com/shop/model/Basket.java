package com.shop.model;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Basket {
    private final Map<Product, TreeMap<LocalDate, Integer>> items;

    public Basket() {
        this.items = new HashMap<>();
    }

    public Map<Product, TreeMap<LocalDate, Integer>> getItems() {
        return items;
    }

    public void setItems(Map<Product, TreeMap<LocalDate, Integer>> items) {
        this.items.putAll(items);
    }
}
