package com.shop.service;

import com.shop.model.Customer;
import com.shop.model.Product;

import java.time.LocalDate;
import java.util.Map;
import java.util.TreeMap;

public class CustomerService {
    private final ShopService shopService;

    public CustomerService(ShopService shopService) {
        this.shopService = shopService;
    }

    public void addProductToBasket(Customer customer, Product product, Integer amount) {
        shopService.removeProduct(product, amount);

        Map<Product, TreeMap<LocalDate, Integer>> basket = customer.getBasket().getItems();

        if (basket.containsKey(product)) {
            TreeMap<LocalDate, Integer> inventory = basket.get(product);
            if (inventory.containsKey(product.getExpirationDate())) {
                int currentAmount = inventory.get(product.getExpirationDate());
                inventory.put(product.getExpirationDate(), currentAmount + amount);
            } else {
                inventory.put(product.getExpirationDate(), amount);
            }
        } else {
            TreeMap<LocalDate, Integer> inventory = new TreeMap<>();
            inventory.put(product.getExpirationDate(), amount);
            basket.put(product, inventory);
        }
    }
}
