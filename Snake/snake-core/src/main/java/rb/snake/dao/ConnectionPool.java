package rb.snake.dao;

import org.apache.commons.dbcp.BasicDataSource;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

class ConnectionPool {
    private static BasicDataSource ds = new BasicDataSource();

    static{
        try {
            Properties props = new Properties();
            props.load(ConnectionPool.class.getResourceAsStream("/db.properties"));
            ds.setUrl(props.getProperty("db.url"));
            //ds.setUsername("user");
            //ds.setPassword("password");
            ds.setMinIdle(5);
            ds.setMaxIdle(10);
            ds.setMaxOpenPreparedStatements(100);
            Class.forName("org.sqlite.JDBC");
        }
        catch (IOException | ClassNotFoundException e){
            System.out.printf("Error loading database URL!");
        }
    }

    private ConnectionPool() {}

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
}
