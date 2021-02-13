package com.smec.cc.accountsmanagement.api;

import com.smec.cc.accountsmanagement.model.Account;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.NativeWebRequest;

import java.time.OffsetDateTime;
import java.util.Optional;

@Service
public class AccountApiDelegateImpl implements AccountApiDelegate {
    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    @Override
    public ResponseEntity<Void> addAccount(Account body) {
        return null;
    }

    @Override
    public ResponseEntity<Void> deleteAccount(Long accountId) {
        return null;
    }

    @Override
    public ResponseEntity<Account> findAccountByName(String name) {
        return null;
    }

    @Override
    public ResponseEntity<Account> getAccountById(Long accountId) {
        Account account = new Account();
        account.id(4711L).name("Account-1").creationDate(OffsetDateTime.now());
        return ResponseEntity.ok(account);
    }

    @Override
    public ResponseEntity<Void> updateAccount(Account body) {
        return null;
    }
}
