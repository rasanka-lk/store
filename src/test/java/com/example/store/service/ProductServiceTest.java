package com.example.store.service;

import com.example.store.dto.CursorPageResponse;
import com.example.store.dto.ProductDTO;
import com.example.store.dto.ProductRequest;
import com.example.store.entity.Product;
import com.example.store.exception.EntityNotFoundException;
import com.example.store.mapper.ProductMapper;
import com.example.store.repository.ProductRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    @Test
    void createSavesProductWithDescriptionOnly() {
        ProductRequest request = new ProductRequest();
        request.setDescription("Coffee");

        Product productToSave = new Product();
        productToSave.setDescription("Coffee");

        Product savedProduct = new Product();
        savedProduct.setId(1L);
        savedProduct.setDescription("Coffee");

        ProductDTO response = new ProductDTO();
        response.setId(1L);
        response.setDescription("Coffee");
        response.setOrderIds(List.of());

        when(productMapper.productRequestToProduct(request)).thenReturn(productToSave);
        when(productRepository.save(productToSave)).thenReturn(savedProduct);
        when(productMapper.productToProductDTO(savedProduct)).thenReturn(response);

        ProductDTO result = productService.create(request);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getDescription()).isEqualTo("Coffee");
        assertThat(result.getOrderIds()).isEmpty();
        verify(productRepository).save(productToSave);
    }

    @Test
    void fetchProductByIdReturnsProductWhenFound() {
        Product product = new Product();
        product.setId(1L);
        product.setDescription("Coffee");

        ProductDTO response = new ProductDTO();
        response.setId(1L);
        response.setDescription("Coffee");

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productMapper.productToProductDTO(product)).thenReturn(response);

        ProductDTO result = productService.fetchProductById(1L);

        assertThat(result.getDescription()).isEqualTo("Coffee");
    }

    @Test
    void fetchProductByIdThrowsWhenMissing() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.fetchProductById(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Product not found - 99");
    }

    @Test
    void fetchProductsReturnsCursorPage() {
        Product first = new Product();
        first.setId(1L);
        Product second = new Product();
        second.setId(2L);

        ProductDTO firstDto = new ProductDTO();
        firstDto.setId(1L);
        ProductDTO secondDto = new ProductDTO();
        secondDto.setId(2L);

        when(productRepository.fetchNextPage(any(), any())).thenReturn(List.of(first, second));
        when(productMapper.productsToProductDTOs(List.of(first, second))).thenReturn(List.of(firstDto, secondDto));

        CursorPageResponse<ProductDTO> result = productService.fetchProducts(null, 2);

        assertThat(result.data()).hasSize(2);
        assertThat(result.hasNext()).isTrue();
        assertThat(result.nextCursor()).isEqualTo(2L);
    }
}
