package com.inmobi.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.inmobi.Exception.InvalidSignatureException;
import com.inmobi.Exception.ResourceNotFoundException;
import com.inmobi.configs.PaymentConfig;
import com.inmobi.dtos.res.PaymentRes;
import com.inmobi.dtos.res.ResponseData;
import com.inmobi.dtos.res.ResponseError;
import com.inmobi.services.PaymentService;

@RestController
public class VnPayController {
    private final PaymentService paymentService;

    public VnPayController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/vnpay/create-payment")
    public ResponseData<?> createPayment(
            @NotNull @RequestParam Long amount,
            @NotNull @RequestParam String language,
            HttpServletRequest httpReq,
            @AuthenticationPrincipal Jwt jwt) {

        try {
            Long userId = Long.parseLong(jwt.getClaims().get("userId").toString());
            PaymentRes paymentRes = paymentService.createPayment(amount, language, httpReq, userId);

            return new ResponseData<>(
                    HttpStatus.ACCEPTED.value(),
                    "Payment URL created",
                    paymentRes);
        } catch (ResourceNotFoundException e) {
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        } catch (Exception e) {
            return new ResponseError(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Create payment URL failed: " + e.getMessage());
        }

    }

    @GetMapping("/vnpay_jsp/vnpay_return.jsp")
    public ResponseData<?> handleReturn(HttpServletRequest request) {
        try {

            return new ResponseData<>(
                    HttpStatus.OK.value(),
                    "VNPAY RETURN RECEIVED",
                    paymentService.handleCallbackPayment(request));
        } catch (InvalidSignatureException e) {
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        } catch (ResourceNotFoundException e) {
            return new ResponseError(HttpStatus.NOT_FOUND.value(), e.getMessage());
        } catch (Exception e) {
            return new ResponseError(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Handle VNPAY RETURN failed: " + e.getMessage());
        }

    }
}