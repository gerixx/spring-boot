package com.smec.cc.accountsmanagement.db.entity;

import javax.persistence.*;

@Table(indexes = {@Index(name = "IDX_EVENT_STATISTICS_DAY", columnList = "day")})
@Entity
public class EventStatisticsEntity {

    public EventStatisticsEntity() {
        //this(null, 0L);
    }

    public EventStatisticsEntity(String type, Long day) {
        this.type = type;
        this.day = day;
        this.countVal = 0L;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Long id;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "event_id", nullable = false)
//    private EventEntity event;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private Long day;

    @Column(nullable = false)
    private Long countVal;

    public Long incrementCount(Long val) {
        countVal += val;
        return countVal;
    }

    public String getType() {
        return type;
    }

    public Long getDay() {
        return day;
    }

    public Long getCountVal() {
        return countVal;
    }
}
