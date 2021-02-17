package com.smec.cc.accountsmanagement.db.entity;

import com.smec.cc.accountsmanagement.DateTimeUtil;

import javax.persistence.*;
import java.time.DateTimeException;
import java.util.Date;

@Entity
public class EventRaisedEntity {

    public EventRaisedEntity() {
    }

    public EventRaisedEntity(String type, Long timestamp) {
        this.timestamp = timestamp;
        this.type = type;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Long id;

    //@ManyToOne
    //@JoinColumn(name="xxevent_id", nullable=false)
    //private EventEntity event;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private Long timestamp;

    public Long getTimestamp() {
        return timestamp;
    }

    public String getType() {
        return type;
    }
}
