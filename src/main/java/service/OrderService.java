package service;

import dao.OrderDAO;
import dto.OrderDTO;
import mapper.OrderMapper;
import exception.DatabaseException;
import exception.NotFoundException;
import model.Order;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderService {
    private OrderDAO repo;

    public OrderService() {
        repo = new OrderDAO();
    }

    public OrderService(OrderDAO orderDAO){
        this.repo=orderDAO;
    }

    private void validateOrderDTO(OrderDTO orderDTO) throws IllegalArgumentException {
        if (orderDTO == null || orderDTO.getAmount() == null || orderDTO.getDescription() == null) {
            throw new IllegalArgumentException("Order data is incomplete.");
        }
    }

    public List<OrderDTO> getAll() throws NotFoundException, DatabaseException {
        try {
            List<Order> orders = repo.getAllOrders();
            if (orders.isEmpty()) {
                throw new NotFoundException("No orders found");
            }
            return OrderMapper.INSTANCE.toOrderDTOList(orders);
        } catch (SQLException e) {
            throw new DatabaseException("Failed with DB", e);
        }
    }

    public List<OrderDTO> getById(Integer id) throws NotFoundException, DatabaseException {
        List<OrderDTO> result = new ArrayList<>();
        try {
            Order order = repo.getByIdOrders(id);
            if (order == null) {
                throw new NotFoundException("No orders found with id " + id);
            }
            result.add(OrderMapper.INSTANCE.toOrderDTO(order));
            return result;
        } catch (SQLException e) {
            throw new DatabaseException("Failed with DB", e);
        }
    }

    public Integer add(OrderDTO in, Integer id) throws DatabaseException, NotFoundException, IllegalArgumentException {
        validateOrderDTO(in);
        Order order = OrderMapper.INSTANCE.toOrder(in);
        try {
            return repo.addOrder(order, id);
        } catch (SQLException e) {
            throw new DatabaseException("Failed with DB", e);
        } catch (IllegalStateException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    public Integer deleteById(Integer id) throws DatabaseException, NotFoundException {
        try {
            Integer deletedRows = repo.deleteOrder(id);
            if (deletedRows == 0) {
                throw new NotFoundException("No order found with id " + id);
            }
            return id;
        } catch (SQLException e) {
            throw new DatabaseException("Failed with DB", e);
        }
    }

    public Integer updateOrder(Integer id, OrderDTO in) throws NotFoundException, DatabaseException, IllegalArgumentException{
        validateOrderDTO(in);
        Order order = OrderMapper.INSTANCE.toOrder(in);
        try {
            Integer changedRows = repo.updateOrder(id, order);
            if (changedRows == 0) {
                throw new NotFoundException("No order found with id " + id);
            }
            return id;
        } catch (SQLException e) {
            throw new DatabaseException("Failed with DB", e);
        }
    }
}
