package ServiceTest;


import DAO.CustomerDAO;

import DTO.CustomerDTO;
import exception.DatabaseException;
import exception.DuplicateDataException;
import exception.NotFoundException;
import model.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import service.CustomerService;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {
    @Mock
    private CustomerDAO customerDao;

    @InjectMocks
    private CustomerService customerService;

    CustomerDTO customerDTO;
    Customer customer;

    @BeforeEach
    void setUp() {
        customerDTO = new CustomerDTO(1, "Tsvetkov Alexey", "tsvetkov-alexey@mail.com");
        customer = new Customer(1, "Tsvetkov Alexey", "tsvetkov-alexey@mail.com", List.of());
    }

    @Test
    void getAll_Success() throws SQLException {
        when(customerDao.getAllCustomer()).thenReturn(List.of(customer));

        List<CustomerDTO> result=customerService.getAll();

        verify(customerDao).getAllCustomer();
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals("Tsvetkov Alexey", result.get(0).getName());
    }

    @Test
    void getAll_NotFoundException() throws SQLException {
        when(customerDao.getAllCustomer()).thenReturn(List.of());

        assertThrows(NotFoundException.class, ()->customerService.getAll());
        verify(customerDao).getAllCustomer();
    }

    @Test
    void getAll_DatabaseException() throws SQLException {
        when(customerDao.getAllCustomer()).thenThrow(new SQLException());

        assertThrows(DatabaseException.class, ()->customerService.getAll());
        verify(customerDao).getAllCustomer();
    }

    @Test
    void addCustomer_Success() throws SQLException {
        when(customerDao.addCustomer(any(Customer.class))).thenReturn(1);

        Integer index = customerService.add(customerDTO);

        verify(customerDao).addCustomer(any(Customer.class));
        assertEquals(1, index);
    }

    @Test
    void addCustomer_DatabaseException() throws SQLException {
        when(customerDao.addCustomer(any(Customer.class))).thenThrow(new SQLException());

        assertThrows(DatabaseException.class, () -> customerService.add(customerDTO));
        verify(customerDao).addCustomer(any(Customer.class));
    }

    @Test
    void addCustomer_DuplicateDataException() throws SQLException {
        when(customerDao.addCustomer(any(Customer.class))).thenThrow(new IllegalStateException());

        assertThrows(DuplicateDataException.class, () -> customerService.add(customerDTO));
        verify(customerDao).addCustomer(any(Customer.class));
    }

    @Test
    void getById_Success() throws SQLException {
        when(customerDao.getByIdCustomer(anyInt())).thenReturn(customer);

        List<CustomerDTO> result = customerService.getById(1);

        verify(customerDao).getByIdCustomer(1);
        assertEquals(1, result.size());
        assertEquals("Tsvetkov Alexey", result.get(0).getName());
    }

    @Test
    void getById_NotFoundException() throws SQLException {
        when(customerDao.getByIdCustomer(anyInt())).thenReturn(null);

        assertThrows(NotFoundException.class, () -> customerService.getById(1));
        verify(customerDao).getByIdCustomer(1);
    }

    @Test
    void getById_DatabaseException() throws SQLException {
        when(customerDao.getByIdCustomer(anyInt())).thenThrow(new SQLException());

        assertThrows(DatabaseException.class, ()->customerService.getById(1));
        verify(customerDao).getByIdCustomer(1);
    }

    @Test
    void deleteById_Success() throws SQLException {
        when(customerDao.deleteCustomer(anyInt())).thenReturn(1);

        Integer result = customerService.deleteById(1);

        verify(customerDao).deleteCustomer(1);
        assertEquals(1, result);
    }

    @Test
    void deleteById_NotFoundException() throws SQLException {
        when(customerDao.deleteCustomer(anyInt())).thenReturn(0);

        assertThrows(NotFoundException.class, () -> customerService.deleteById(1));
        verify(customerDao).deleteCustomer(1);
    }

    @Test
    void deleteById_DatabaseException() throws SQLException {
        when(customerDao.deleteCustomer(anyInt())).thenThrow(new SQLException());

        assertThrows(DatabaseException.class, ()->customerService.deleteById(1));
        verify(customerDao).deleteCustomer(1);
    }

    @Test
    void updateCustomer_Success() throws SQLException {
        when(customerDao.updateCustomer(anyInt(), any(Customer.class))).thenReturn(1);

        Integer result = customerService.updateCustomer(1, customerDTO);

        assertEquals(1, result);
        verify(customerDao).updateCustomer(anyInt(), any(Customer.class));
    }

    @Test
    void updateCustomer_NotFoundException() throws SQLException {
        when(customerDao.updateCustomer(anyInt(), any(Customer.class))).thenReturn(0);

        assertThrows(NotFoundException.class, () -> customerService.updateCustomer(1, customerDTO));
        verify(customerDao).updateCustomer(anyInt(), any(Customer.class));
    }

    @Test
    void updateCustomer_DatabaseException() throws SQLException {
        when(customerDao.updateCustomer(anyInt(), any(Customer.class))).thenThrow(new SQLException());

        assertThrows(DatabaseException.class, () -> customerService.updateCustomer(1, customerDTO));
        verify(customerDao).updateCustomer(anyInt(), any(Customer.class));
    }

    @Test
    void updateCustomer_DuplicateDataException() throws SQLException {
        when(customerDao.updateCustomer(anyInt(), any(Customer.class))).thenThrow(new IllegalStateException());

        assertThrows(DuplicateDataException.class, () -> customerService.updateCustomer(1, customerDTO));
        verify(customerDao).updateCustomer(anyInt(),any(Customer.class));
    }



























}
