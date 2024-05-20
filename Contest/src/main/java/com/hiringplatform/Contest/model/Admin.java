package com.hiringplatform.Contest.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Entity @Data @Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Admin {
    @Id
    private String email;
    private String password;
    private String role;

    public void setRole(String role) {
        this.role = "ADMIN";
    }
}
