package service;


import dao.OrderDAO;
import dto.OrderDTO;
import exception.DatabaseException;
import exception.NotFoundException;
import model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import service.OrderService;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
    @Mock
    private OrderDAO orderDAO;

    @InjectMocks
    private OrderService orderService;

    OrderDTO orderDTO;
    Order order;

    @BeforeEach
    void setUp() {
        orderDTO = new OrderDTO(1, "New Order", 12.3 ,"Alexey");
        order = new Order(1, "New Order", 12.3 , null);
    }

    @Test
    void getAll_Success() throws SQLException {
        when(orderDAO.getAllOrders()).thenReturn(List.of(order));

        List<OrderDTO> result=orderService.getAll();

        verify(orderDAO).getAllOrders();
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals("New Order", result.get(0).getDescription());
    }

    @Test
    void getAll_NotFoundException() throws SQLException {
        when(orderDAO.getAllOrders()).thenReturn(List.of());

        assertThrows(NotFoundException.class, ()->orderService.getAll());
        verify(orderDAO).getAllOrders();
    }

    @Test
    void getAll_DatabaseException() throws SQLException {
        when(orderDAO.getAllOrders()).thenThrow(new SQLException());

        assertThrows(DatabaseException.class, ()->orderService.getAll());
        verify(orderDAO).getAllOrders();
    }

    @Test
    void addOrder_Success() throws SQLException {
        when(orderDAO.addOrder(any(Order.class), anyInt())).thenReturn(1);

        Integer index = orderService.add(orderDTO, 1);

        verify(orderDAO).addOrder(any(Order.class), eq(1));
        assertEquals(1, index);
    }

    @Test
    void addOrder_DatabaseException() throws SQLException {
        when(orderDAO.addOrder(any(Order.class), anyInt())).thenThrow(new SQLException());

        assertThrows(DatabaseException.class, () -> orderService.add(orderDTO, 1));
        verify(orderDAO).addOrder(any(Order.class), eq(1));
    }

    @Test
    void addOrder_NotFoundException() throws SQLException {
        when(orderDAO.addOrder(any(Order.class), anyInt())).thenThrow(new IllegalStateException());

        assertThrows(NotFoundException.class, () -> orderService.add(orderDTO, 1));
        verify(orderDAO).addOrder(any(Order.class), eq(1));
    }

    @Test
    void getById_Success() throws SQLException {
        when(orderDAO.getByIdOrders(anyInt())).thenReturn(order);

        List<OrderDTO> result = orderService.getById(1);

        verify(orderDAO).getByIdOrders(1);
        assertEquals(1, result.size());
        assertEquals("New Order", result.get(0).getDescription());
    }

    @Test
    void getById_NotFoundException() throws SQLException {
        when(orderDAO.getByIdOrders(anyInt())).thenReturn(null);

        assertThrows(NotFoundException.class, () -> orderService.getById(1));
        verify(orderDAO).getByIdOrders(1);
    }

    @Test
    void getById_DatabaseException() throws SQLException {
        when(orderDAO.getByIdOrders(anyInt())).thenThrow(new SQLException());

        assertThrows(DatabaseException.class, ()->orderService.getById(1));
        verify(orderDAO).getByIdOrders(1);
    }

    @Test
    void deleteById_Success() throws SQLException {
        when(orderDAO.deleteOrder(anyInt())).thenReturn(1);

        Integer result = orderService.deleteById(1);

        verify(orderDAO).deleteOrder(1);
        assertEquals(1, result);
    }

    @Test
    void deleteById_NotFoundException() throws SQLException {
        when(orderDAO.deleteOrder(anyInt())).thenReturn(0);

        assertThrows(NotFoundException.class, () -> orderService.deleteById(1));
        verify(orderDAO).deleteOrder(1);
    }

    @Test
    void deleteById_DatabaseException() throws SQLException {
        when(orderDAO.deleteOrder(anyInt())).thenThrow(new SQLException());

        assertThrows(DatabaseException.class, () -> orderService.deleteById(1));
        verify(orderDAO).deleteOrder(1);
    }

    @Test
    void updateCustomer_Success() throws SQLException {
        when(orderDAO.updateOrder(anyInt(), any(Order.class))).thenReturn(1);

        Integer result = orderService.updateOrder(1, orderDTO);

        assertEquals(1, result);
        verify(orderDAO).updateOrder(eq(1), any(Order.class));
    }

    @Test
    void testUpdateCustomer_NotFoundException() throws SQLException {
        when(orderDAO.updateOrder(anyInt(), any(Order.class))).thenReturn(0);

        assertThrows(NotFoundException.class, () -> orderService.updateOrder(1, orderDTO));
        verify(orderDAO).updateOrder(eq(1), any(Order.class));
    }

    @Test
    void testUpdateCustomer_DatabaseException() throws SQLException {
        when(orderDAO.updateOrder(anyInt(), any(Order.class))).thenThrow(new SQLException());

        assertThrows(DatabaseException.class, () -> orderService.updateOrder(1, orderDTO));
        verify(orderDAO).updateOrder(eq(1), any(Order.class));
    }

}
