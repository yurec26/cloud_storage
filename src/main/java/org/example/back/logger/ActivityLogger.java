package org.example.back.logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ActivityLogger {
    public enum LogType {
        LOGIN,
        LOGOUT,
        ERROR
    }

    private final static String USER_LOGIN = "Пользователь вошёл в сервис : ";
    private final static String ERROR = "Ошибка : ";
    private final static String USER_LOGOUT = "Пользователь вышел из сети : ";
    private final static String DOWNLOAD = "Пользователь скачал файл :";
    private final static String UPLOAD = "Пользователь разместил файл :";
    private final static String DELETE = "Пользователь удалил файл :";
    private final static String LIST = "Пользователь запросил содержимое хранилища";
    private final static String RENAME = "Пользователь переименовал файл :";
    private final static String LOG_PATH = "src/main/resources/log.txt";
    private final static String LOG_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static void loginLogout(String login, LogType logType) {
        StringBuilder logBuilder = new StringBuilder();
        if (logType.equals(LogType.LOGIN)) {
            logBuilder.append(USER_LOGIN);
        } else if (logType.equals(LogType.ERROR)) {
            logBuilder.append(ERROR);
        } else if (logType.equals(LogType.LOGOUT)) {
            logBuilder.append(USER_LOGOUT);
        }
        logBuilder.append(login);
        log(logBuilder.toString());
    }

    public static void getList() {
        log(LIST);
    }


    public static void postFile(String name) {
        log(UPLOAD + name);
    }

    public static void getFile(String name) {
        log(DOWNLOAD + name);
    }

    public static void deleteFile(String name) {
        log(DELETE + name);
    }

    public static void renameFile(String oldname, String newname) {
        log(RENAME + "старое имя : " + oldname + ", новое имя :" + newname);
    }


    public static void log(String msg) {
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(LOG_DATE_PATTERN);
        String timestamp = localDateTime.format(formatter);
        //
        String logMessage = String.format("[%s] %s%n", timestamp, msg);
        //
        Path path = Path.of(LOG_PATH);
        //
        try {
            // Проверяем, существует ли файл, и создаем его, если нет
            if (!Files.exists(path)) {
                Files.createFile(path);
            }
            // Записываем сообщение в файл в режиме добавления
            Files.write(path, logMessage.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException exception) {
            System.out.println("wrong path");
        }
    }
}
