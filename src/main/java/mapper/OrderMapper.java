package mapper;

import dto.OrderDTO;
import model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface OrderMapper {
    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    @Mapping(source = "customer.name", target = "customerName")
    OrderDTO toOrderDTO(Order order);

    List<OrderDTO> toOrderDTOList(List<Order> orders);

    Order toOrder(OrderDTO orderDTO);

    List<Order> toCustomerList(List<OrderDTO> orders);
}
