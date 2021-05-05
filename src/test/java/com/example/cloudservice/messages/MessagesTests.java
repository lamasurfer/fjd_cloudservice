package com.example.cloudservice.messages;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.support.MessageSourceAccessor;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class MessagesTests {

    @Autowired
    private MessageSourceAccessor messages;

    @Test
    void test_messages() {
        assertEquals("Не удалось сохранить файл, некорректный файл", messages.getMessage("request.upload.file.is.missing"));
        assertEquals("Http метод не поддерживается", messages.getMessage("request.invalid.method"));
        assertEquals("Некорректный запрос", messages.getMessage("request.invalid.message"));
        assertEquals("Некорректные параметры запроса", messages.getMessage("request.invalid.parameter"));
        assertEquals("Некорректный формат данных", messages.getMessage("request.invalid.data.format"));
        assertEquals("Проблемы с загрузкой файла", messages.getMessage("file.upload.problems"));
        assertEquals("Проблемы с загрузкой файла, такой файл уже есть", messages.getMessage("file.upload.file.exists"));
        assertEquals("Не удалось скачать файл, файл не найден", messages.getMessage("file.download.problems"));
        assertEquals("Не удалось удалить файл, файл не найден", messages.getMessage("file.delete.problems"));
        assertEquals("Не удалось переименовать файл, файл не найден", messages.getMessage("file.rename.problems"));
        assertEquals("Пользователь не найден", messages.getMessage("user.not.found"));
        assertEquals("Пользователь с таким логином и паролем не найден", messages.getMessage("bad.credentials"));
        assertEquals("Токен доступа недействителен, отозван или поврежден", messages.getMessage("wrong.auth.token"));

        assertEquals("Не удалось сохранить файл! Имя файла не указано", messages.getMessage("upload.file.filename.is.blank"));
        assertEquals("Не удалось загрузить файл! Имя файла не указано", messages.getMessage("download.file.filename.is.blank"));
        assertEquals("Не удалось удалить файл! Имя файла не указано", messages.getMessage("delete.file.filename.is.blank"));
        assertEquals("Не удалось переименовать файл! Имя файла не указано", messages.getMessage("rename.file.filename.is.blank"));
        assertEquals("Не удалось переименовать файл! Новое имя файла не указано", messages.getMessage("rename.file.new.filename.is.blank"));
        assertEquals("Логин должен быть заполнен", messages.getMessage("login.request.login.is.blank"));
        assertEquals("Пароль должен быть заполнен", messages.getMessage("login.request.password.is.blank"));
        assertEquals("Некорректный объект", messages.getMessage("object.is.invalid"));
        assertEquals("Что-то не так...", messages.getMessage("default.message"));
    }
}
