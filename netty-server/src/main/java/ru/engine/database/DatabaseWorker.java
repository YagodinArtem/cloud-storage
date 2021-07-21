package ru.engine.database;

import io.netty.channel.ChannelHandlerContext;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import model.DeleteFileMessage;
import model.FileMessage;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;


@Slf4j
public class DatabaseWorker {

    private Connection connection;

    private final String url = "jdbc:mysql://localhost:3306/";
    private final String username = "root";
    private final String password = "985632";

    private final String tempFolder = "netty-server/filesServer";

    public DatabaseWorker() {
        initConnection();
    }

    /**
     * @param fm input file message
     * @return true if file was stored in database
     */
    public boolean saveFile(FileMessage fm) {
        try {
            if (findFile(fm).next()) {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

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

    @SneakyThrows
    public boolean download(ChannelHandlerContext ctx, String s) {
        ResultSet rs = findFile(s.split(" "));
        if (rs.next()) {
            System.out.println(rs.getString("content"));
            FileMessage fm = new FileMessage();
            String filename = rs.getString("file_name");
            File temp = new File(tempFolder + "\\" + filename);
            try (InputStream is = rs.getBinaryStream("content");
                 FileOutputStream fos = new FileOutputStream(temp)) {
                int b;
                while((b = is.read()) != -1) {
                    fos.write(b);
                }
                fm.setFile(temp);
                fm.setName(filename);
                fm.setSize(temp.length());
                fm.setFileOwner(rs.getString("file_owner"));
                System.out.println(fm.getSize());
                System.out.println(fm.getFile());
                ctx.writeAndFlush(fm);
                temp.delete();
                return true;
            }
        }
        return false;
    }

    public boolean deleteFile(DeleteFileMessage dfm) throws SQLException {
        PreparedStatement deleteFile = connection.prepareStatement("delete from `storage`.`files` where file_name =?;");
        deleteFile.setString(1, dfm.getName());
        return deleteFile.executeUpdate() > 0;
    }

    private ResultSet findFile(FileMessage fm) throws SQLException {
        PreparedStatement findFile = connection.prepareStatement("select * from `storage`.`files` where file_name = ? && file_owner = ?;");
        findFile.setString(1, fm.getName());
        findFile.setInt(2, Integer.parseInt(fm.getFileOwner()));
        return findFile.executeQuery();
    }

    private ResultSet findFile(String[] prop) throws SQLException {
        PreparedStatement findFile = connection.prepareStatement("select * from `storage`.`files` where file_name = ? && file_owner = ?;");
        findFile.setString(1, prop[1]);
        findFile.setInt(2, Integer.parseInt(prop[2]));
        return findFile.executeQuery();
    }

    public boolean registration(ChannelHandlerContext ctx, String s) throws SQLException {
        String[] prop = s.split(" ");
        ResultSet rs = findUser(s);
        if (!rs.next()) {
            PreparedStatement registration = connection.prepareStatement("INSERT INTO `storage`.`users` (`username`, `password`) VALUES (?, ?);");
            registration.setString(1, prop[1]);
            registration.setString(2, prop[2]);
            registration.executeUpdate();
            ctx.writeAndFlush(new String[]{"/registration", prop[1], prop[2], String.valueOf(getUserId(prop))});
            return true;
        }
        return false;
    }

    public boolean login(ChannelHandlerContext ctx, String s) throws SQLException {
        ResultSet rs = findUser(s);
        String[] prop = s.split(" ");
        if (rs.next()) {
            if (rs.getString(2).equals(prop[1]) && rs.getString(3).equals(prop[2])) {
                ctx.writeAndFlush(new String[]{"/login", rs.getString(2), rs.getString(3), String.valueOf(rs.getInt(1))});
                return true;
            }
        }
        return false;
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
        fileNames.add("/refresh");
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

    private ResultSet findUser(String s) throws SQLException {
        String[] prop = s.split(" ");
        PreparedStatement findUser = connection.prepareStatement("select * from `storage`.`users` where username = ? && password = ?;");
        findUser.setString(1, prop[1]);
        findUser.setString(2, prop[2]);
        return findUser.executeQuery();
    }

}
