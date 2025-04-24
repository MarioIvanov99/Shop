package com.shop.service;

import com.shop.exception.OutOfStockException;
import com.shop.exception.ProductNotFoundException;
import com.shop.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ShopServiceTest {

    private ShopService shopService;

    @BeforeEach
    public void setUp() throws Exception {
        Map<Category, BigDecimal> markUpPercentage = new EnumMap<>(Category.class);
        markUpPercentage.put(Category.FOOD, new BigDecimal("0.1"));
        markUpPercentage.put(Category.NON_FOOD, new BigDecimal("0.2"));
        BigDecimal discountPercentage = new BigDecimal("0.3");

        List<CashierDesk> cashierDesks = new ArrayList<>();

        Cashier cashier1 = new Cashier("Cashier 1", new BigDecimal(100));
        Cashier cashier2 = new Cashier("Cashier 2", new BigDecimal(111));
        cashierDesks.add(new CashierDesk(cashier1, new ArrayList<>()));
        cashierDesks.add(new CashierDesk(cashier2, new ArrayList<>()));

        Shop shop = new Shop("Test", markUpPercentage, 3, discountPercentage, cashierDesks);

        Map<Product, TreeMap<LocalDate, Integer>> inventory = new HashMap<>();

        Product product1 = new FoodProduct.FoodProductBuilder().productId("FP01").name("Apple").price(new BigDecimal("1.5")).canExpire(true).build();
        Product product2 = new FoodProduct.FoodProductBuilder().productId("FP02").name("Banana").price(new BigDecimal("2.0")).canExpire(true).build();

        TreeMap<LocalDate, Integer> product1Inventory = new TreeMap<>();
        product1Inventory.put(LocalDate.of(2023, 10, 1), 50);
        product1Inventory.put(LocalDate.of(2023, 10, 5), 30);

        TreeMap<LocalDate, Integer> product2Inventory = new TreeMap<>();
        product2Inventory.put(LocalDate.of(2023, 10, 5), 75);
        product2Inventory.put(LocalDate.of(2023, 10, 6), 20);
        product2Inventory.put(LocalDate.of(2023, 10, 10), 100);

        inventory.put(product1, product1Inventory);
        inventory.put(product2, product2Inventory);

        shop.setInventory(inventory);

        shopService = new ShopService(shop);
    }

    @Test
    public void testAddProductWhenProductAlreadyExistsAndDateMatches() {
        Product product = new FoodProduct.FoodProductBuilder().productId("FP01").expirationDate(LocalDate.of(2023, 10, 1)).build();
        shopService.addProduct(product, 50);

        int quantity = shopService.getProductData(product).get(LocalDate.of(2023, 10, 1));

        assertEquals(100, quantity, "Addition should increase the quantity of the product for the given date");
        assertEquals(2, shopService.getProductData(product).size(), "Addition should not increase number of dates for given product");
    }

    @Test
    public void testAddProductWhenProductAlreadyExistsAndDateDoesNotMatch() {
        Product product = new FoodProduct.FoodProductBuilder().productId("FP01").expirationDate(LocalDate.of(2023, 10, 2)).build();
        shopService.addProduct(product, 50);

        int quantity = shopService.getProductData(product).get(LocalDate.of(2023, 10, 1));

        assertEquals(50, quantity);
        assertEquals(3, shopService.getProductData(product).size(), "Addition should increase number of dates for given product");
    }

    @Test
    public void testAddProductWhenProductDoesNotExist() {
        Product product = new FoodProduct.FoodProductBuilder().productId("FP03").expirationDate(LocalDate.of(2023, 10, 1)).build();
        shopService.addProduct(product, 50);

        assertEquals(1, shopService.getProductData(product).size(), "Addition should create new product and add date and quantity to it");
    }

    @Test
    public void testGetProductDataById() {
        Product product = new FoodProduct.FoodProductBuilder().productId("FP01").build();
        TreeMap<LocalDate, Integer> productData = shopService.getProductData(product);

        assertNotNull(productData, "Product data should not be null");
        assertEquals(2, productData.size(), "Product should have data for 2 dates");
    }

    @Test
    void testClearExpiredFoodsRemovesDates() {
        try (MockedStatic<DateWrapper> mockedStatic = Mockito.mockStatic(DateWrapper.class)) {
            mockedStatic.when(DateWrapper::currentDate).thenReturn(LocalDate.of(2023, 10, 4));

            shopService.clearExpiredFoods();
            assertEquals(1, shopService.getProductData(new FoodProduct.FoodProductBuilder().productId("FP01").build()).size());
            assertEquals(3, shopService.getProductData(new FoodProduct.FoodProductBuilder().productId("FP02").build()).size());
        }
    }

    @Test
    void testClearExpiredFoodsRemovesProductIfAllDatesExpired() {
        try (MockedStatic<DateWrapper> mockedStatic = Mockito.mockStatic(DateWrapper.class)) {
            mockedStatic.when(DateWrapper::currentDate).thenReturn(LocalDate.of(2023, 10, 6));

            shopService.clearExpiredFoods();
            assertNull(shopService.getProductData(new FoodProduct.FoodProductBuilder().productId("FP01").build()));
        }
    }

    @Test
    void testRemoveProductWhenIdAndDateMatch() {
        Product product = new FoodProduct.FoodProductBuilder().productId("FP01").expirationDate(LocalDate.of(2023, 10, 1)).build();
        shopService.removeProduct(product, 40);

        assertEquals(10, shopService.getProductData(product).get(LocalDate.of(2023, 10, 1)));
    }

    @Test
    void testRemoveProductWhenIdAndDateDoNotMatch() {
        Product product = new FoodProduct.FoodProductBuilder().productId("FP01").expirationDate(LocalDate.of(2023, 10, 2)).build();

        assertThrows(ProductNotFoundException.class, () -> shopService.removeProduct(product, 40));
    }

    @Test
    void testRemoveProductWhenIdDoesNotExist() {
        Product product = new FoodProduct.FoodProductBuilder().productId("FP03").build();

        assertThrows(ProductNotFoundException.class, () -> shopService.removeProduct(product, 40));
    }

    @Test
    void testRemoveProductWhenProductIsOutOfStock() {
        Product product = new FoodProduct.FoodProductBuilder().productId("FP01").expirationDate(LocalDate.of(2023, 10, 1)).build();

        assertThrows(OutOfStockException.class, () -> shopService.removeProduct(product, 100));
    }

    @Test
    void testCalculateCashierSalaries() {
        assertEquals(new BigDecimal("211"), shopService.CalculateCashierSalaries());
    }

    @Test
    void testNewDeliveryIncreaseDeliveryCost() {
        Product product = new FoodProduct.FoodProductBuilder().productId("FP01").expirationDate(LocalDate.of(2023, 10, 1)).build();
        BigDecimal deliveryCost = new BigDecimal("52.00");
        shopService.getNewDelivery(product, 50, deliveryCost);
        assertEquals(new BigDecimal("52.00"), shopService.getDeliveryCost());
    }

    @Test
    void testProductPriceReturnsCorrectAmount() {
        try (MockedStatic<DateWrapper> mockedStatic = Mockito.mockStatic(DateWrapper.class)) {
            mockedStatic.when(DateWrapper::currentDate).thenReturn(LocalDate.of(2023, 10, 6));

            Product product1 = new FoodProduct.FoodProductBuilder().productId("FP01").name("Apple").price(new BigDecimal("2.0")).canExpire(true).build();
            Product product2 = new FoodProduct.FoodProductBuilder().productId("FP02").name("Banana").price(new BigDecimal("2.0")).canExpire(true).build();

            BigDecimal actual1 = shopService.getProductPrice(product1, LocalDate.of(2023, 10, 10));
            assertEquals(new BigDecimal("2.20"), actual1);

            BigDecimal actual2 = shopService.getProductPrice(product2, LocalDate.of(2023, 10, 8));
            assertEquals(new BigDecimal("1.54"), actual2);
        }
    }
}