package com.sight.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "store", indexes = {
        @Index(name = "idx_fk_address_id", columnList = "address_id")
}, uniqueConstraints = {
        @UniqueConstraint(name = "idx_unique_manager", columnNames = {"manager_staff_id"})
})
public class Store {
    @Id
    @Column(name = "store_id")
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "manager_staff_id", nullable = false)
    private Staff managerStaff;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "last_update", nullable = false)
    private Instant lastUpdate;

}