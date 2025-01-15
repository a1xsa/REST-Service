package dto;

public class OrderDTO {
    private Integer id;
    private String description;
    private Double amount;
    private String customerName;

    public OrderDTO(Integer id, String description, Double amount, String customerName) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.customerName = customerName;
    }

    public OrderDTO(){

    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
