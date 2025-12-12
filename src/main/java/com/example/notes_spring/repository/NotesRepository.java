package com.example.notes_spring.repository;

import com.example.notes_spring.model.Note;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotesRepository extends JpaRepository<Note, Long> {
    Page <Note> findByOwnerId(Long ownerId, Pageable pageable);
    Optional<Note> findNoteById(Long ownerId, Long id);
}
