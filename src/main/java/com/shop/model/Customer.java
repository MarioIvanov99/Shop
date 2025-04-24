package com.shop.model;

import java.math.BigDecimal;

public class Customer {
    private final BigDecimal budget;
    private final Basket basket;

    public Customer(BigDecimal budget) {
        this.budget = budget;
        this.basket = new Basket();
    }

    public BigDecimal getBudget() {
        return budget;
    }

    public Basket getBasket() {
        return basket;
    }

    public void setBasket(Basket basket) {
        this.basket.setItems(basket.getItems());
    }
}
