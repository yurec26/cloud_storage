package org.example.back.service;

import org.example.back.constants.ResponseMessage;
import org.example.back.entity.*;
import org.example.back.repository.FileRepository;
import org.example.back.repository.UserRepository;
import org.example.back.token.TokenGenerator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.example.back.constants.ResponseMessage.*;


@Service
public class BackService {

    private final UserRepository userRepository;
    private final FileRepository fileRepository;


    public BackService(UserRepository userRepository, FileRepository fileRepository) {
        this.userRepository = userRepository;
        this.fileRepository = fileRepository;
    }

    public ResponseEntity<?> login(LoginRequest loginRequest) {
        Optional<User> optionalUser = userRepository
                .findByLoginAndPassword(loginRequest.getLogin(), loginRequest.getPassword());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setAuthToken(new Token(TokenGenerator.generateToken()));
            userRepository.save(user);
            return ResponseEntity.ok(user.getAuthToken());
        } else {
            return new ResponseEntity<>(new ConfirmOperationDTO(BAD_CREDENTIALS), HttpStatus.BAD_REQUEST);
        }
    }

    public void logout(Token tokenDirty) {
        tokenDirty.setAuthToken(tokenDirty.getAuthToken()
                .replace("Bearer ", ""));
        Optional<User> optUser = userRepository.findByAuthToken(tokenDirty);
        if (optUser.isPresent()) {
            User user = optUser.get();
            user.setAuthToken(null);
            userRepository.save(user);
        }
    }

    public ResponseEntity<?> getList(int limit) {
        try {
            List<File> fileList = fileRepository.listTopNFiles(limit);
            return new ResponseEntity<>(fileList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ConfirmOperationDTO(ERROR_GETTING_LIST), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> postFile(MultipartFile file, String filename) {
        try {
            File fileEntity = new File();
            fileEntity.setSize(file.getSize());
            fileEntity.setFilename(filename);
            fileEntity.setContent(file.getBytes());
            fileRepository.save(fileEntity);
        } catch (Exception e) {
            return new ResponseEntity<>(new ConfirmOperationDTO(BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new ConfirmOperationDTO(OK), HttpStatus.OK);
    }

    public ResponseEntity<?> getFile(String filename) {
        try {
            Optional<File> optionalFile = fileRepository.findFileByFilename(filename);
            if (optionalFile.isPresent()) {
                return ResponseEntity.ok(optionalFile.get().getContent());
            } else {
                return new ResponseEntity<>(new ConfirmOperationDTO(BAD_REQUEST), HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(new ConfirmOperationDTO(ERROR_UPLOAD), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> deleteFile(String filename) {
        Optional<File> optionalFile = fileRepository.findFileByFilename(filename);
        if (optionalFile.isPresent()) {
            File file = optionalFile.get();
            try {
                fileRepository.delete(file);
                return new ResponseEntity<>(ResponseMessage.OK, HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>(new ConfirmOperationDTO(ERROR_DELETE), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>(new ConfirmOperationDTO(BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> renameFile(String oldName, String newName) {
        Optional<File> optionalFile = fileRepository.findFileByFilename(oldName);
        if (optionalFile.isPresent()) {
            File file = optionalFile.get();
            file.setFilename(newName);
            try {
                fileRepository.save(file);
                return new ResponseEntity<>(ResponseMessage.OK, HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>(new ConfirmOperationDTO(ERROR_UPLOAD), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>(new ConfirmOperationDTO(BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

    public boolean verificate(Token tokenDirty) {
        tokenDirty.setAuthToken(tokenDirty.getAuthToken()
                .replace("Bearer ", ""));
        Optional<User> optionalUser = userRepository.findByAuthToken(tokenDirty);
        return optionalUser.isPresent();
    }
}