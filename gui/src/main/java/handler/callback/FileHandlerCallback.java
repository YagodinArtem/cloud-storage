package handler.callback;

import model.FileMessage;

import java.io.IOException;

public interface FileHandlerCallback {

    void call(FileMessage fm) throws IOException;
}
