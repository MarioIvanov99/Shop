package com.shop.model;

import java.util.List;

public class CashierDesk {
    private Cashier cashier;
    private List<Customer> customers;
    public CashierDesk(Cashier cashier, List<Customer> customers) {
        this.cashier = cashier;
        this.customers = customers;
    }

    public Cashier getCashier() {
        return cashier;
    }

    public List<Customer> getCustomers() {
        return customers;
    }
}
