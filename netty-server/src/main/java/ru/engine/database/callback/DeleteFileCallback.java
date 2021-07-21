package ru.engine.database.callback;

import model.DeleteFileMessage;

public interface DeleteFileCallback {

    void call(DeleteFileMessage dfm);
}
