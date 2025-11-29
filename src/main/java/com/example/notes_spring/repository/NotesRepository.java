package com.example.notes_spring.repository;

import com.example.notes_spring.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotesRepository extends JpaRepository<Note, Long> { }
