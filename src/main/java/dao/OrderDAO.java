package dao;

import connectdb.DataBaseConnectionManager;
import connectdb.SqlExecutor;
import model.Customer;
import model.Order;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {
    DataBaseConnectionManager connectionManager;

    public OrderDAO() {
        connectionManager = new DataBaseConnectionManager();
        try (Connection connect = connectionManager.connect()) {
            SqlExecutor sqlExecutor = new SqlExecutor();
            sqlExecutor.executeScriptFromResources(connect, "init.sql");
        } catch (SQLException e) {
            throw new RuntimeException("Database error: ", e);
        }
    }

    public OrderDAO(DataBaseConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
        try (Connection connect = connectionManager.connect()) {
            SqlExecutor sqlExecutor = new SqlExecutor();
            sqlExecutor.executeScriptFromResources(connect, "init.sql");
        } catch (SQLException e) {
            throw new RuntimeException("Database error: ", e);
        }
    }

    private Customer getCustomerById(Integer id) throws SQLException {
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

    public List<Order> getAllOrders() throws SQLException {
        List<Order> result = new ArrayList<>();
        String query = """
                select *
                from orders 
                order by customer_id
                """;
        try (Connection connect = connectionManager.connect(); Statement statement = connect.createStatement()) {
            ResultSet rs = statement.executeQuery(query);
            Customer customer = null;
            while (rs.next()) {
                Integer customerID = rs.getInt("customer_id");
                if (customer == null || !customer.getId().equals(customerID)) {
                    customer = getCustomerById(customerID);
                }
                result.add(new Order(rs.getInt("id"), rs.getString("description"), rs.getDouble("amount"), customer));
            }
        }
        return result;
    }

    public Order getByIdOrders(Integer id) throws SQLException {
        Order order = null;
        String query = """
                select *
                from orders 
                where id=?
                """;
        try (Connection connect = connectionManager.connect(); PreparedStatement statement = connect.prepareStatement(query)) {
            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                Integer customerID = rs.getInt("customer_id");
                Customer customer = getCustomerById(customerID);
                order = new Order(rs.getInt("id"), rs.getString("description"), rs.getDouble("amount"), customer);
            }
        }
        return order;
    }

    public Integer addOrder(Order order, Integer id) throws SQLException, IllegalStateException {
        Customer customer = getCustomerById(id);
        String query = "insert into orders (customer_id, description, amount) values (?,?,?)";
        if (customer == null) {
            throw new IllegalStateException("There is no user with this id " + id);
        }
        try (Connection connect = connectionManager.connect(); PreparedStatement statement = connect.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, id);
            statement.setString(2, order.getDescription());
            statement.setDouble(3, order.getAmount());
            statement.executeUpdate();
            ResultSet rs = statement.getGeneratedKeys();
            rs.next();
            return rs.getInt(1);
        }
    }

    public Integer deleteOrder(Integer id) throws SQLException {
        Integer deletedRows = 0;
        String query = "delete from orders where id=?";
        try (Connection connect = connectionManager.connect(); PreparedStatement statement = connect.prepareStatement(query)) {
            statement.setInt(1, id);
            deletedRows = statement.executeUpdate();
            return deletedRows;
        }
    }

    public Integer updateOrder(Integer index, Order in) throws SQLException {
        Integer changedRows = 0;
        String query = "update orders set description=?, amount=? where id=?";
        try (Connection connect = connectionManager.connect(); PreparedStatement statement = connect.prepareStatement(query)) {
            statement.setString(1, in.getDescription());
            statement.setDouble(2, in.getAmount());
            statement.setInt(3, index);
            changedRows = statement.executeUpdate();
            return changedRows;
        }
    }
}
