package com.nbloi.conventional.eda.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class CustomerDTO {

    private String customerId;
    private String name;
    private String email;
    private String phoneNumber;
    private BigDecimal balance;

    @Column(nullable = false, updatable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    public CustomerDTO(){}

    public CustomerDTO(String customerId, String name, String email, String phoneNumber, BigDecimal balance) {
        this.customerId = customerId;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.balance = balance;
        this.createdAt = LocalDateTime.now();
    }
}
