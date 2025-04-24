package com.shop;

import com.shop.model.*;
import com.shop.service.CashierDeskService;
import com.shop.service.CustomerService;
import com.shop.service.ShopService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

public class App
{
    public static void main( String[] args )
    {
        Map<Category, BigDecimal> markUpPercentage = new EnumMap<>(Category.class);
        markUpPercentage.put(Category.FOOD, new BigDecimal("0.1"));
        markUpPercentage.put(Category.NON_FOOD, new BigDecimal("0.2"));

        Cashier cashier1 = new Cashier("John Doe", new BigDecimal("1000"));
        Cashier cashier2 = new Cashier("Jane Doe", new BigDecimal("1100"));

        Customer customer1 = new Customer(new BigDecimal("123.00"));
        Customer customer2 = new Customer(new BigDecimal("123.00"));
        Customer customer3 = new Customer(new BigDecimal("123.00"));

        List<Customer> desk1Customers = new ArrayList<>(Arrays.asList(customer1, customer2));
        List<Customer> desk2Customers = new ArrayList<>(Collections.singletonList(customer3));

        CashierDesk desk1 = new CashierDesk(cashier1, desk1Customers);
        CashierDesk desk2 = new CashierDesk(cashier2, desk2Customers);

        List<CashierDesk> cashierDesks = new ArrayList<>(Arrays.asList(desk1, desk2));

        Shop shop = new Shop("SuperMart", markUpPercentage, 3, new BigDecimal("0.3"), cashierDesks);
        ShopService shopService = new ShopService(shop);
        CashierDeskService cashierDeskService = new CashierDeskService(shopService);

        Product apple = new FoodProduct.FoodProductBuilder()
                .productId("FP01").name("Apple").price(new BigDecimal("1.5"))
                .expirationDate(LocalDate.now().plusDays(5)).build();

        Product banana = new FoodProduct.FoodProductBuilder()
                .productId("FP02").name("Banana").price(new BigDecimal("2.0"))
                .expirationDate(LocalDate.now().plusDays(2)).build();

        Product milk = new FoodProduct.FoodProductBuilder()
                .productId("FP03").name("Milk").price(new BigDecimal("3.0"))
                .expirationDate(LocalDate.now().plusDays(7)).build();


        shopService.getNewDelivery(apple, 100, new BigDecimal("20.00"));
        shopService.getNewDelivery(banana, 50, new BigDecimal("15.00"));
        shopService.getNewDelivery(milk, 30, new BigDecimal("25.00"));


        CustomerService customerService = new CustomerService(shopService);
        customerService.addProductToBasket(customer1, apple, 2);
        customerService.addProductToBasket(customer1, banana, 1);

        customerService.addProductToBasket(customer2, apple, 1);
        customerService.addProductToBasket(customer2, banana, 2);

        customerService.addProductToBasket(customer3, apple, 3);
        customerService.addProductToBasket(customer3, milk, 1);


        for (CashierDesk desk : cashierDesks) {
            cashierDeskService.processPurchaseForAllCustomers(desk.getCustomers(), desk.getCashier());
        }

        List<Customer> allCustomers = new ArrayList<>();
        for (CashierDesk desk : cashierDesks) {
            allCustomers.addAll(desk.getCustomers());
        }

        BigDecimal totalIncome = cashierDeskService.getTotalCustomerSpending(allCustomers);
        BigDecimal totalCost = shopService.getTotalCost();
        BigDecimal profit = shopService.getProfit(totalIncome);

        System.out.println("\n--- Business Summary ---");
        System.out.println("Total Income: " + totalIncome + "BGN");
        System.out.println("Total Costs : " + totalCost + "BGN");
        System.out.println("Total Profit: " + profit + "BGN");

        shopService.clearExpiredFoods();
    }
}
