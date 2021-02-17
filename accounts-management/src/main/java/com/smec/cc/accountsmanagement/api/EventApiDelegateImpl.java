package com.smec.cc.accountsmanagement.api;

import com.smec.cc.accountsmanagement.DateTimeUtil;
import com.smec.cc.accountsmanagement.db.entity.AccountEntity;
import com.smec.cc.accountsmanagement.db.entity.AccountEntityRepository;
import com.smec.cc.accountsmanagement.db.entity.EventRaisedEntity;
import com.smec.cc.accountsmanagement.db.entity.EventStatisticsEntity;
import com.smec.cc.accountsmanagement.model.Event;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.NativeWebRequest;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class EventApiDelegateImpl implements EventApiDelegate {
    Logger logger = LoggerFactory.getLogger(EventApiDelegateImpl.class);

    @Autowired
    private AccountEntityRepository accountEntityRepository;

    @Autowired
    private EntityManager entityManager;

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    @Override
    @Transactional
    public ResponseEntity<Void> createEventsForAccountId(Long accountId, List<Event> eventList) {
        try {
            //JdbcUtil.showTables(dataSource, logger);
            Optional<AccountEntity> accountByIdOpt = accountEntityRepository.findById(accountId);
            if (accountByIdOpt.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } else if (eventList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            } else {
                AccountEntity accountEntity = accountByIdOpt.get();
                for (Event event : eventList) {
                    EventRaisedEntity eventRaisedProbe = new EventRaisedEntity(event.getType(), event.getTimestamp());
                    EventStatisticsEntity eventStatisticsEntry = getStatisticsEntry(accountId, event.getType(),
                            DateTimeUtil.toDayBeginUTC(event.getTimestamp()));

                    if (eventStatisticsEntry.getId() == null) { // no statistics entry found yet
                        EventStatisticsEntity eventStatisticsProbe =
                                new EventStatisticsEntity(event.getType(), DateTimeUtil.toDayBeginUTC(event.getTimestamp()), 1L);
                        accountEntity.addEventStatistics(eventStatisticsProbe);
                    } else {
                        eventStatisticsEntry.incrementCount(1L);
                        accountEntity.addEventStatistics(eventStatisticsEntry);
                    }
                    accountEntity.addEventRaised(eventRaisedProbe);
                }
                accountEntityRepository.save(accountEntity);
//                JdbcUtil.showTables(dataSource, logger);
            }
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            raiseInternalError(e);
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private EventStatisticsEntity getStatisticsEntry(Long accountId, String type, long day) {
        final EventStatisticsEntity eventStatisticsEntityResult = new EventStatisticsEntity();
        entityManager.unwrap(Session.class)
                     .doWork(conn -> {
                         PreparedStatement pstmt = conn.prepareStatement(
                                 "SELECT es.* FROM EVENT_STATISTICS_ENTITY es, ACCOUNT_ENTITY_EVENT_STATISTICS a_es " +
                                         "WHERE a_es.ACCOUNT_ENTITY_ID = ? and " +
                                         "a_es.EVENT_STATISTICS_ID = es.ID and " +
                                         "es.DAY = ? and " +
                                         "es.TYPE = ?");
                         pstmt.setLong(1, accountId);
                         pstmt.setLong(2, day);
                         pstmt.setString(3, type);
                         ResultSet resultSet = pstmt.executeQuery();
                         if (resultSet.next()) {
                             long id = resultSet.getLong(1);
                             long countVal = resultSet.getLong(2);
                             long dayResult = resultSet.getLong(3);
                             String typeResult = resultSet.getString(4);
                             assert day == dayResult;
                             assert type == typeResult;
                             eventStatisticsEntityResult.setId(id);
                             eventStatisticsEntityResult.setCountVal(countVal);
                             eventStatisticsEntityResult.setDay(day);
                             eventStatisticsEntityResult.setType(type);
                             resultSet.close();
                         }
                         pstmt.close();
                     });
        return eventStatisticsEntityResult;
    }

    @Transactional
    @Override
    public ResponseEntity<Void> createEventsForAccountName(String accountName,
                                                           List<Event> eventList) {
        try {
            Example<AccountEntity> example = Example.of(new AccountEntity(accountName));
            Optional<AccountEntity> accountEntityByNameOpt = accountEntityRepository.findOne(example);
            if (accountEntityByNameOpt.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } else {
                return createEventsForAccountId(accountEntityByNameOpt.get()
                                                                      .getId(), eventList);
            }
        } catch (Exception e) {
            return raiseInternalError(e);
        }
    }

    @Transactional
    @Override
    public ResponseEntity<String> getEventsForAccountName(String accountName) {
        try {
            Example<AccountEntity> example = Example.of(new AccountEntity(accountName));
            Optional<AccountEntity> accountEntityByNameOpt = accountEntityRepository.findOne(example);
            if (accountEntityByNameOpt.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } else {
                AccountEntity accountEntity = accountEntityByNameOpt.get();
                List<EventRaisedEntity> raisedEvents = accountEntity.getRaisedEvents();
                StringBuffer resultBody = new StringBuffer();
                resultBody.append("date, type");
                if (raisedEvents != null && !raisedEvents.isEmpty()) {
                    for (EventRaisedEntity raisedEvent : raisedEvents) {
                        resultBody.append('\n')
                                  .append(DateTimeUtil.dformatExact.format(new Date(raisedEvent.getTimestamp())))
                                  .append(", ")
                                  .append(raisedEvent.getType());
                    }
                } else {
                    resultBody.append("\nno event entries found for account: ")
                              .append(accountEntity.getName());
                }
                return ResponseEntity.ok(resultBody.toString());
            }
        } catch (Exception e) {
            return raiseInternalError(e);
        }
    }

    private ResponseEntity raiseInternalError(Exception e) {
        logger.error("internal", e);
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
