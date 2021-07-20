package ru.engine.database;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import model.FileMessage;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


@Slf4j
public class DatabaseWorker {

    private Connection connection;

    private final String url = "jdbc:mysql://localhost:3306/";
    private final String username = "root";
    private final String password = "985632";

    public DatabaseWorker() {
        initConnection();
    }

    public boolean saveFile(FileMessage fm) {
        try (FileInputStream fis = new FileInputStream(fm.getFile())) {
            PreparedStatement saveFile = connection.prepareStatement("INSERT INTO `storage`.`files` (`file_name`, `content`, `file_owner`) VALUES (?, ?, ?);");
            saveFile.setString(1, fm.getName());
            saveFile.setBinaryStream(2, fis);
            saveFile.setInt(3, Integer.parseInt(fm.getFileOwner()));
            saveFile.executeUpdate();
            log.debug("save file");
            return true;
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            log.debug("unable to save file");
            return false;
        }
    }

    public void registration(ChannelHandlerContext ctx, String s) throws SQLException {
        String[] prop = s.split(" ");
        PreparedStatement findUser = connection.prepareStatement("select * from `storage`.`users` where username = ? && password = ?;");
        findUser.setString(1, prop[1]);
        findUser.setString(2, prop[2]);
        ResultSet rs = findUser.executeQuery();

        if (rs.next()) {
            if (rs.getString(2).equals(prop[1]) && rs.getString(3).equals(prop[2])) {
                ctx.writeAndFlush(new String[]{"login", rs.getString(2), rs.getString(3), String.valueOf(rs.getInt(1))});
            }
        }else {
            PreparedStatement registration = connection.prepareStatement("INSERT INTO `storage`.`users` (`username`, `password`) VALUES (?, ?);");
            registration.setString(1, prop[1]);
            registration.setString(2, prop[2]);
            registration.executeUpdate();
            ctx.writeAndFlush(new String[]{"registration", prop[1], prop[2], String.valueOf(getUserId(prop))});
        }
    }

    public void initConnection() {
        try {
            connection = DriverManager.getConnection(url, username, password);
            log.debug("Connection succeed");
        } catch (SQLException e) {
            log.debug("Unable to establish connection to DB");
        }
    }

    public void closeConnection() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void refresh(ChannelHandlerContext ctx, String s) throws SQLException {
        String[] prop = s.split(" ");
        PreparedStatement findFiles = connection.prepareStatement("select * from `storage`.`files` where file_owner=?");
        findFiles.setInt(1, Integer.parseInt(prop[1]));
        ResultSet rs = findFiles.executeQuery();
        ArrayList<String> fileNames = new ArrayList<>();

        while (rs.next()) {
            fileNames.add(rs.getString(2));
        }

        String[] list= new String[fileNames.size()];
        ctx.writeAndFlush(fileNames.toArray(list));
    }

    private int getUserId(String[] prop) throws SQLException {
        PreparedStatement findId = connection.prepareStatement("select id_user from `storage`.`users` where username=? && password=?");
        findId.setString(1,prop[1]);
        findId.setString(2,prop[2]);
        ResultSet rs = findId.executeQuery();
        if (rs.next()) {
            return rs.getInt(1);
        } else {
            return 0;
        }
    }
}
