package com.hobject.multithreadingspringboot.controller;

import com.hobject.multithreadingspringboot.ResponseDto;
import com.hobject.multithreadingspringboot.entity.User;
import com.hobject.multithreadingspringboot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/")
public class UserController {
    @Autowired
    private UserService service;
//    @PostMapping("/copy")
//    public ResponseEntity saveData(@RequestParam MultipartFile[] files) {
//        CompletableFuture<List<User>> users = null;
//        Map<String, String> responseMessage;
//        ResponseEntity responseEntity;
//        int records= 0;
//
//
//           for (MultipartFile file: files){
//               users =  service.persistData(file);
//       }
//        try {
//            records = users.get().size();
//        } catch (Exception e){
//            e.printStackTrace();
//        }
//            if (records>0){
//                responseEntity = new ResponseEntity("+"+records+" Users pushed into DB.",HttpStatus.CREATED);
//            }
//            else {
//                responseEntity = new ResponseEntity("No new records pushed into db", HttpStatus.NOT_ACCEPTABLE);
//            }
//
//        return responseEntity;
//    }

    @PostMapping("/save")
    public ResponseDto saveData(@RequestParam MultipartFile[] files) {
        return service.persistDataResponse(files);
    }

    @GetMapping("/get")
    public ResponseEntity getAllData(){
        ResponseEntity entity ;
      try {
        entity=  new ResponseEntity(!service.findAll().get().isEmpty()?service.findAll().get():"No records found.", HttpStatus.OK);
      }catch (Exception e){
         entity= new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
      }
      return entity;
    }
    @GetMapping("/get3")
    public ResponseEntity threeThread() throws ExecutionException, InterruptedException {

        CompletableFuture<List<User>>  entity=  service.findAll();
        CompletableFuture<List<User>> entity1=  service.findAll();
        CompletableFuture<List<User>> entity2=  service.findAll();
        CompletableFuture.allOf(entity, entity1,entity2).join();
         return new ResponseEntity(Arrays.asList(entity.get(),entity1.get(), entity2.get()),HttpStatus.OK);
    }

}
