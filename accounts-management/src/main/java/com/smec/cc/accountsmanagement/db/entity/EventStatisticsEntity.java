package com.smec.cc.accountsmanagement.db.entity;

import javax.persistence.*;
import java.util.Objects;

@Table(indexes = {@Index(name = "IDX_EVENT_STATISTICS_DAY", columnList = "day")})
@Entity
public class EventStatisticsEntity {

    public EventStatisticsEntity() {
    }

    public EventStatisticsEntity(String type, Long day, Long countVal) {
        this.type = type;
        this.day = day;
        this.countVal = countVal;
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

    public void setId(long id) {
        this.id = id;
    }

    public void setCountVal(long countVal) {
        this.countVal = countVal;
    }

    public void setDay(long day) {
        this.day = day;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getId() {
        return this.id;
    }

    @Override
    public boolean equals(Object o) {
        // self check
        if (this == o)
            return true;
        // null check
        if (o == null)
            return false;
        // type check and cast
        if (getClass() != o.getClass())
            return false;
        EventStatisticsEntity es = (EventStatisticsEntity) o;
        // field comparison
        return Objects.equals(this.id, es.id)
                && Objects.equals(this.day, es.day)
                && Objects.equals(this.type, es.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.day, this.type);
    }
}
