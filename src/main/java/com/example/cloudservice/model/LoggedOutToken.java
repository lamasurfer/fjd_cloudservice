package com.example.cloudservice.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;
import java.util.Objects;

@Entity(name = "logged_out_tokens")
public class LoggedOutToken {
    @Id
    @Column(length = 750)
    private String token;
    private Date storeTill;

    public LoggedOutToken() {
    }

    public LoggedOutToken(String token, Date storeTill) {
        this.token = token;
        this.storeTill = storeTill;
    }

    public String getToken() {
        return token;
    }

    public LoggedOutToken setToken(String token) {
        this.token = token;
        return this;
    }

    public Date getStoreTill() {
        return storeTill;
    }

    public LoggedOutToken setStoreTill(Date storeTill) {
        this.storeTill = storeTill;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoggedOutToken that = (LoggedOutToken) o;
        return Objects.equals(token, that.token)
                && Objects.equals(storeTill, that.storeTill);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token, storeTill);
    }

    @Override
    public String toString() {
        return "LoggedOutToken{" +
                "token='" + token + '\'' +
                ", storeTill=" + storeTill +
                '}';
    }
}
