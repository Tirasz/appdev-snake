package rb.snake.dao;

import java.sql.*;
import java.util.List;

abstract class DAO<T> {
    Connection DBC;

    public DAO(){
        try {
            DBC = ConnectionPool.getConnection();
            System.out.println(getClass().getName() + ": Established database connection!");
        } catch (SQLException e) {
            System.out.println(getClass().getName() + ": Failed to establish database connection!");
            e.printStackTrace();
        }
    }


    public abstract List<T> getAll();
    public abstract T save(T record);
    public abstract T delete(T record);

}
