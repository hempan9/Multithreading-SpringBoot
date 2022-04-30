package com.hobject.multithreadingspringboot.entity;

import lombok.Data;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
public class User {
    @Id
    private Integer userId;
    private String userName;
    private String email;
}
