package com.smec.cc.accountsmanagement.api;

import com.smec.cc.accountsmanagement.db.entity.*;
import com.smec.cc.accountsmanagement.model.Event;
import org.hibernate.Session;
import org.hibernate.jdbc.Work;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.NativeWebRequest;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import javax.transaction.Transactional;
import java.sql.*;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class EventApiDelegateImpl implements EventApiDelegate {
    Logger logger = LoggerFactory.getLogger(EventApiDelegateImpl.class);

    @Autowired
    private AccountEntityRepository accountEntityRepository;

    @Autowired
    private DataSource dataSource;

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
                    EventStatisticsEntity eventStatisticsEntry = getStatisticsEntry(accountId, event.getType(), 123L);

                    if (eventStatisticsEntry.getId() == null) { // no statistics entry found yet
                        EventStatisticsEntity eventStatisticsProbe =
                                new EventStatisticsEntity(event.getType(), 123L, 1L); // TODO event.getTimestamp());
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

    @Override
    public ResponseEntity<Void> createEventsForAccountName(String accountName,
                                                           List<Event> body) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    private ResponseEntity raiseInternalError(Exception e) {
        logger.error("internal", e);
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
