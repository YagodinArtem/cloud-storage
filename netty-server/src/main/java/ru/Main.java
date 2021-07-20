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
        new Server((fileMessage -> {
            if (db.saveFile(fileMessage)) {
                log.debug(fileMessage.getName() + " stored in database");
            }
        }), (ctx, s) -> {
            try {
                if (s.contains("registration")) {
                    db.registration(ctx, s);
                } else if (s.contains("/refresh")) {
                    db.refresh(ctx, s);
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });

    }
}
