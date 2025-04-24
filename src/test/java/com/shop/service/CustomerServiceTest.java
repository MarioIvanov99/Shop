package com.shop.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.shop.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.TreeMap;

public class CustomerServiceTest {

    @Mock
    private ShopService shopService;

    @InjectMocks
    private CustomerService basketService;

    private Customer customer;
    private Product product;
    private Basket basket;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        customer = new Customer(new BigDecimal("100.00"));

        product = new FoodProduct.FoodProductBuilder().productId("FP01").name("Apple").price(new BigDecimal("1.5")).canExpire(true).expirationDate(LocalDate.of(2023, 10, 1)).build();
    }

    @Test
    public void testAddProductWhenProductDoesNotExist() {
        int amount = 5;

        basketService.addProductToBasket(customer, product, amount);

        Map<Product, TreeMap<LocalDate, Integer>> items = customer.getBasket().getItems();
        assertTrue(items.containsKey(product), "Basket should contain the product");

        TreeMap<LocalDate, Integer> inventory = items.get(product);
        assertTrue(inventory.containsKey(product.getExpirationDate()), "Basket should contain the expiration date");

        int actualAmount = inventory.get(product.getExpirationDate());
        assertEquals(amount, actualAmount, "Basket should have the correct amount");

        verify(shopService, times(1)).removeProduct(product, amount);
    }

    @Test
    public void testAddProductWhenProductAlreadyExistsAndDateMatches() {

        int initialAmount = 3;
        int additionalAmount = 2;
        int expectedAmount = initialAmount + additionalAmount;

        TreeMap<LocalDate, Integer> inventory = new TreeMap<>();
        inventory.put(product.getExpirationDate(), initialAmount);
        customer.getBasket().getItems().put(product, inventory);

        basketService.addProductToBasket(customer, product, additionalAmount);

        Map<Product, TreeMap<LocalDate, Integer>> items = customer.getBasket().getItems();
        assertTrue(items.containsKey(product), "Basket should contain the product");

        TreeMap<LocalDate, Integer> updatedInventory = items.get(product);
        assertTrue(updatedInventory.containsKey(product.getExpirationDate()), "Basket should contain the expiration date");

        int actualAmount = updatedInventory.get(product.getExpirationDate());
        assertEquals(expectedAmount, actualAmount, "Basket should have the correct updated amount");

        verify(shopService, times(1)).removeProduct(product, additionalAmount);
    }
}