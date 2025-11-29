package com.example.notes_spring.controller;

import com.example.notes_spring.dto.CreateNoteRequest;
import com.example.notes_spring.dto.NotesResponse;
import com.example.notes_spring.service.NotesService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/notes")
public class NotesController {

    private final NotesService notesService;

    public NotesController(NotesService notesService) {
        this.notesService = notesService;
    }

    @PostMapping
    public NotesResponse createNote(@RequestBody @Valid CreateNoteRequest createNoteRequest) {
        return notesService.createNote(createNoteRequest);
    }

    @GetMapping("/{id}")
    public NotesResponse getNote(@PathVariable Long id) {
        return notesService.getNote(id);
    }

    @PutMapping("/{id}")
    public NotesResponse updateNote(@PathVariable Long id, @RequestBody @Valid CreateNoteRequest createNoteRequest) {
        return notesService.updateNote(id, createNoteRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        notesService.deleteNote(id);
    }

    @GetMapping()
    Page<NotesResponse> getAll(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        return notesService.getAllNotes(page, size);
    }
}
