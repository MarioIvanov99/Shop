package com.shop.model;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;
public class Cashier {
    private final String id;
    private String name;
    private BigDecimal salary;

    public Cashier(String name, BigDecimal salary) {
        this.name = name;
        this.id = UUID.randomUUID().toString();
        this.salary = salary;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public void printReceipt(Receipt receipt) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                Files.newOutputStream(Paths.get("receipts/" + receipt.getId() + ".txt")))) {
            oos.writeObject(receipt);
        } catch (IOException e) {
            throw new RuntimeException("Error printing receipt: " + e.getMessage());
        }
    }

    public void saveReceiptAsText(Receipt receipt) {
        String fileName = "receipts/" + getName() + "_" + receipt.getId().substring(0, 4) + "_"
                + receipt.getDate().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                + ".txt";


        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(fileName))) {
            writer.write("Receipt\n");
            writer.write("========\n");
            writer.write("Cashier: " + getName() + "\n");
            writer.write("Date: " + receipt.getDate() + "\n");
            writer.write("Items:\n");

            receipt.getItems().forEach((product, quantity) -> {
                try {
                    writer.write("- " + product.getName() + " x " + quantity +
                            " @ " + product.getPrice() + " each\n");
                } catch (IOException e) {
                    throw new RuntimeException("Error writing receipt item: " + e.getMessage(), e);
                }
            });

            writer.write("\nTotal: $" + receipt.getTotal() + "\n");
            writer.write("========\n");

        } catch (IOException e) {
            throw new RuntimeException("Error saving receipt as text: " + e.getMessage(), e);
        }
    }

    public Receipt loadReceipt(String receiptId) {
        try (ObjectInputStream ois = new ObjectInputStream(
                Files.newInputStream(Paths.get("receipts/" + receiptId + ".txt")))) {
            return (Receipt) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Error loading receipt: " + e.getMessage());
        }
    }
}

