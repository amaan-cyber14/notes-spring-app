package com.example.notes_spring.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "notes")
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id = 0L;

    @Column(unique = true, nullable = false)
    Long ownerId;

    String title;

    @Column(columnDefinition = "TEXT")
    String body;

    public Note(String title, String body, Long ownerId) {
        this.title = title;
        this.body = body;
        this.ownerId = ownerId;
    }
}
