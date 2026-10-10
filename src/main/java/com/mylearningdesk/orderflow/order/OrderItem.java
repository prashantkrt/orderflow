package com.mylearningdesk.orderflow.order;

import java.math.BigDecimal;

public class OrderItem {
    private String productCode;
    private int quantity;
    private BigDecimal unitPrice;

    public OrderItem() {}
    public OrderItem(String productCode, int quantity, BigDecimal unitPrice) {
        this.productCode = productCode;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }
    public String getProductCode() { return productCode; }
    public int getQuantity() { return quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
}
