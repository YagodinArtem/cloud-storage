package ru;

import lombok.extern.slf4j.Slf4j;
import ru.engine.database.DatabaseWorker;
import ru.engine.server.Server;

import java.sql.SQLException;


@Slf4j
public class Main {

    private static DatabaseWorker db;

    public static void main(String[] args) {

        db = new DatabaseWorker();

        //Настройки реакции callback
        new Server(
                (fileMessage -> {
            if (db.saveFile(fileMessage)) {
                log.debug(fileMessage.getName() + " stored in database");
            }}),

                (ctx, s) -> {
            try {
                if (s.contains("/registration")) {
                    db.registration(ctx, s);
                } else if (s.contains("/refresh")) {
                    db.refresh(ctx, s);
                } else if (s.contains("/login")) {
                    db.login(ctx, s);
                } else if (s.contains("/download")) {
                    db.download(ctx, s);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        },

        (dfm) -> {
            try {
                db.deleteFile(dfm);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });

    }
}
