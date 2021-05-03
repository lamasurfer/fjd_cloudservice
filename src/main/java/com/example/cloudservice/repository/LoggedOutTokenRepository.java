package com.example.cloudservice.repository;

import com.example.cloudservice.model.LoggedOutToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

public interface LoggedOutTokenRepository extends JpaRepository<LoggedOutToken, String> {

    void removeAllByStoreTillBefore(Date now);
}
