package dao;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.util.List;

import dao.CustomerDAO;
import connectdb.DataBaseConnectionManager;
import model.Customer;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CustomerDAOTest {
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:16-alpine"
    );

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
        customerDao = new CustomerDAO(connectionProvider);
    }

    @Test
    @Order(1)
    void addCustomer_Success() throws SQLException {
        Customer customer = new Customer(null, "Tsvetkov Alexey", "tsvetkov-alexey@mail.com", List.of());
        assertEquals(1, customerDao.addCustomer(customer));
    }

    @Test
    @Order(2)
    void addCustomer_IllegalStateException() throws SQLException {
        Customer customer =new Customer(null, "Ivan Petrov", "tsvetkov-alexey@mail.com", List.of());
        assertThrows(IllegalStateException.class, ()->customerDao.addCustomer(customer));
    }

    @Test
    @Order(3)
    void getCustomer() throws SQLException {
        List<Customer> customers = null;
        customers = customerDao.getAllCustomer();
        assertFalse(customers.isEmpty());
        assertEquals("Tsvetkov Alexey", customers.get(0).getName());
    }

    @Test
    @Order(4)
    void getByIdCustomer() throws SQLException {
        Customer customer = customerDao.getByIdCustomer(1);
        assertNotNull(customer);
        assertEquals("Tsvetkov Alexey", customer.getName());
    }
    @Test
    @Order(5)
    void updateCustomer() throws SQLException {
        Customer updatedCustomer = new Customer(null, "Tsvetkov Roman", "tsvetkov-roman@mail.com", List.of());
        int rowsUpdated = customerDao.updateCustomer(1, updatedCustomer);
        assertEquals(1, rowsUpdated);
        Customer retrievedCustomer = customerDao.getByIdCustomer(1);
        assertEquals("Tsvetkov Roman", retrievedCustomer.getName());
    }

    @Test
    @Order(6)
    void deleteCustomer() throws SQLException {
        int rowsDeleted = customerDao.deleteCustomer(1);
        assertEquals(1, rowsDeleted);
        Customer deletedCustomer = customerDao.getByIdCustomer(1);
        assertNull(deletedCustomer);
    }

}
