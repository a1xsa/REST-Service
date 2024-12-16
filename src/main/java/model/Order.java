package model;

public class Order {
    private Integer id;
    private String description;
    private Double amount;
    private Customer customer;

    public Order(Integer id, String description, Double amount, Customer customer) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.customer = customer;
    }

    public Integer getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public Double getAmount() {
        return amount;
    }

    public Customer getCustomer() {
        return customer;
    }

}
