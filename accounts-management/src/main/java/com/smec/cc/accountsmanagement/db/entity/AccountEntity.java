package com.smec.cc.accountsmanagement.db.entity;

import com.smec.cc.accountsmanagement.model.Account;

import javax.persistence.*;
import java.util.List;

@Table(indexes = {@Index(name = "IDX_ACCOUNT_NAME", columnList = "name")})
@Entity
public class AccountEntity {

    public AccountEntity() {
    }

    public AccountEntity(String name) {
        this(null, name);
    }

    public AccountEntity(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EventStatisticsEntity> eventStatistics;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EventRaisedEntity> eventsRaised;

    //@JoinColumn(name="account_id")
    //@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    //private List<EventRaisedEntity> eventsRaised;

    public Long getId() {
        return id;
    }

    public Account toTDO() {
        return new Account().id(id)
                            .name(name);
    }

    public void merge(Account account) {
        this.name = account.getName();
    }

    public List<EventStatisticsEntity> getEventStatistics() {
        return eventStatistics;
    }

    public void addEventStatistics(EventStatisticsEntity eventStatisticsProbe) {
        eventStatistics.add(eventStatisticsProbe);
    }

    public void addEventRaised(EventRaisedEntity eventRaisedProbe) {
        eventsRaised.add(eventRaisedProbe);
    }

    public String getName() {
        return name;
    }
}
