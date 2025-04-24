package com.shop.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

public abstract class Product implements Serializable {
    private final String productId;
    private String name;
    private BigDecimal price;
    private boolean canExpire;
    private LocalDate expirationDate;
    private Category category;

    protected Product(ProductBuilder<?> builder) {
        this.productId = builder.productId;
        this.name = builder.name;
        this.price = builder.price;
        this.canExpire = builder.canExpire;
        this.expirationDate = builder.expirationDate;
        this.category = builder.category;
    }

    public String getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Category getCategory() {
        return category;
    }

    public void setExpirationDate(LocalDate expirationDate){
        this.expirationDate = expirationDate;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((productId == null) ? 0 : productId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Product)) {
            return false;
        }
        if (this.productId == null) {
            return false;
        }
        return this.productId.equals(((Product) obj).getProductId());
    }

    public abstract static class ProductBuilder<T extends ProductBuilder<T>> {

        private String productId;
        private String name;
        private BigDecimal price;
        private boolean canExpire;
        private LocalDate expirationDate;
        private Category category;

        public T productId(String productId) {
            this.productId = productId;
            return self();
        }

        public T name(String name) {
            this.name = name;
            return self();
        }

        public T price(BigDecimal price) {
            this.price = price;
            return self();
        }

        public T canExpire(boolean canExpire) {
            this.canExpire = canExpire;
            return self();
        }

        public T expirationDate(LocalDate expirationDate) {
            this.expirationDate = expirationDate;
            return self();
        }

        protected T category(Category category) {
            this.category = category;
            return self();
        }

        protected abstract T self();

        public abstract Product build();
    }
}
