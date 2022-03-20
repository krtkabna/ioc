package com.dzytsiuk.ioc.service;


import java.util.Objects;

public class UserService {
    private MailService mailService;

    public UserService() {
    }

    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserService that = (UserService) o;
        return Objects.equals(mailService, that.mailService);
    }

    @Override
    public int hashCode() {

        return Objects.hash(mailService);
    }

}
