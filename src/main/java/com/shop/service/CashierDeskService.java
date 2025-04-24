package com.shop.service;

import com.shop.exception.InsufficientFundsException;
import com.shop.model.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class CashierDeskService {
    private ShopService shopService;

    public CashierDeskService(ShopService shopService) {
        this.shopService = shopService;
    }

    public BigDecimal getTotalBasketPrice(Basket basket) {
        return basket.getItems().entrySet().stream()
                .flatMap(entry -> entry.getValue().entrySet().stream()
                        .map(dateEntry -> {
                            Product product = entry.getKey();
                            LocalDate purchaseDate = dateEntry.getKey();
                            int quantity = dateEntry.getValue();

                            BigDecimal priceForDate = shopService.getProductPrice(product, purchaseDate);
                            return priceForDate.multiply(BigDecimal.valueOf(quantity));
                        }))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getTotalCustomerSpending(List<Customer> customers) {
        BigDecimal income = BigDecimal.ZERO;
        for(Customer customer : customers) {
            income = income.add(getTotalBasketPrice(customer.getBasket()));
        }

        return income;
    }

    public void processPurchase(Basket basket, BigDecimal budget, Cashier cashier) {
        BigDecimal totalCost = getTotalBasketPrice(basket);

        if (totalCost.compareTo(budget) > 0) {
            basket.getItems().forEach((product, dateMap) ->
                    dateMap.forEach((date, quantity) -> {
                        product.setExpirationDate(date);
                        shopService.addProduct(product, quantity);
                    })
            );

            throw new InsufficientFundsException("Customer budget of " + budget + " is insufficient for total cost: " + totalCost);
        }

        createReceipt(cashier, basket);
    }

    public void processPurchaseForAllCustomers(List<Customer> customers, Cashier cashier) {
        for (Customer customer : customers) {
            processPurchase(customer.getBasket(), customer.getBudget(), cashier);
        }
    }

    public Receipt createReceipt(Cashier cashier, Basket basket) {
        LocalDate date = LocalDate.now();

        Map<Product, Integer> consolidatedItems = new HashMap<>();

        for (Map.Entry<Product, TreeMap<LocalDate, Integer>> entry : basket.getItems().entrySet()) {
            Product product = entry.getKey();
            int totalQuantity = entry.getValue().values().stream().mapToInt(Integer::intValue).sum();
            consolidatedItems.put(product, totalQuantity);
        }

        BigDecimal totalCost = getTotalBasketPrice(basket);

        Receipt receipt = new Receipt(cashier.getName(), date, consolidatedItems, totalCost);

        cashier.printReceipt(receipt);
        cashier.saveReceiptAsText(receipt);

        return receipt;
    }


}
