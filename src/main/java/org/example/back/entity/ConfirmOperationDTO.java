package org.example.back.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ConfirmOperationDTO {
    private String id;
    private String message;

    public ConfirmOperationDTO(String message) {
        this.message = message;
    }
}
