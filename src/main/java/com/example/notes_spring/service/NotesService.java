package com.example.notes_spring.service;

import com.example.notes_spring.component.AuthUtil;
import com.example.notes_spring.dto.CreateNoteRequest;
import com.example.notes_spring.dto.NotesResponse;
import com.example.notes_spring.exception.NoteNotFoundException;
import com.example.notes_spring.model.Note;
import com.example.notes_spring.repository.NotesRepository;
import jakarta.transaction.Transactional;
import org.aspectj.weaver.ast.Not;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotesService {

    private final NotesRepository notesRepository;
    private final AuthUtil authUtil;

    @Autowired
    public NotesService(NotesRepository notesRepository,  AuthUtil authUtil) {
        this.notesRepository = notesRepository;
        this.authUtil = authUtil;
    }


    @Transactional
    public NotesResponse createNote(CreateNoteRequest  createNoteRequest) {
        Long userId = authUtil.getCurrentUserId();
        Note note = new Note(createNoteRequest.getTitle(), createNoteRequest.getBody(), userId);
        Note savedNote =  notesRepository.save(note);

        return new NotesResponse(savedNote.getId(), savedNote.getTitle(), savedNote.getBody());
    }


    public NotesResponse getNote(Long id) {
        Long ownerId = authUtil.getCurrentUserId();
        System.out.println("User ID = " + authUtil.getCurrentUserId());
        Note note = notesRepository.findByOwnerIdAndId(ownerId, id).orElseThrow(() -> new NoteNotFoundException(id));
        return new NotesResponse(note.getId(), note.getTitle(), note.getBody());
    }

    public Page<NotesResponse> getAllNotes(int page, int size) {
        Long ownerId = authUtil.getCurrentUserId();
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Note> notes = notesRepository.findByOwnerId(ownerId,pageable);
        return notes.map(this::noteToResponse);
    }

    @Transactional
    public NotesResponse updateNote(Long id, CreateNoteRequest createNoteRequest) {
        Long ownerId = authUtil.getCurrentUserId();
        Note note = notesRepository.findByOwnerIdAndId(ownerId,id).orElseThrow(() -> new NoteNotFoundException(id));

        note.setTitle(createNoteRequest.getTitle());
        note.setBody(createNoteRequest.getBody());

        Note savedNote = notesRepository.save(note);
        return new NotesResponse(savedNote.getId(), savedNote.getTitle(), savedNote.getBody());
    }

    @Transactional
    public void deleteNote(Long id) {
        Long ownerId = authUtil.getCurrentUserId();
        Note note = notesRepository.findByOwnerIdAndId(ownerId,id).orElseThrow(() -> new NoteNotFoundException(id));
        notesRepository.delete(note);
    }

    private NotesResponse noteToResponse(Note note) {
        return new NotesResponse(note.getId(), note.getTitle(), note.getBody());
    }


}
