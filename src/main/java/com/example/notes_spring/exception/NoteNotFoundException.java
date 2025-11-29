package com.example.notes_spring.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class NoteNotFoundException extends RuntimeException {

    Long id;


    @Override
    public String getMessage() {
        return "Note not found with id " + id;
    }
}
