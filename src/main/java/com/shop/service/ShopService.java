package com.shop.service;

import com.shop.exception.OutOfStockException;
import com.shop.exception.ProductNotFoundException;
import com.shop.model.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Map;
import java.util.TreeMap;

public class ShopService {

    private Shop shop;
    private BigDecimal deliveryCost;

    public ShopService(Shop shop) {
        this.shop = shop;
        this.deliveryCost = BigDecimal.ZERO;
    }
    public void clearExpiredFoods() {
        LocalDate today = DateWrapper.currentDate();

        shop.getInventory().entrySet().removeIf(entry -> {
            entry.getValue().keySet().removeIf(date -> date.isBefore(today));
            return entry.getValue().isEmpty();
        });
    }

    public TreeMap<LocalDate, Integer> getProductData(Product product) {
        return shop.getInventory().get(product);
    }

    public void addProduct(Product product, Integer amount) {
        Map<Product, TreeMap<LocalDate, Integer>> shopCatalogue = shop.getInventory();

        if (shopCatalogue.containsKey(product)) {
            TreeMap<LocalDate, Integer> inventory = shopCatalogue.get(product);
            if (inventory.containsKey(product.getExpirationDate())) {
                int currentAmount = inventory.get(product.getExpirationDate());
                inventory.put(product.getExpirationDate(), currentAmount + amount);
            } else {
                inventory.put(product.getExpirationDate(), amount);
            }
        } else {
            TreeMap<LocalDate, Integer> inventory = new TreeMap<>();
            inventory.put(product.getExpirationDate(), amount);
            shopCatalogue.put(product, inventory);
        }
    }

    public void removeProduct(Product product, Integer amount) {
        Map<Product, TreeMap<LocalDate, Integer>> shopCatalogue = shop.getInventory();

        if (shopCatalogue.containsKey(product)) {
            TreeMap<LocalDate, Integer> inventory = shopCatalogue.get(product);
            if (inventory.containsKey(product.getExpirationDate())) {
                int currentAmount = inventory.get(product.getExpirationDate());
                if(isThereEnoughStock(currentAmount, amount)) {
                    inventory.put(product.getExpirationDate(), currentAmount - amount);
                }
                else {
                    throw new OutOfStockException("Product " + product.getProductId() + " dated " + product.getExpirationDate() + " is out of stock");
                }
            }
            else {
                throw new ProductNotFoundException("Product dated " + product.getExpirationDate() + " not found");
            }
        }
        else {
            throw new ProductNotFoundException("Product not found");
        }
    }

    private boolean isThereEnoughStock(Integer current , Integer requested) {
        return current >= requested;
    }

    public BigDecimal CalculateCashierSalaries() {
        BigDecimal salaries = BigDecimal.ZERO;

        for(CashierDesk cashier : shop.getCashierDesks()) {
            salaries = salaries.add(cashier.getCashier().getSalary());
        }

        return salaries;
    }

    public BigDecimal getDeliveryCost() {
        return deliveryCost;
    }

    public void getNewDelivery(Product product, Integer amount, BigDecimal deliveryCost) {
        this.deliveryCost = this.deliveryCost.add(deliveryCost);
        addProduct(product, amount);
    }

    public BigDecimal getTotalCost() {
        return getDeliveryCost().add(CalculateCashierSalaries());
    }

    public BigDecimal getProductPrice(Product product, LocalDate date) {
        BigDecimal finalPrice = product.getPrice();
        finalPrice = finalPrice.multiply(shop.getMarkupPercentage(product.getCategory()).add(new BigDecimal("1.0")));
        if(isCloseToExpire(date)) {
            finalPrice = finalPrice.multiply(new BigDecimal("1.0").subtract(shop.getDiscountPercentage()));
        }
        return finalPrice.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getProfit(BigDecimal income) {
        return income.subtract(getTotalCost()).setScale(2, RoundingMode.HALF_UP);
    }

    public boolean isCloseToExpire(LocalDate date){
        return date.isBefore(DateWrapper.currentDate().plusDays(shop.getDaysBeforeExpityDiscount()));
    }
}
