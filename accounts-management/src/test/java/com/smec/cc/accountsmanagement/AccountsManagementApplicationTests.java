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
        Account newAccount = api.addAccount(accountPrototype)
                                .getBody();

        // then
        assertTrue(newAccount.getId() > 0);
        assertEquals("Account-1", newAccount.getName());

        Account newAccountById = api.getAccountById(newAccount.getId())
                                    .getBody();
        assertEquals(newAccount.getId(), newAccountById.getId());
        assertEquals(newAccount.getName(), newAccountById.getName());
    }

    @Test
    void createAccount_NameAlreadyExistsError() {
        Account existingAccount = api.addAccount(new Account().name("Account-8"))
                                     .getBody();
        // when
        ResponseEntity<Account> addAccountResponse = api.addAccount(new Account().name(existingAccount.getName()));

        // then
        assertEquals(HttpStatus.BAD_REQUEST, addAccountResponse.getStatusCode());
    }

    @Test
    void getAccountById() {
        ResponseEntity<Account> newAccountResponse = api.addAccount(new Account().name("Account-2"));
        Account newAccount = newAccountResponse.getBody();

        // when
        Account accountById = api.getAccountById(newAccount.getId()).getBody();

        // then
        assertNotNull(accountById);
        assertEquals(newAccount.getId(), accountById.getId());
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
        Account accountById = api.getAccountById(newAccount.getId())
                                 .getBody();

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
        Account newAccount = api.addAccount(new Account().name("Account-4"))
                                .getBody();
        Account accountById = api.getAccountById(newAccount.getId())
                                 .getBody();

        // when
        accountById.setName("Account-4-CHANGED");
        ResponseEntity<Void> updateAccountResponse = api.updateAccount(accountById);

        // then
        assertEquals(HttpStatus.OK, updateAccountResponse.getStatusCode());
        Account updatedAccountById = api.getAccountById(accountById.getId())
                                        .getBody();
        assertEquals(accountById.getId(), updatedAccountById.getId());
        assertEquals(accountById.getName(), updatedAccountById.getName());
    }


    @Test
    void updateAccount_InvalidId() {
        Account account = new Account().name("Account-5");
        ResponseEntity<Void> updateAccountResponse = api.updateAccount(account);
        assertEquals(HttpStatus.BAD_REQUEST, updateAccountResponse.getStatusCode());
    }

    @Test
    void updateAccount_NotFoundError() {
        Account account = api.addAccount(new Account().name("Account-5"))
                             .getBody();
        // when
        account.setId(Long.MAX_VALUE);
        ResponseEntity<Void> updateAccountResponse = api.updateAccount(account);

        // then
        assertEquals(HttpStatus.NOT_FOUND, updateAccountResponse.getStatusCode());
    }

    @Test
    void updateAccount_NameAlreadyExistsError() {
        Account existingAccount = api.addAccount(new Account().name("Account-6"))
                                     .getBody();
        Account updatingAccount = api.addAccount(new Account().name("Account-7"))
                                     .getBody();
        // when
        Account changedAccount = new Account().id(updatingAccount.getId())
                                              .name(existingAccount.getName());
        ResponseEntity<Void> updateAccountResponse = api.updateAccount(changedAccount);

        // then
        assertEquals(HttpStatus.BAD_REQUEST, updateAccountResponse.getStatusCode());
    }

    @Test
    void findAccountByName() {
        Account account = api.addAccount(new Account().name("Account-9"))
                                     .getBody();
        // when
        Account accountByName = api.findAccountByName(account.getName()).getBody();

        // then
        assertNotNull(accountByName);
        assertEquals(account.getId(), accountByName.getId());
    }

    @Test
    void findAccountByName_NotFound() {
        Account account = (new Account().name("Account-NOT-EXISTING"));

        // when
        ResponseEntity<Account> accountByNameResponse = api.findAccountByName(account.getName());

        // then
        assertEquals(HttpStatus.NOT_FOUND, accountByNameResponse.getStatusCode());
    }
}
