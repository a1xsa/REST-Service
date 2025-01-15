package connectdb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseConnectionManager {
    private String url;
    private String username;
    private String password;

    public DataBaseConnectionManager() {
        ConfigLoader configLoader = new ConfigLoader();
        this.url = configLoader.getProperty("db.url");
        this.username = configLoader.getProperty("db.username");
        this.password = configLoader.getProperty("db.password");
    }

    public DataBaseConnectionManager(String url, String username, String password){
        this.url=url;
        this.username=username;
        this.password=password;
    }

    public Connection connect() throws SQLException {
         return DriverManager.getConnection(url, username, password);
    }

}
