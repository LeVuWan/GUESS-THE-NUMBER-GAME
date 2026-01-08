package com.inmobi.services.impl;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

import com.inmobi.Exception.InvalidSignatureException;
import com.inmobi.Exception.ResourceNotFoundException;
import com.inmobi.configs.PaymentConfig;
import com.inmobi.dtos.res.CallbackPaymentRes;
import com.inmobi.dtos.res.PaymentRes;
import com.inmobi.models.Payment;
import com.inmobi.models.User;
import com.inmobi.repositories.PaymentRepository;
import com.inmobi.repositories.UserRepository;
import com.inmobi.services.PaymentService;
import com.inmobi.utils.PaymentStatus;

@Service
public class PaymentServiceImpl implements PaymentService {
        private final PaymentRepository paymentRepository;
        private final UserRepository userRepository;

        public PaymentServiceImpl(PaymentRepository paymentRepository, UserRepository userRepository) {
                this.paymentRepository = paymentRepository;
                this.userRepository = userRepository;
        }

        @Override
        @Transactional
        public PaymentRes createPayment(Long amount, String language,
                        HttpServletRequest httpReq, Long userId) {

                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

                /* ================== 1. TẠO PAYMENT TRƯỚC ================== */
                Payment payment = new Payment();
                payment.setAmount(amount);
                payment.setStatus(PaymentStatus.PENDING);
                payment.setUser(user);

                payment.setVnpResponseCode(null);
                payment.setVnpTransactionNo(null);
                payment.setVnpBankCode(null);
                payment.setVnpPayDate(null);
                String vnp_TxnRef = UUID.randomUUID()
                                .toString()
                                .replace("-", "")
                                .substring(0, 16);

                payment.setVnpTxnRef(vnp_TxnRef);
                // save lần 1 để lấy ID
                payment = paymentRepository.save(payment);

                /* ================== 2. BUILD VNPAY PARAMS ================== */
                String vnp_Version = "2.1.0";
                String vnp_Command = "pay";
                String orderType = "other";

                String vnp_IpAddr = PaymentConfig.getIpAddress(httpReq);
                String vnp_TmnCode = PaymentConfig.vnp_TmnCode;

                Map<String, String> vnp_Params = new HashMap<>();
                vnp_Params.put("vnp_Version", vnp_Version);
                vnp_Params.put("vnp_Command", vnp_Command);
                vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
                vnp_Params.put("vnp_Amount", String.valueOf(amount * 1000));
                vnp_Params.put("vnp_CurrCode", "VND");
                vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
                vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + vnp_TxnRef);
                vnp_Params.put("vnp_OrderType", orderType);
                vnp_Params.put("vnp_Locale",
                                (language != null && !language.isEmpty()) ? language : "vn");
                vnp_Params.put("vnp_ReturnUrl", PaymentConfig.vnp_ReturnUrl);
                vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

                Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
                SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");

                vnp_Params.put("vnp_CreateDate", formatter.format(cld.getTime()));
                cld.add(Calendar.MINUTE, 15);
                vnp_Params.put("vnp_ExpireDate", formatter.format(cld.getTime()));

                /* ================== 3. BUILD HASH & URL ================== */
                String hashData = buildHashData(vnp_Params);
                String queryUrl = buildQuery(vnp_Params);

                String vnp_SecureHash = PaymentConfig.hmacSHA512(
                                PaymentConfig.secretKey,
                                hashData);

                String paymentUrl = PaymentConfig.vnp_PayUrl
                                + "?" + queryUrl
                                + "&vnp_SecureHash=" + vnp_SecureHash;

                /* ================== 4. RESPONSE ================== */
                return new PaymentRes(vnp_SecureHash, paymentUrl);
        }

        private String buildQuery(Map<String, String> params) {
                List<String> fieldNames = new ArrayList<>(params.keySet());
                Collections.sort(fieldNames);

                StringBuilder query = new StringBuilder();

                for (Iterator<String> itr = fieldNames.iterator(); itr.hasNext();) {
                        String key = itr.next();
                        String value = params.get(key);

                        if (value != null && !value.isEmpty()) {
                                query.append(URLEncoder.encode(key, StandardCharsets.US_ASCII))
                                                .append('=')
                                                .append(URLEncoder.encode(value, StandardCharsets.US_ASCII));

                                if (itr.hasNext())
                                        query.append('&');
                        }
                }
                return query.toString();
        }

        private String buildHashData(Map<String, String> params) {
                List<String> fieldNames = new ArrayList<>(params.keySet());
                Collections.sort(fieldNames);

                StringBuilder hashData = new StringBuilder();

                for (Iterator<String> itr = fieldNames.iterator(); itr.hasNext();) {
                        String key = itr.next();
                        String value = params.get(key);

                        if (value != null && !value.isEmpty()) {
                                hashData.append(key)
                                                .append('=')
                                                .append(URLEncoder.encode(value, StandardCharsets.US_ASCII));

                                if (itr.hasNext())
                                        hashData.append('&');
                        }
                }
                return hashData.toString();
        }

        @Override
        public CallbackPaymentRes handleCallbackPayment(HttpServletRequest request) {

                Map<String, String> params = new HashMap<>();
                Enumeration<String> paramNames = request.getParameterNames();

                while (paramNames.hasMoreElements()) {
                        String name = paramNames.nextElement();
                        if (!"vnp_SecureHash".equals(name)
                                        && !"vnp_SecureHashType".equals(name)) {
                                params.put(name, request.getParameter(name));
                        }
                }

                String vnpSecureHash = request.getParameter("vnp_SecureHash");

                String signValue = PaymentConfig.hmacSHA512(
                                PaymentConfig.secretKey,
                                buildHashData(params));

                if (!signValue.equals(vnpSecureHash)) {
                        throw new InvalidSignatureException("Invalid VNPay signature");
                }

                String vnpTxnRef = request.getParameter("vnp_TxnRef");
                String responseCode = request.getParameter("vnp_ResponseCode");
                String transactionStatus = request.getParameter("vnp_TransactionStatus");

                Long amount = Long.parseLong(
                                request.getParameter("vnp_Amount")) / 1000;

                Payment payment = paymentRepository.findByVnpTxnRef(vnpTxnRef)
                                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

                if (payment.getStatus() == PaymentStatus.PAID) {
                        return new CallbackPaymentRes(
                                        vnpTxnRef,
                                        amount,
                                        true,
                                        "Payment already confirmed");
                }

                if ("00".equals(responseCode)
                                && "00".equals(transactionStatus)) {

                        payment.setStatus(PaymentStatus.PAID);
                        payment.setVnpResponseCode(responseCode);
                        payment.setVnpTransactionNo(
                                        request.getParameter("vnp_TransactionNo"));
                        payment.setVnpBankCode(
                                        request.getParameter("vnp_BankCode"));
                        payment.setVnpPayDate(
                                        request.getParameter("vnp_PayDate"));

                        User user = payment.getUser();
                        user.setTurn(user.getTurn() + 5);

                } else {
                        payment.setStatus(PaymentStatus.FAILED);
                        payment.setVnpResponseCode(responseCode);
                }

                paymentRepository.save(payment);

                /* ================== 6. RESPONSE DTO ================== */
                boolean success = payment.getStatus() == PaymentStatus.PAID;

                return new CallbackPaymentRes(
                                vnpTxnRef,
                                amount,
                                success,
                                success ? "Thanh toán thành công" : "Thanh toán thất bại");

        }

}
