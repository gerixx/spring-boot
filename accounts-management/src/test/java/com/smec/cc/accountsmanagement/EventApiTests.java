package com.smec.cc.accountsmanagement;

import com.smec.cc.accountsmanagement.api.AccountApiDelegate;
import com.smec.cc.accountsmanagement.api.EventApiDelegate;
import com.smec.cc.accountsmanagement.api.StatisticsApiController;
import com.smec.cc.accountsmanagement.api.StatisticsApiDelegate;
import com.smec.cc.accountsmanagement.model.Account;
import com.smec.cc.accountsmanagement.model.Event;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EventApiTests {

    @Autowired
    private AccountApiDelegate apiAccounts;

    @Autowired
    private EventApiDelegate apiEvents;

    @Autowired
    private StatisticsApiDelegate apiStatistics;

    @Test
    public void createNewEventsForAccountId() {
        Account account = apiAccounts.addAccount(new Account().name("EventsTest-Account-1"))
                                     .getBody();
        //when
        List<Event> evenList = Arrays.asList(
                new Event().type("EventType-1")
                           .timestamp(System.currentTimeMillis()),
                new Event().type("EventType-2")
                           .timestamp(System.currentTimeMillis())
        );
        ResponseEntity<Void> createEventsResponse = apiEvents.createEventsForAccountId(account.getId(), evenList);

//        evenList = Arrays.asList(
//                new Event().type("EventType-3")
//                           .timestamp(System.currentTimeMillis()),
//                new Event().type("EventType-4")
//                           .timestamp(System.currentTimeMillis()));
//        createEventsResponse = apiEvents.createEventsForAccountId(account.getId(), evenList);

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

    @Test
    public void raiseKnownEventsForAccountId() {
        Account account = apiAccounts.addAccount(new Account().name("EventsTest-Account-2"))
                                     .getBody();
        List<Event> evenList = Arrays.asList(
                new Event().type("EventType-1")
                           .timestamp(System.currentTimeMillis()),
                new Event().type("EventType-2")
                           .timestamp(System.currentTimeMillis())
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
        assertTrue(statisticsText.contains("EventType-1, 2"));
        assertTrue(statisticsText.contains("EventType-2, 2"));
    }
}