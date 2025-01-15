package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import connectdb.DataBaseConnectionManager;
import connectdb.SqlExecutor;
import model.Customer;
import model.Order;

public class CustomerDAO {
    DataBaseConnectionManager connectionManager;

    public CustomerDAO() {
        connectionManager = new DataBaseConnectionManager();
        try (Connection connect = connectionManager.connect()) {
            SqlExecutor sqlExecutor = new SqlExecutor();
            sqlExecutor.executeScriptFromResources(connect, "init.sql");
        } catch (SQLException e) {
            throw new RuntimeException("Database error: ", e);
        }
    }

    public CustomerDAO(DataBaseConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
        try (Connection connect = connectionManager.connect()) {
            SqlExecutor sqlExecutor = new SqlExecutor();
            sqlExecutor.executeScriptFromResources(connect, "init.sql");
        } catch (SQLException e) {
            throw new RuntimeException("Database error: ", e);
        }
    }

    public List<Customer> getAllCustomer() throws SQLException {
        List<Customer> result = new ArrayList<>();
        String query = """
                select customers.id, customers.name, customers.email,
                orders.id as order_id, orders.description, orders.amount 
                from customers 
                left join orders on customers.id=orders.customer_id
                order by customers.id
                """;
        try (Connection connect = connectionManager.connect(); Statement statement = connect.createStatement()) {
            ResultSet rs = statement.executeQuery(query);
            Customer customer = null;
            List<Order> orders = new ArrayList<>();
            while (rs.next()) {
                Integer customerID = rs.getInt("id");
                if (customer == null || !customer.getId().equals(customerID)) {
                    if (customer != null) {
                        customer.setOrders(orders);
                        result.add(customer);
                    }
                    customer = new Customer(customerID, rs.getString("name"), rs.getString("email"), new ArrayList<>());
                    orders.clear();
                }
                Integer orderID = rs.getInt("order_id");
                if (!rs.wasNull()) {
                    orders.add(new Order(rs.getInt("order_id"), rs.getString("description"), rs.getDouble("amount"), customer));
                }
            }
            if (customer != null) {
                customer.setOrders(orders);
                result.add(customer);
            }
        }
        return result;
    }

    public Customer getByIdCustomer(Integer id) throws SQLException {
        Customer customer = null;
        String query = """
                select customers.id, customers.name, customers.email,
                orders.id as order_id, orders.description, orders.amount 
                from customers 
                left join orders on customers.id=orders.customer_id
                where customers.id=(?)
                """;
        try (Connection connect = connectionManager.connect(); PreparedStatement statement = connect.prepareStatement(query)) {
            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();
            List<Order> orders = new ArrayList<>();
            while (rs.next()) {
                if (customer == null) {
                    customer = new Customer(rs.getInt("id"), rs.getString("name"), rs.getString("email"), new ArrayList<>());
                }
                Integer orderID = rs.getInt("order_id");
                if (!rs.wasNull()) {
                    orders.add(new Order(rs.getInt("order_id"), rs.getString("description"), rs.getDouble("amount"), customer));
                }
            }
            if (customer != null) {
                customer.setOrders(orders);
            }
        }
        return customer;
    }

    public Integer deleteCustomer(Integer id) throws SQLException {
        Integer deletedRows = 0;
        String query = "delete from customers where id=?";
        try (Connection connect = connectionManager.connect(); PreparedStatement statement = connect.prepareStatement(query)) {
            statement.setInt(1, id);
            deletedRows = statement.executeUpdate();
            return deletedRows;
        }
    }


    public Integer updateCustomer(Integer id, Customer in) throws SQLException, IllegalStateException {
        Integer changedRows = 0;
        String query = "update customers set name=?, email=? where id=?";
        try (Connection connect = connectionManager.connect(); PreparedStatement statement = connect.prepareStatement(query)) {
            statement.setString(1, in.getName());
            statement.setString(2, in.getEmail());
            statement.setInt(3, id);
            changedRows = statement.executeUpdate();
            return changedRows;
        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) {
                throw new IllegalStateException("A user with this email already exists");
            }
            throw e;
        }
    }

    public Integer addCustomer(Customer in) throws SQLException, IllegalStateException {
        String query = "insert into customers (name, email) values (?,?)";
        try (Connection connect = connectionManager.connect(); PreparedStatement statement = connect.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, in.getName());
            statement.setString(2, in.getEmail());
            statement.executeUpdate();
            ResultSet rs = statement.getGeneratedKeys();
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) {
                throw new IllegalStateException("A user with this email already exists");
            }
            throw e;
        }
    }
}
