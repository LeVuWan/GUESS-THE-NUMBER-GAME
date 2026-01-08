package com.inmobi.dtos.req;

import java.io.Serializable;

public class PaymentDto implements Serializable {
    private Long amount;
    private String language;

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
