package com.smec.cc.accountsmanagement;

import com.smec.cc.accountsmanagement.api.AccountApiDelegate;
import com.smec.cc.accountsmanagement.model.Account;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AccountsManagementApplicationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private AccountApiDelegate api;

    @Test
    void createAccount() {
        // when
        Account accountPrototype = new Account().name("Account-1");
        Account newAccount = api.addAccount(accountPrototype).getBody();

        // then
        assertTrue(newAccount.getId() > 0);
        assertEquals("Account-1", newAccount.getName());

        Account newAccountById = api.getAccountById(newAccount.getId()).getBody();
        assertEquals(newAccount.getId(), newAccountById.getId());
        assertEquals(newAccount.getName(), newAccountById.getName());
    }

    @Test
    void getAccountById() {
        ResponseEntity<Account> newAccountResponse = api.addAccount(new Account().name("Account-2"));
        Account newAccount = newAccountResponse.getBody();

        // when
        ResponseEntity<Account> accountByIdResponse = api.getAccountById(newAccount.getId());

        // then
        assertNotNull(accountByIdResponse);
        assertEquals(newAccount.getId(), accountByIdResponse.getBody()
                                                            .getId());
    }

    @Test
    void getAccountById_NotFoundError() {
        ResponseEntity<Account> accountByIdResponse = api.getAccountById(0L);
        assertEquals(HttpStatus.NOT_FOUND, accountByIdResponse.getStatusCode());
    }

    @Test
    void deleteAccount() {
        ResponseEntity<Account> newAccountResponse = api.addAccount(new Account().name("Account-3"));
        Account newAccount = newAccountResponse.getBody();
        Account accountById = api.getAccountById(newAccount.getId()).getBody();

        // when
        ResponseEntity<Void> deleteAccountResponse = api.deleteAccount(accountById.getId());

        // then
        assertEquals(HttpStatus.OK, deleteAccountResponse.getStatusCode());
        ResponseEntity<Account> accountByIdResponse = api.getAccountById(accountById.getId());
        assertEquals(HttpStatus.NOT_FOUND, accountByIdResponse.getStatusCode());
    }

    @Test
    void deleteAccount_NotFoundError() {
        ResponseEntity<Void> deleteAccountResponse = api.deleteAccount(0L);
        assertEquals(HttpStatus.NOT_FOUND, deleteAccountResponse.getStatusCode());
    }

    @Test
    void updateAccount() {
        Account newAccount = api.addAccount(new Account().name("Account-4")).getBody();
        Account accountById = api.getAccountById(newAccount.getId()).getBody();

        // when
        accountById.setName("Account-4-CHANGED");
        ResponseEntity<Void> updateAccountResponse = api.updateAccount(accountById);

        // then
        assertEquals(HttpStatus.OK, updateAccountResponse.getStatusCode());
        Account updatedAccountById = api.getAccountById(accountById.getId()).getBody();
        assertEquals(accountById.getId(), updatedAccountById.getId());
        assertEquals(accountById.getName(), updatedAccountById.getName());
    }
}
