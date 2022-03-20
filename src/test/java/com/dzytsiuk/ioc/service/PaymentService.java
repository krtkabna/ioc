package com.dzytsiuk.ioc.service;

import java.util.Objects;

public class PaymentService {
    private MailService mailService;
    private int maxAmount;

    public PaymentService() {
    }

    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }

    public void setMaxAmount(int maxAmount) {
        this.maxAmount = maxAmount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentService that = (PaymentService) o;
        return maxAmount == that.maxAmount &&
                Objects.equals(mailService, that.mailService);
    }

    @Override
    public int hashCode() {

        return Objects.hash(mailService, maxAmount);
    }

}
