package ru.filini.cloudstorage.DTO.requests;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Data;

@Data
public class EditFileNameRequest {

    private String filename;

    @JsonCreator
    public EditFileNameRequest(String filename) {
        this.filename = filename;
    }
}
