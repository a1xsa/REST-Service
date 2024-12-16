package model;

import com.google.gson.annotations.Expose;

import java.util.List;

public class Customer {
    private Integer id;
    private String name;
    private String email;
    private List<Order> orders;

    public Customer(Integer id, String name, String email, List<Order> orders) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.orders = orders;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Order> getOrders() {
        return List.copyOf(orders);
    }

    public void setOrders(List<Order> orders) {
        this.orders = List.copyOf(orders);
    }

    public String getEmail() {
        return email;
    }

}
