package com.hobject.multithreadingspringboot.service;

import com.hobject.multithreadingspringboot.UserRepository;
import com.hobject.multithreadingspringboot.entity.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class UserService {
private static Logger log = LogManager.getLogger(UserService.class);
    @Autowired
    private UserRepository userRepository;
@Async
    public CompletableFuture<List<User>> persistData(MultipartFile file) throws IOException {
        List<User> users = new ArrayList<>();
                if (!file.isEmpty() && file!=null){
                    log.info("Started parsing file: "+file.getOriginalFilename());
            InputStreamReader read = new InputStreamReader(file.getInputStream());
            BufferedReader reader = new BufferedReader(read);
                    String[] data;
                    int id=1;
                long startTime=  System.currentTimeMillis();
                    log.info("Mapping file data into user object: ");
                    while (reader.ready()){
                User user = new User();
                data = reader.readLine().split(",");
                if (data[0].startsWith("u")){
                    continue;
                }
                if (userRepository.existsById(Integer.valueOf(data[0]))){
                    user.setUserId((userRepository.findAll().size())+id);
                }
                else {
                    user.setUserId(Integer.valueOf(data[0]));
                }
                    user.setUserName(data[1]);
                    user.setEmail(data[2]);
                    users.add(user);
                    id++;
            }

                    log.info("Saving records into repo: {}", Thread.currentThread().getName());
            userRepository.saveAll(users);
                    long endTime=  System.currentTimeMillis();
                    log.info("Successfully finished parsing file and mapping records({}): time took: {} Millis ",users.size(), endTime-startTime);

                }
        return CompletableFuture.completedFuture(users);
    }
@Async
    public CompletableFuture<List<User>> findAll() {
        List<User> listUser= null;
        try{
          listUser = userRepository.findAll();
          log.info("Thread name: "+Thread.currentThread().getName());
        }catch (Exception e){
            e.printStackTrace();
        }
        return CompletableFuture.completedFuture(listUser);
    }
}
