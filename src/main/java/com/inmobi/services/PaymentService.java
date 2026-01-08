package com.inmobi.services;

import com.inmobi.dtos.res.CallbackPaymentRes;
import com.inmobi.dtos.res.PaymentRes;

import jakarta.servlet.http.HttpServletRequest;

public interface PaymentService {
    PaymentRes createPayment(Long amount, String language, HttpServletRequest httpReq, Long userId);

    CallbackPaymentRes handleCallbackPayment(HttpServletRequest request);
}
