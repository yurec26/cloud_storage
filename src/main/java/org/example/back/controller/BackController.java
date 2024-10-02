package org.example.back.controller;

import org.example.back.constants.ResponseMessage;
import org.example.back.entity.ConfirmOperationDTO;
import org.example.back.entity.File;
import org.example.back.entity.LoginRequest;
import org.example.back.entity.Token;
import org.example.back.logger.ActivityLogger;
import org.example.back.service.BackService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static org.example.back.constants.ResponseMessage.*;

@RestController
public class BackController {
    private final BackService backService;

    public BackController(BackService backService) {
        this.backService = backService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Token token = backService.login(loginRequest);
        if (token != null) {
            ActivityLogger.loginLogout(loginRequest.getLogin(), ActivityLogger.LogType.LOGIN);
            return ResponseEntity.ok(token);
        } else {
            ActivityLogger.loginLogout(loginRequest.getLogin(), ActivityLogger.LogType.ERROR);
            return new ResponseEntity<>(new ConfirmOperationDTO(BAD_CREDENTIALS), HttpStatus.BAD_REQUEST);
        }
    }

    // ! по факту это logout
    @GetMapping("/login")
    public ResponseEntity<Object> login(@RequestParam(value = "logout", required = false) String logout,
                                        @RequestHeader("auth-token") Token authToken) {
        String username = backService.logout(authToken);
        ActivityLogger.loginLogout(username, ActivityLogger.LogType.LOGOUT);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/list")
    public ResponseEntity<?> getList(@RequestParam int limit,
                                     @RequestHeader("auth-token") Token authToken) {
        if (backService.verificate(authToken)) {
            List<File> fileList = backService.getList(limit);
            ActivityLogger.getList();
            return new ResponseEntity<>(fileList, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(ResponseMessage.UNAUTHORIZED, HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/file")
    public ResponseEntity<?> postFile(@RequestParam String filename,
                                      @RequestHeader("auth-token") Token authToken,
                                      @RequestParam("file") MultipartFile file) {
        if (backService.verificate(authToken)) {
            try {
                backService.postFile(file, filename);
                ActivityLogger.postFile(filename);
                return new ResponseEntity<>(new ConfirmOperationDTO(OK), HttpStatus.OK);
            } catch (IOException e) {
                return new ResponseEntity<>(new ConfirmOperationDTO(BAD_REQUEST), HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>(ResponseMessage.UNAUTHORIZED, HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/file")
    public ResponseEntity<?> getFile(@RequestParam String filename,
                                     @RequestHeader("auth-token") Token authToken) {
        if (backService.verificate(authToken)) {
            byte[] bytes = backService.getFile(filename);
            if (bytes.length != 0) {
                ActivityLogger.getFile(filename);
                return ResponseEntity.ok(bytes);
            } else {
                return new ResponseEntity<>(new ConfirmOperationDTO(BAD_REQUEST), HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>(ResponseMessage.UNAUTHORIZED, HttpStatus.UNAUTHORIZED);
    }

    @DeleteMapping("/file")
    public ResponseEntity<?> deleteFile(@RequestParam String filename,
                                        @RequestHeader("auth-token") Token authToken) {
        if (backService.verificate(authToken)) {
            try {
                backService.deleteFile(filename);
                ActivityLogger.deleteFile(filename);
                return new ResponseEntity<>(new ConfirmOperationDTO(OK), HttpStatus.OK);
            } catch (RuntimeException e) {
                return new ResponseEntity<>(new ConfirmOperationDTO(BAD_REQUEST), HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>(ResponseMessage.UNAUTHORIZED, HttpStatus.UNAUTHORIZED);
        }
    }

    @PutMapping("/file")
    public ResponseEntity<?> renameFile(@RequestParam String filename,
                                        @RequestHeader("auth-token") Token authToken,
                                        @RequestBody File file) {
        if (backService.verificate(authToken)) {
            try {
                backService.renameFile(filename, file.getFilename());
                ActivityLogger.renameFile(filename, file.getFilename());
                return new ResponseEntity<>(new ConfirmOperationDTO(OK), HttpStatus.OK);
            } catch (RuntimeException e) {
                return new ResponseEntity<>(new ConfirmOperationDTO(BAD_REQUEST), HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>(ResponseMessage.UNAUTHORIZED, HttpStatus.UNAUTHORIZED);
        }
    }
}