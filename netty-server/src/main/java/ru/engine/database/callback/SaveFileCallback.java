package ru.engine.database.callback;

import model.FileMessage;

public interface SaveFileCallback {

    void call(FileMessage fileMessage);
}
