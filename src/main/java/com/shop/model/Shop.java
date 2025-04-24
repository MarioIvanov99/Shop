package com.shop.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

public class Shop {
    private String name;
    private final Map<Category, BigDecimal> markupPercentage;
    private int daysBeforeExpityDiscount;
    private BigDecimal discountPercentage;
    private final Map<Product, TreeMap<LocalDate, Integer>> inventory;
    private final List<CashierDesk> cashierDesks;

    public Shop(String name, Map<Category, BigDecimal> markupPercentage, int daysBeforeExpityDiscount, BigDecimal discountPercentage, List<CashierDesk> cashierDesks) {
        this.name = name;
        this.markupPercentage = markupPercentage;
        this.daysBeforeExpityDiscount = daysBeforeExpityDiscount;
        this.discountPercentage = discountPercentage;
        this.inventory = new HashMap<>();
        this.cashierDesks = cashierDesks;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getMarkupPercentage(Category category) {
        return markupPercentage.get(category);
    }

    public int getDaysBeforeExpityDiscount() {
        return daysBeforeExpityDiscount;
    }

    public BigDecimal getDiscountPercentage() {
        return discountPercentage;
    }

    public Map<Product, TreeMap<LocalDate, Integer>> getInventory() {
        return inventory;
    }

    public List<CashierDesk> getCashierDesks() {
        return cashierDesks;
    }

    public void setInventory(Map<Product, TreeMap<LocalDate, Integer>> inventory) {
        this.inventory.putAll(inventory);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMarkupPercentage(Category category, BigDecimal markupPercentage) {
        this.markupPercentage.put(category, markupPercentage);
    }

    public void setDaysBeforeExpityDiscount(int daysBeforeExpityDiscount) {
        this.daysBeforeExpityDiscount = daysBeforeExpityDiscount;
    }

    public void setDiscountPercentage(BigDecimal discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

}
