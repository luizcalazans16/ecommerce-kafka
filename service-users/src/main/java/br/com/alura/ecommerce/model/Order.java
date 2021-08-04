package br.com.alura.ecommerce.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class Order implements Serializable {

    private final String orderId;
    private final BigDecimal amount;
    private String email;

    public Order(String orderId, BigDecimal amount, String email) {

        this.orderId = orderId;
        this.amount = amount;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
