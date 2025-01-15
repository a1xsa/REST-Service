package dao;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.util.List;

import dao.CustomerDAO;
import dao.OrderDAO;
import connectdb.DataBaseConnectionManager;
import model.Customer;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrderDAOTest {
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:16-alpine"
    );

    OrderDAO orderDAO;
    CustomerDAO customerDao;

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @BeforeEach
    void setUp() {
        DataBaseConnectionManager connectionProvider = new DataBaseConnectionManager(
                postgres.getJdbcUrl(),
                postgres.getUsername(),
                postgres.getPassword()
        );
        orderDAO = new OrderDAO(connectionProvider);
        customerDao=new CustomerDAO(connectionProvider);
    }

    @Test
    @Order(1)
    void addOrder() throws SQLException {
        Customer customer = new Customer(null, "Tsvetkov Alexey", "tsvetkov-alexey@mail.ru", List.of());
        int idCustomer=customerDao.addCustomer(customer);
        model.Order order = new model.Order(null, "New Test Order", 150.0, customer);
        Integer orderId = orderDAO.addOrder(order, idCustomer);
        assertNotNull(orderId);
    }

    @Test
    @Order(2)
    void getAllOrders() throws SQLException {
        List<model.Order> orders = orderDAO.getAllOrders();
        assertNotNull(orders);
        assertFalse(orders.isEmpty());
        assertEquals("New Test Order", orders.get(0).getDescription());
    }

    @Test
    @Order(3)
    void getByIdOrder() throws SQLException {
        model.Order order = orderDAO.getByIdOrders(1);
        assertNotNull(order);
        assertEquals("New Test Order", order.getDescription());
    }

    @Test
    @Order(4)
    void updateOrder() throws SQLException {
        model.Order updatedOrder = new model.Order(null, "Updated Order", 200.0, null);
        int rowsUpdated = orderDAO.updateOrder(1, updatedOrder);
        assertEquals(1, rowsUpdated);
        model.Order retrievedOrder = orderDAO.getByIdOrders(1);
        assertEquals("Updated Order", retrievedOrder.getDescription());
    }

    @Test
    @Order(5)
    void deleteOrder() throws SQLException {
        int rowsDeleted = orderDAO.deleteOrder(1);
        assertEquals(1, rowsDeleted);
        model.Order deletedOrder = orderDAO.getByIdOrders(1);
        assertNull(deletedOrder);
    }

}
