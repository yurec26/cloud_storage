package org.example.back.controller;

import org.example.back.constants.ResponseMessage;
import org.example.back.entity.File;
import org.example.back.entity.LoginRequest;
import org.example.back.entity.Token;
import org.example.back.service.BackService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class BackController {
    private final BackService backService;

    public BackController(BackService backService) {
        this.backService = backService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        return backService.login(loginRequest);
    }

    // ! по факту это logout
    @GetMapping("/login")
    public ResponseEntity<Object> login(@RequestParam(value = "logout", required = false) String logout,
                                        @RequestHeader("auth-token") Token authToken) {
        backService.logout(authToken);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/list")
    public ResponseEntity<?> getList(@RequestParam int limit,
                                     @RequestHeader("auth-token") Token authToken) {
        if (backService.verificate(authToken)) {
            return backService.getList(limit);
        } else {
            return new ResponseEntity<>(ResponseMessage.UNAUTHORIZED, HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/file")
    public ResponseEntity<?> postFile(@RequestParam String filename,
                                      @RequestHeader("auth-token") Token authToken,
                                      @RequestParam("file") MultipartFile file) {
        if (backService.verificate(authToken)) {
            return backService.postFile(file, filename);
        } else {
            return new ResponseEntity<>(ResponseMessage.UNAUTHORIZED, HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/file")
    public ResponseEntity<?> getFile(@RequestParam String filename,
                                     @RequestHeader("auth-token") Token authToken) {
        if (backService.verificate(authToken)) {
            return backService.getFile(filename);
        }
        return new ResponseEntity<>(ResponseMessage.UNAUTHORIZED, HttpStatus.UNAUTHORIZED);
    }

    @DeleteMapping("/file")
    public ResponseEntity<?> deleteFile(@RequestParam String filename,
                                        @RequestHeader("auth-token") Token authToken) {
        if (backService.verificate(authToken)) {
            return (backService.deleteFile(filename));
        } else {
            return new ResponseEntity<>(ResponseMessage.UNAUTHORIZED, HttpStatus.UNAUTHORIZED);
        }
    }

    @PutMapping("/file")
    public ResponseEntity<?> renameFile(@RequestParam String filename,
                                        @RequestHeader("auth-token") Token authToken,
                                        @RequestBody File file) {
        if (backService.verificate(authToken)) {
            return backService.renameFile(filename, file.getFilename());
        } else {
            return new ResponseEntity<>(ResponseMessage.UNAUTHORIZED, HttpStatus.UNAUTHORIZED);
        }
    }
}