package com.shop.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

public class Receipt implements Serializable {
    private static final long serialVersionUID = 67686234738156688L;
    private String id;
    private String cashierName;
    private LocalDate date;
    private Map<Product, Integer> items;
    private BigDecimal total;

    public Receipt() {}

    public Receipt(String cashierName, LocalDate date, Map<Product, Integer> items, BigDecimal total) {
        this.id = UUID.randomUUID().toString();
        this.cashierName = cashierName;
        this.date = date;
        this.items = items;
        this.total = total;
    }

    public String getId() {
        return id;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public Map<Product, Integer> getItems() {
        return items;
    }

    public LocalDate getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "Receipt{" +
                "id='" + id + '\'' +
                ", cashierName='" + cashierName + '\'' +
                ", date=" + date +
                ", items=" + items +
                ", total=" + total +
                '}';
    }
}
