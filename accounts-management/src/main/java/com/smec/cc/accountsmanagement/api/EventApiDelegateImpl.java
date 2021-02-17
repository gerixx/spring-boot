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
            } else {
                AccountEntity accountEntity = accountByIdOpt.get();
                boolean newEntities = false;
                for (Event event : eventList) {
                    if (!eventTypeAlreadyExistsInDatabase(event.getType())) {
                        EventStatisticsEntity eventStatisticsProbe = new EventStatisticsEntity(event.getType(), 123L); // TODO event.getTimestamp());
                        EventRaisedEntity eventRaisedProbe = new EventRaisedEntity(event.getType(), event.getTimestamp());
                        accountEntity.addEventStatistics(eventStatisticsProbe);
                        accountEntity.addEventRaised(eventRaisedProbe);
                        newEntities = true;
                    }
                    // update raised events

                    // update statistics

                }
                if (newEntities) {
                    accountEntityRepository.save(accountEntity);
                    Optional<AccountEntity> acc = accountEntityRepository.findById(accountId);
                    if (acc.isPresent()) {
                        List<EventStatisticsEntity> events = acc.get()
                                                      .getEventStatistics();
                        System.out.println("hi");
                    }
                }

                if (!eventTypeAlreadyExistsInDatabase(eventList.get(0).getType())) {
                    logger.info("event found in db");
                }
//                eventEntities = accountEntity.getEvents();
//                for (EventEntity eventEntity : eventEntities) {
//                    logger.info("existing event: " + eventEntity.getType());
//                }
            }

//            // query by example
//            AccountEntity accountEntity = accountByIdOpt.get();
//            for (Event event : eventList) {
//                EventEntity eventPrototype = new EventEntity(event.getType());
//                accountEntity.addEvent(eventPrototype);
//                Optional<AccountEntity> one = accountEntityRepository.findOne(Example.of(accountEntity));
//                if (one.isPresent()) {
//                    AccountEntity accountEntity1 = one.get();
//                }
//            }

            //JdbcUtil.showTables(dataSource, logger);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            raiseInternalError(e);
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void insertEventTypeIfNotExists(String type) {
        entityManager.unwrap(Session.class).doWork(conn -> {
            PreparedStatement pstmt = conn.prepareStatement(
                    "INSERT INTO EVENT_ENTITY (type) VALUES (?)");
            pstmt.setString(1, type);
            pstmt.executeUpdate();
        });
    }

    boolean result;
    private boolean eventTypeAlreadyExistsInDatabase(String type) throws SQLException {
        entityManager.unwrap(Session.class).doWork(conn -> {
            PreparedStatement pstmt = conn.prepareStatement("SELECT 1 FROM EVENT_ENTITY WHERE type = ?");
            pstmt.setString(1, type);
            ResultSet rs = pstmt.getResultSet();
            result = rs != null && rs.next();
        });
        return result;
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
