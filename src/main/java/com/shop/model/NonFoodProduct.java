package com.shop.model;

public class NonFoodProduct extends Product {
    private Integer warrantyMonths;

    private NonFoodProduct(NonFoodProductBuilder builder) {
        super(builder);
        this.warrantyMonths = builder.warrantyMonths;
    }

    public static class NonFoodProductBuilder extends ProductBuilder<NonFoodProductBuilder> {
        private Integer warrantyMonths;

        public NonFoodProductBuilder warrantyMonths(Integer warrantyMonths) {
            this.warrantyMonths = warrantyMonths;
            return this;
        }

        @Override
        protected NonFoodProductBuilder self() {
            return this;
        }

        @Override
        public NonFoodProduct build() {
            category(Category.NON_FOOD);
            return new NonFoodProduct(this);
        }
    }
}