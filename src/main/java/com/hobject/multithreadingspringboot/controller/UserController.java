package com.hobject.multithreadingspringboot.controller;

import com.hobject.multithreadingspringboot.entity.User;
import com.hobject.multithreadingspringboot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/")
public class UserController {
    @Autowired
    private UserService service;
    @PostMapping("/copy")
    public ResponseEntity saveData(@RequestParam MultipartFile[] files) {
        CompletableFuture<List<User>> users = null;
        ResponseEntity responseEntity;
        try{
            for (MultipartFile file: files){
                users =  service.persistData(file);
            }
        responseEntity = new ResponseEntity(HttpStatus.CREATED);
        }catch (Exception e){
            responseEntity = new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
        }
        return responseEntity;
    }
    @GetMapping("/get")
    public ResponseEntity getAllData(){
        ResponseEntity entity ;
      try {
        entity=  new ResponseEntity(service.findAll().get(), HttpStatus.OK);
      }catch (Exception e){
         entity= new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
      }
      return entity;
    }
    @GetMapping("/get3")
    public ResponseEntity threeThread(){

          CompletableFuture<List<User>>  entity=  service.findAll();
        CompletableFuture<List<User>> entity1=  service.findAll();
        CompletableFuture<List<User>> entity2=  service.findAll();
         CompletableFuture.allOf(entity, entity1,entity2).join();
         return new ResponseEntity(HttpStatus.OK);
    }

}
