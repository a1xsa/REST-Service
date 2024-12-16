package service;

import DAO.CustomerDAO;
import DTO.CustomerDTO;

import mapper.CustomerMapper;
import exception.DatabaseException;
import exception.DuplicateDataException;
import exception.NotFoundException;
import model.Customer;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CustomerService {
    private CustomerDAO repo;

    public CustomerService() {
        repo = new CustomerDAO();
    }

    public CustomerService(CustomerDAO customerDao) {
        this.repo = customerDao;
    }

    private void validateCustomerDTO(CustomerDTO customerDTO) {
        if (customerDTO == null || customerDTO.getName() == null || customerDTO.getEmail() == null) {
            throw new IllegalArgumentException("Customer data is incomplete.");
        }
    }

    public List<CustomerDTO> getAll() throws NotFoundException, DatabaseException {
        try {
            List<Customer> customers = repo.getAllCustomer();
            if (customers.isEmpty()) {
                throw new NotFoundException("No customer found");
            }
            return CustomerMapper.INSTANCE.toCustomerDTOList(customers);
        } catch (SQLException e) {
            throw new DatabaseException("Failed with DB", e);
        }
    }

    public Integer add(CustomerDTO in) throws DatabaseException, DuplicateDataException, IllegalArgumentException {
        validateCustomerDTO(in);
        Customer customer = CustomerMapper.INSTANCE.toCustomer(in);
        try {
            return repo.addCustomer(customer);
        } catch (SQLException e) {
            throw new DatabaseException("Failed with DB", e);
        } catch (IllegalStateException e) {
            throw new DuplicateDataException(e.getMessage());
        }
    }

    public List<CustomerDTO> getById(Integer id) throws NotFoundException, DatabaseException {
        List<CustomerDTO> result = new ArrayList<>();
        try {
            Customer customer = repo.getByIdCustomer(id);
            if (customer == null) {
                throw new NotFoundException("No customer found with id " + id);
            }
            result.add(CustomerMapper.INSTANCE.toCustomerDTO(customer));
            return result;
        } catch (SQLException e) {
            throw new DatabaseException("Failed with DB", e);
        }
    }

    public Integer deleteById(Integer id) throws NotFoundException, DatabaseException {
        try {
            Integer deletedRows=repo.deleteCustomer(id);
            if(deletedRows==0){
                throw new NotFoundException("No customer found with id " + id);
            }
            return id;
        } catch (SQLException e) {
            throw new DatabaseException("Failed with DB", e);
        }
    }

    public Integer updateCustomer(Integer id, CustomerDTO in) throws NotFoundException, DatabaseException, IllegalArgumentException {
        validateCustomerDTO(in);
        Customer customer = CustomerMapper.INSTANCE.toCustomer(in);
        try {
            Integer changedRows=repo.updateCustomer(id, customer);
            if(changedRows==0){
                throw new NotFoundException("No customer found with id " + id);
            }
            return id;
        } catch (SQLException e) {
            throw new DatabaseException("Failed with DB", e);
        } catch (IllegalStateException e) {
            throw new DuplicateDataException(e.getMessage());
        }
    }


}
