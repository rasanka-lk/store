package com.example.store.mapper;

import com.example.store.dto.ProductDTO;
import com.example.store.dto.ProductOrderDTO;
import com.example.store.dto.ProductRequest;
import com.example.store.entity.Product;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(
            target = "orderIds",
            expression =
                    "java(product.getOrders() == null ? java.util.List.of() : product.getOrders().stream().map(com.example.store.entity.Order::getId).toList())")
    ProductDTO productToProductDTO(Product product);

    List<ProductDTO> productsToProductDTOs(List<Product> products);

    ProductOrderDTO productToProductOrderDTO(Product product);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orders", ignore = true)
    Product productRequestToProduct(ProductRequest request);
}
