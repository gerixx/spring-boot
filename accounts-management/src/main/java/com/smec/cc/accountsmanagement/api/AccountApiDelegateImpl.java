package com.smec.cc.accountsmanagement.api;

import com.smec.cc.accountsmanagement.model.Account;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.NativeWebRequest;

import java.time.OffsetDateTime;
import java.util.*;

@Service
public class AccountApiDelegateImpl implements AccountApiDelegate {
    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    private static Map<Long, Account> dbAccounts = Collections.synchronizedMap(new HashMap<>());
    private static Set<String> dbAccountNames = Collections.synchronizedSet(new HashSet<>());

    // use for creation an Account prototype object,
    // in the case Accounts are getting more complex in the future
    @Override
    public ResponseEntity<Account> addAccount(Account newAccount) {
        // TODO validate name
        // TODO check if name already in use
        // TODO create account in DB
        long accountId = System.currentTimeMillis();
        newAccount.id(accountId)
                  .creationDate(OffsetDateTime.now());
        if (!dbAccountNames.add(newAccount.getName())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            dbAccounts.put(accountId, newAccount);
            return ResponseEntity.ok(newAccount);
        }
    }

    @Override
    public ResponseEntity<Void> deleteAccount(Long accountId) {
        Account removedAccount = dbAccounts.remove(accountId);
        if (removedAccount == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    @Override
    public ResponseEntity<Account> findAccountByName(String name) {
        return null;
    }

    @Override
    public ResponseEntity<Account> getAccountById(Long accountId) {
        Account account = dbAccounts.get(accountId);
        if (account == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return ResponseEntity.ok(account);
        }
    }

    @Override
    public ResponseEntity<Void> updateAccount(Account account) {
        // TODO synchronize
        Account accountOld = dbAccounts.get(account.getId());
        if (accountOld == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else if (!dbAccountNames.add(account.getName())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            assert accountOld.getId() == account.getId();
            dbAccounts.put(account.getId(), account);
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }
}
