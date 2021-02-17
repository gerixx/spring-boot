package com.smec.cc.accountsmanagement;

import com.smec.cc.accountsmanagement.api.AccountApiDelegate;
import com.smec.cc.accountsmanagement.api.EventApiDelegate;
import com.smec.cc.accountsmanagement.api.StatisticsApiController;
import com.smec.cc.accountsmanagement.api.StatisticsApiDelegate;
import com.smec.cc.accountsmanagement.model.Account;
import com.smec.cc.accountsmanagement.model.Event;
import com.smec.cc.accountsmanagement.schedule.ScheduledTasks;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.persistence.EntityManager;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EventApiTests {

    @Autowired
    private AccountApiDelegate apiAccounts;

    @Autowired
    private EventApiDelegate apiEvents;

    @Autowired
    private StatisticsApiDelegate apiStatistics;

    @Test
    public void createNewEventsForAccountId() throws ParseException {
        Account account = apiAccounts.addAccount(new Account().name("EventsTest-Account-1"))
                                     .getBody();
        Date timeStamp1 = DateTimeUtil.parseDateFrom("2021-02-17T14:51:15.022+0000");
        Date timeStamp2 = DateTimeUtil.parseDateFrom("2021-02-17T14:56:15.099+0000");
        Date timeStamp3 = DateTimeUtil.parseDateFrom("2021-02-18T14:57:15.099+0000");
        Date timeStamp4 = DateTimeUtil.parseDateFrom("2021-02-18T14:58:15.099+0000");

        //when
        List<Event> evenList = Arrays.asList(
                new Event().type("EventType-1")
                           .timestamp(timeStamp1.getTime()),
                new Event().type("EventType-2")
                           .timestamp(timeStamp2.getTime())
        );
        ResponseEntity<Void> createEventsResponse = apiEvents.createEventsForAccountId(account.getId(), evenList);

        evenList = Arrays.asList(
                new Event().type("EventType-3")
                           .timestamp(timeStamp3.getTime()),
                new Event().type("EventType-4")
                           .timestamp(timeStamp4.getTime()));
        createEventsResponse = apiEvents.createEventsForAccountId(account.getId(), evenList);

        // then
        assertEquals(HttpStatus.OK, createEventsResponse.getStatusCode());
        ResponseEntity<String> statisticsResponse = apiStatistics.getStatistics(account.getName());
        String statisticsText = statisticsResponse.getBody();
        System.out.println("====================================================");
        System.out.println(statisticsText);
        System.out.println("====================================================");
        assertTrue(statisticsText.contains("2021-02-17, EventType-1, 1"));
        assertTrue(statisticsText.contains("2021-02-17, EventType-2, 1"));
        assertTrue(statisticsText.contains("2021-02-18, EventType-3, 1"));
        assertTrue(statisticsText.contains("2021-02-18, EventType-4, 1"));
    }

    @Test
    public void createKnownEventsForAccountId() throws ParseException {
        Account account = apiAccounts.addAccount(new Account().name("EventsTest-Account-2"))
                                     .getBody();
        Date timeStamp1 = DateTimeUtil.parseDateFrom("2021-02-17T14:51:15.022+0000");
        Date timeStamp2 = DateTimeUtil.parseDateFrom("2021-02-17T14:56:15.099+0000");
        Date timeStamp3 = DateTimeUtil.parseDateFrom("2021-02-18T14:57:15.099+0000");
        Date timeStamp4 = DateTimeUtil.parseDateFrom("2021-02-18T14:58:15.099+0000");

        List<Event> evenList = Arrays.asList(
                new Event().type("EventType-1")
                           .timestamp(timeStamp1.getTime()),
                new Event().type("EventType-2")
                           .timestamp(timeStamp2.getTime()),
                new Event().type("EventType-3")
                           .timestamp(timeStamp3.getTime()),
                new Event().type("EventType-4")
                           .timestamp(timeStamp4.getTime())
        );
        assertEquals(HttpStatus.OK, apiEvents.createEventsForAccountId(account.getId(), evenList)
                                             .getStatusCode());

        // when
        evenList = Arrays.asList(
                new Event().type("EventType-1")
                           .timestamp(System.currentTimeMillis()),
                new Event().type("EventType-2")
                           .timestamp(System.currentTimeMillis())
        );

        // then
        assertEquals(HttpStatus.OK, apiEvents.createEventsForAccountId(account.getId(), evenList)
                                             .getStatusCode());
        ResponseEntity<String> statisticsResponse = apiStatistics.getStatistics(account.getName());
        String statisticsText = statisticsResponse.getBody();
        System.out.println("====================================================");
        System.out.println(statisticsText);
        System.out.println("====================================================");
        assertNotNull(statisticsText);
        assertTrue(statisticsText.contains("2021-02-17, EventType-1, 2"));
        assertTrue(statisticsText.contains("2021-02-17, EventType-2, 2"));
        assertTrue(statisticsText.contains("2021-02-18, EventType-3, 1"));
        assertTrue(statisticsText.contains("2021-02-18, EventType-4, 1"));
    }

    @Test
    public void retrieveEventsForAccountByName() {
        Account account = apiAccounts.addAccount(new Account().name("EventsTest-Account-3"))
                                     .getBody();
        long oneDay = new Date(1024L).getTime();
        List<Event> evenList = Arrays.asList(
                new Event().type("EventType-1")
                           .timestamp(oneDay),
                new Event().type("EventType-2")
                           .timestamp(oneDay)
                           .timestamp(oneDay)
        );
        assertEquals(HttpStatus.OK, apiEvents.createEventsForAccountId(account.getId(), evenList)
                                             .getStatusCode());

        // then
        ResponseEntity<String> eventsResponse = apiEvents.getEventsForAccountName(account.getName());
        assertEquals(HttpStatus.OK, eventsResponse.getStatusCode());
        String eventsText = eventsResponse.getBody();
        System.out.println("====================================================");
        System.out.println(eventsText);
        System.out.println("====================================================");
        assertNotNull(eventsText);
        assertTrue(eventsText.contains(DateTimeUtil.dformatExact.format(new Date(oneDay)) + ", EventType-1"));
        assertTrue(eventsText.contains(DateTimeUtil.dformatExact.format(new Date(oneDay)) + ", EventType-2"));
    }

    @Test
    public void createEventsForAccountName() {

        Account account = apiAccounts.addAccount(new Account().name("EventsTest-Account-4"))
                                     .getBody();
        //when
        List<Event> evenList = Arrays.asList(
                new Event().type("EventType-1")
                           .timestamp(System.currentTimeMillis()),
                new Event().type("EventType-2")
                           .timestamp(System.currentTimeMillis())
        );
        ResponseEntity<Void> createEventsResponse = apiEvents.createEventsForAccountName(account.getName(), evenList);

        // then
        assertEquals(HttpStatus.OK, createEventsResponse.getStatusCode());
        ResponseEntity<String> statisticsResponse = apiStatistics.getStatistics(account.getName());
        String statisticsText = statisticsResponse.getBody();
        System.out.println("====================================================");
        System.out.println(statisticsText);
        System.out.println("====================================================");
        assertTrue(statisticsText.contains("EventType-1, 1"));
        assertTrue(statisticsText.contains("EventType-2, 1"));
    }

    @Autowired
    private ScheduledTasks scheduledTasks;

    @Test
    public void deleteOutagedEvents() {
        Account account = apiAccounts.addAccount(new Account().name("EventsTest-Account-5"))
                                     .getBody();
        long now = System.currentTimeMillis();
        Date timeStamp1 = new Date(now);
        Date timeStamp2 = new Date(now - DateTimeUtil.millisOfDay);
        Date timeStamp3 = new Date(now - 2 * DateTimeUtil.millisOfDay);
        Date timeStamp4 = new Date(now - 30 * DateTimeUtil.millisOfDay);
        Date timeStamp5 = new Date(now - 31 * DateTimeUtil.millisOfDay);

        List<Event> evenList = Arrays.asList(
                new Event().type("EventType-1")
                           .timestamp(timeStamp1.getTime()),
                new Event().type("EventType-2")
                           .timestamp(timeStamp2.getTime()),
                new Event().type("EventType-3")
                           .timestamp(timeStamp3.getTime()),
                new Event().type("EventType-4")
                           .timestamp(timeStamp4.getTime()),
                new Event().type("EventType-5")
                           .timestamp(timeStamp5.getTime())
        );
        assertEquals(HttpStatus.OK, apiEvents.createEventsForAccountId(account.getId(), evenList)
                                             .getStatusCode());
        ResponseEntity<String> eventsResponse = apiEvents.getEventsForAccountName(account.getName());
        assertEquals(HttpStatus.OK, eventsResponse.getStatusCode());
        String eventsText = eventsResponse.getBody();
        System.out.println("====================================================");
        System.out.println(eventsText);
        System.out.println("====================================================");
        assertNotNull(eventsText);
        assertTrue(eventsText.contains(DateTimeUtil.dformatExact.format(timeStamp1) + ", EventType-1"));
        assertTrue(eventsText.contains(DateTimeUtil.dformatExact.format(timeStamp2) + ", EventType-2"));
        assertTrue(eventsText.contains(DateTimeUtil.dformatExact.format(timeStamp3) + ", EventType-3"));
        assertTrue(eventsText.contains(DateTimeUtil.dformatExact.format(timeStamp4) + ", EventType-4"));
        assertTrue(eventsText.contains(DateTimeUtil.dformatExact.format(timeStamp5) + ", EventType-5"));

        // when
        scheduledTasks.deleteOutagedEvents();

        // then
        eventsResponse = apiEvents.getEventsForAccountName(account.getName());
        assertEquals(HttpStatus.OK, eventsResponse.getStatusCode());
        eventsText = eventsResponse.getBody();
        System.out.println("====================================================");
        System.out.println(eventsText);
        System.out.println("====================================================");
        assertNotNull(eventsText);
        assertTrue(eventsText.contains(DateTimeUtil.dformatExact.format(timeStamp1) + ", EventType-1"));
        assertTrue(eventsText.contains(DateTimeUtil.dformatExact.format(timeStamp2) + ", EventType-2"));
        assertTrue(eventsText.contains(DateTimeUtil.dformatExact.format(timeStamp3) + ", EventType-3"));
        assertFalse(eventsText.contains(DateTimeUtil.dformatExact.format(timeStamp4) + ", EventType-4"));
        assertFalse(eventsText.contains(DateTimeUtil.dformatExact.format(timeStamp5) + ", EventType-5"));
    }
}