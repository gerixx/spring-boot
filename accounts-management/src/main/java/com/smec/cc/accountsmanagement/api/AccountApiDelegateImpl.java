package com.smec.cc.accountsmanagement.api;

import com.smec.cc.accountsmanagement.db.entity.AccountEntity;
import com.smec.cc.accountsmanagement.db.entity.AccountEntityRepository;
import com.smec.cc.accountsmanagement.model.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.NativeWebRequest;

import javax.sql.DataSource;
import java.util.Optional;

@Service
public class AccountApiDelegateImpl implements AccountApiDelegate {
    Logger logger = LoggerFactory.getLogger(AccountApiDelegateImpl.class);

    @Autowired
    private AccountEntityRepository accountEntityRepository;

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    @Override
    public ResponseEntity<Account> addAccount(Account newAccount) {
        try {
            // TODO validate name
            AccountEntity accountEntity = new AccountEntity(newAccount.getName());
            AccountEntity savedAccountEntity = accountEntityRepository
                    .save(accountEntity);
            return ResponseEntity.ok(savedAccountEntity.toTDO());
        } catch (DataIntegrityViolationException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return raiseInternalError(e);
        }
    }

    @Override
    public ResponseEntity<Void> deleteAccount(Long accountId) {
        try {
            accountEntityRepository.deleteById(accountId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (EmptyResultDataAccessException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return raiseInternalError(e);
        }
    }

    @Override
    public ResponseEntity<Account> findAccountByName(String name) {
        try {
            Example<AccountEntity> example = Example.of(new AccountEntity(name));
            Optional<AccountEntity> accountEntityByName = accountEntityRepository.findOne(example);
            if (accountEntityByName.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } else {
                return ResponseEntity.ok(accountEntityByName.get().toTDO());
            }
        } catch (Exception e) {
            return raiseInternalError(e);
        }
    }

    @Override
    public ResponseEntity<Account> getAccountById(Long accountId) {
        try {
            Optional<AccountEntity> accountEntityById = accountEntityRepository.findById(accountId);
            if (accountEntityById.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } else {
                return ResponseEntity.ok(accountEntityById.get().toTDO());
            }
        } catch (Exception e) {
            return raiseInternalError(e);
        }
    }

    @Override
    public ResponseEntity<Void> updateAccount(Account account) {
        try {
            if (account.getId() == null) {
                throw new IllegalArgumentException("Account id value 'null'  is not allowed.");
            }
            // TODO avoid additional find round trip
            Optional<AccountEntity> accountById = accountEntityRepository.findById(account.getId());
            if (accountById.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            accountById.get().merge(account);
            accountEntityRepository.save(accountById.get());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (DataIntegrityViolationException | IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return raiseInternalError(e);
        }
    }

    private ResponseEntity raiseInternalError(Exception e) {
        logger.error("internal", e);
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
