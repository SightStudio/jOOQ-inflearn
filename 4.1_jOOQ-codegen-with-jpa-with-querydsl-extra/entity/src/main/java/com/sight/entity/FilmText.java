package com.sight.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "film_text", indexes = {
        @Index(name = "idx_title_description", columnList = "title, description")
})
public class FilmText {
    @Id
    @Column(name = "film_id", nullable = false)
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "title", nullable = false)
    private String title;

    @Lob
    @Column(name = "description")
    private String description;

}