package org.rboug.application.elibrary.view.shopping;

import org.rboug.application.elibrary.model.Item;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;


public class ShoppingCartItem {

    // ======================================
    // =             Attributes             =
    // ======================================

    @NotNull
    private Item item;
    @NotNull
    @Min(1)
    private Integer quantity;

    // ======================================
    // =            Constructors            =
    // ======================================

    public ShoppingCartItem(Item item, Integer quantity) {
        this.item = item;
        this.quantity = quantity;
    }

    // ======================================
    // =          Business methods          =
    // ======================================

    public Float getSubTotal() {
        return item.getUnitCost() * quantity;
    }

    // ======================================
    // =         Getters & setters          =
    // ======================================

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    // ======================================
    // =   Methods hash, equals, toString   =
    // ======================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ShoppingCartItem cartItem = (ShoppingCartItem) o;

        if (!item.equals(cartItem.item)) return false;
        if (!quantity.equals(cartItem.quantity)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = item.hashCode();
        result = 31 * result + quantity.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ShoppingCartItem{" +
                "item=" + item +
                ", quantity=" + quantity +
                '}';
    }
}