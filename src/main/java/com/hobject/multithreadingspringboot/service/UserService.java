package com.hobject.multithreadingspringboot.service;

import com.hobject.multithreadingspringboot.ResponseDto;
import com.hobject.multithreadingspringboot.UserRepository;
import com.hobject.multithreadingspringboot.entity.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class UserService {
    private static Logger log = LogManager.getLogger(UserService.class);
    @Autowired
    private UserRepository userRepository;
    public ResponseDto persistDataResponse(MultipartFile[] files) {
        List<User> allUsers = new ArrayList<>(files.length);
        ResponseDto response = null;

        try {
            for (MultipartFile file : files) {
                CompletableFuture<List<User>> users = persistData(file);
                if (!users.get().isEmpty()) {
                    allUsers.addAll(users.get());
                }
            }
            if (!allUsers.isEmpty()) {
                response = new ResponseDto();
                response.setResult(allUsers);
                response.setMessage("(+" + allUsers.size() + ") " + "new records.");
                response.setStatus(HttpStatus.CREATED);
            } else {
                response = new ResponseDto();
                response.setMessage("No new records found. Please check file you are trying to persist.");
                response.setStatus(HttpStatus.NOT_MODIFIED);
            }
        } catch (Exception e) {
            response = new ResponseDto();
            response.setError(e.getMessage());

            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);

        }
        return response;
    }
    @Async
    public CompletableFuture<List<User>> persistData(MultipartFile file) {
        List<User> userList = new ArrayList<>();
        if (checkIfValidFile(file)) {
            log.info("Trying to save records into repo using thread: {}", Thread.currentThread().getName());
            userList = parseCsvFile(file);
            if (!CollectionUtils.isEmpty(userList)) {
                try {
                    long startTime = System.currentTimeMillis();
                    userRepository.saveAll(userList);
                    long endTime = System.currentTimeMillis();
                    log.info("Successfully finished parsing file and mapping records({}): time took: {} Millis ", userList.size(), endTime - startTime);
                } catch (Exception e) {
                    log.info("Successfully finished parsing file and mapping records({}): time took: {} Millis ");
                }
            }
            return CompletableFuture.completedFuture(userList);

        }
        return CompletableFuture.completedFuture(userList);
    }

    @Async
    public CompletableFuture<List<User>> findAll() {
        List<User> listUser = null;
        try {
            listUser = userRepository.findAll();
            log.info("Fetching {} records using thread thread {} ", listUser.size(), Thread.currentThread().getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CompletableFuture.completedFuture(listUser);
    }

    private boolean validatingIfRecordExists(String email) {
        boolean recordExists = false;
        if (!email.isEmpty() || email != null) {
            recordExists = userRepository.existsByEmail(email);
        } else {
            log.info("Saving records failed. Records already exist. Please add file with new records with distinct email address.");
        }
        return recordExists;
    }
    private List<User> parseCsvFile(MultipartFile file) {
        List<User> users = null;
        try {
            users = new ArrayList<>();
            if (!file.isEmpty() && file != null) {
                log.info("Started parsing file: " + file.getOriginalFilename());
                InputStreamReader read = new InputStreamReader(file.getInputStream());
                BufferedReader reader = new BufferedReader(read);
                String[] data;
                while (reader.ready()) {
                    User user = new User();
                    data = reader.readLine().split(",");
                    if (data[0].startsWith("u")) {
                        continue;
                    }
                    /**
                     * Validating if the records already exist for particular user based on email id;
                     */
                    if (validatingIfRecordExists(data[2])) {
                        continue;
                    } else {
                        user.setUserName(data[1]);
                        user.setEmail(data[2]);
                    }
                    users.add(user);
                }
            }
        } catch (Exception e) {
            log.info("{} :: Exception occurred while parsing file: {}", e.getMessage(), file.getOriginalFilename());
        }

        return users;
    }

    private boolean checkIfValidFile(MultipartFile file) {
        String fileExension = "CSV";
        if (file.getOriginalFilename().endsWith(fileExension.toLowerCase()) || file.getOriginalFilename().endsWith(fileExension)) {
            log.info("File {} accepted type {}. Please make sure if the file is in csv format: ", file.getOriginalFilename(), file.getContentType());

            return true;
        }
        log.info("File {} not supported. Please make sure if the file is in csv format: ", file.getOriginalFilename(), file.getContentType());
        return false;
    }
}
