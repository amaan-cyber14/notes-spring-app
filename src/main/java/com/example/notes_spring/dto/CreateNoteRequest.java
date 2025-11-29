package com.example.notes_spring.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateNoteRequest {
    @NotBlank(message = "Title cannot be empty")
    @Size(message = "Title cannot exceed 100 chars")
    String title;

    @NotBlank(message = "Body cannot be empty")
    String body;
}

