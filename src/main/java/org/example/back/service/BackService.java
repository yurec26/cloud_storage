package org.example.back.service;

import org.example.back.constants.ResponseMessage;
import org.example.back.entity.*;
import org.example.back.repository.FileRepository;
import org.example.back.repository.UserRepository;
import org.example.back.token.TokenGenerator;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;


@Service
public class BackService {

    private final UserRepository userRepository;
    private final FileRepository fileRepository;


    public BackService(UserRepository userRepository, FileRepository fileRepository) {
        this.userRepository = userRepository;
        this.fileRepository = fileRepository;
    }

    public Token login(LoginRequest loginRequest) {
        Optional<User> optionalUser = userRepository
                .findByLoginAndPassword(loginRequest.getLogin(), loginRequest.getPassword());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setAuthToken(new Token(TokenGenerator.generateToken()));
            userRepository.save(user);
            return user.getAuthToken();
        } else {
            return null;
        }
    }

    public String logout(Token tokenDirty) {
        tokenDirty.setAuthToken(tokenDirty.getAuthToken()
                .replace("Bearer ", ""));
        Optional<User> optUser = userRepository.findByAuthToken(tokenDirty);
        if (optUser.isPresent()) {
            User user = optUser.get();
            user.setAuthToken(null);
            userRepository.save(user);
            return user.getLogin();
        }
        return null;
    }

    public List<File> getList(int limit) {
        try {
            return fileRepository.listTopNFiles(limit);
        } catch (RuntimeException e) {
            throw new RuntimeException(ResponseMessage.ERROR_GETTING_LIST);
        }
    }

    public void postFile(MultipartFile file, String filename) throws IOException {
        File fileEntity = new File();
        fileEntity.setSize(file.getSize());
        fileEntity.setFilename(filename);
        fileEntity.setContent(file.getBytes());
        fileRepository.save(fileEntity);
    }

    public byte[] getFile(String filename) {
        try {
            Optional<File> optionalFile = fileRepository.findFileByFilename(filename);
            return optionalFile.map(File::getContent).orElse(null);
        } catch (RuntimeException e) {
            throw new RuntimeException(ResponseMessage.ERROR_UPLOAD);
        }
    }

    public void deleteFile(String filename) {
        Optional<File> optionalFile = fileRepository.findFileByFilename(filename);
        if (optionalFile.isPresent()) {
            File file = optionalFile.get();
            try {
                fileRepository.delete(file);
            } catch (RuntimeException e) {
                throw new RuntimeException(ResponseMessage.ERROR_DELETE);
            }
        } else {
            throw new RuntimeException(ResponseMessage.BAD_REQUEST);
        }
    }

    public void renameFile(String oldName, String newName) {
        Optional<File> optionalFile = fileRepository.findFileByFilename(oldName);
        if (optionalFile.isPresent()) {
            File file = optionalFile.get();
            file.setFilename(newName);
            try {
                fileRepository.save(file);
            } catch (Exception e) {
                throw new RuntimeException(ResponseMessage.ERROR_UPLOAD);
            }
        } else {
            throw new RuntimeException(ResponseMessage.BAD_REQUEST);
        }
    }

    public boolean verificate(Token tokenDirty) {
        tokenDirty.setAuthToken(tokenDirty.getAuthToken()
                .replace("Bearer ", ""));
        Optional<User> optionalUser = userRepository.findByAuthToken(tokenDirty);
        return optionalUser.isPresent();
    }
}