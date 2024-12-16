package mapper;

import DTO.CustomerDTO;
import model.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface CustomerMapper {
    CustomerMapper INSTANCE = Mappers.getMapper(CustomerMapper.class);

    CustomerDTO toCustomerDTO(Customer customer);

    List<CustomerDTO> toCustomerDTOList(List<Customer> customers);

//    @Mapping(target = "orders", ignore = true)  // Если заказы не используются
    Customer toCustomer(CustomerDTO dto);

    List<Customer> toCustomerList(List<CustomerDTO> customers);
}
