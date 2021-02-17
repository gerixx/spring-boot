package com.smec.cc.accountsmanagement.schedule;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.smec.cc.accountsmanagement.DateTimeUtil;
import com.smec.cc.accountsmanagement.db.entity.AccountEntityRepository;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

@Service
public class ScheduledTasks {
    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);

    private static final long maxAgeOfEventsInMillis = 30 * DateTimeUtil.millisOfDay;

    @Autowired
    private EntityManager entityManager;

    @Scheduled(initialDelay = 0, fixedRate = DateTimeUtil.millisOfDay)
    @Transactional
    public void deleteOutagedEvents() {
        logger.info("The time is now {}", new Date());
        entityManager.unwrap(Session.class)
                     .doWork(conn -> {
                         long maxAge = System.currentTimeMillis() - maxAgeOfEventsInMillis;
                         PreparedStatement pstmt = conn.prepareStatement(
                                 "DELETE FROM ACCOUNT_ENTITY_EVENTS_RAISED " +
                                         "WHERE  EVENTS_RAISED_ID IN (SELECT ID " +
                                         "              FROM   EVENT_RAISED_ENTITY " +
                                         "              WHERE  TIMESTAMP < ?)");
                         pstmt.setLong(1, maxAge);
                         pstmt.executeUpdate();
                         pstmt.close();

                         pstmt = conn.prepareStatement(
                                 "DELETE FROM EVENT_RAISED_ENTITY WHERE TIMESTAMP < ?");
                         pstmt.setLong(1, maxAge);
                         int cnt = pstmt.executeUpdate();
                         pstmt.close();

                         logger.info("Deleted {} events from database.", cnt);
                     });
    }
}
