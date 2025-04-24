package com.shop.model;

public class FoodProduct extends Product {
    private boolean requiresRefrigeration;

    private FoodProduct(FoodProductBuilder builder) {
        super(builder);
        this.requiresRefrigeration = builder.requiresRefrigeration;
    }

    public static class FoodProductBuilder extends ProductBuilder<FoodProductBuilder> {
        private boolean requiresRefrigeration;

        public FoodProductBuilder requiresRefrigeration(boolean requiresRefrigeration) {
            this.requiresRefrigeration = requiresRefrigeration;
            return this;
        }

        @Override
        protected FoodProductBuilder self() {
            return this;
        }

        @Override
        public FoodProduct build() {
            category(Category.FOOD);
            return new FoodProduct(this);
        }
    }
}

