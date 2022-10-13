package com.mihail.baciu.account.transfer.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import static javax.persistence.GenerationType.IDENTITY;

@Data
@Entity
public class Account {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long ownerId;

    @Column(nullable = false)
    private String currency;

    @Column(nullable = false)
    private Double balance;
}
