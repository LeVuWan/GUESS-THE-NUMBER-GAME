package com.inmobi.dtos.res;

public class CallbackPaymentRes {
    private String orderId;
    private Long amount;
    private boolean success;
    private String message;

    public CallbackPaymentRes() {
    }

    public CallbackPaymentRes(String orderId, Long amount,
            boolean success, String message) {
        this.orderId = orderId;
        this.amount = amount;
        this.success = success;
        this.message = message;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
