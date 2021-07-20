package ru.engine.database;

import model.FileMessage;

public interface SaveFileCallback {

    void call(FileMessage fileMessage);
}
