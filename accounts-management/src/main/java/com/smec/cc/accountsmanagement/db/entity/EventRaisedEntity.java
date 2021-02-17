package com.smec.cc.accountsmanagement.db.entity;

import javax.persistence.*;
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

    public String getDateTime() {
        return new Date(timestamp).toString();
    }

    public String getType() {
        return type;
    }
}
