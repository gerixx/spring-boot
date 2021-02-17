package com.smec.cc.accountsmanagement.api;

import com.smec.cc.accountsmanagement.DateTimeUtil;
import com.smec.cc.accountsmanagement.db.entity.AccountEntity;
import com.smec.cc.accountsmanagement.db.entity.AccountEntityRepository;
import com.smec.cc.accountsmanagement.db.entity.EventStatisticsEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.NativeWebRequest;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class StatisticsApiDelegateImpl implements StatisticsApiDelegate {
    Logger logger = LoggerFactory.getLogger(StatisticsApiDelegateImpl.class);

    @Autowired
    private AccountEntityRepository accountEntityRepository;

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    @Transactional
    @Override
    public ResponseEntity<String> getStatistics(String accountName) {
        try {
            AccountEntity accountEntityProbe = new AccountEntity(accountName);
            Optional<AccountEntity> accountEntityOpt = accountEntityRepository.findOne(Example.of(accountEntityProbe));
            if (accountEntityOpt.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } else {
                StringBuffer resultBody = new StringBuffer();
                AccountEntity accountEntity = accountEntityOpt.get();
                Set<EventStatisticsEntity> eventStatistics = accountEntity.getEventStatistics();
                resultBody.append("day, type, count");
                if (eventStatistics != null && !eventStatistics.isEmpty()) {
                    for (EventStatisticsEntity eventStatistic : eventStatistics) {
                        resultBody.append('\n')
                                  .append(DateTimeUtil.toDayAsStringUTC(eventStatistic.getDay()))
                                  .append(", ")
                                  .append(eventStatistic.getType())
                                  .append(", ")
                                  .append(eventStatistic.getCountVal());
                    }
                } else {
                    resultBody.append("\nno statistic entries found for account: ").append(accountEntity.getName());
                }
                return ResponseEntity.ok(resultBody.toString());
            }
        } catch (Exception e) {
            raiseInternalError(e);
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity raiseInternalError(Exception e) {
        logger.error("internal", e);
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
