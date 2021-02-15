package com.smec.cc.accountsmanagement.db.entity;

import com.smec.cc.accountsmanagement.model.Account;

import javax.persistence.*;

@Table(indexes = { @Index(name = "IDX_ACCOUNT_NAME", columnList = "name") })
@Entity
public class AccountEntity {

    public AccountEntity() {
    }

    public AccountEntity(String name) {
        this(null, name);
    }

    public AccountEntity(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    public Long getId() {
        return id;
    }

    public Account toTDO() {
        return new Account().id(id).name(name);
    }

    public void merge(Account account) {
        this.name = account.getName();
    }
}
