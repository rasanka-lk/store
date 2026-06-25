package com.example.store.service;

import com.example.store.dto.CursorPageResponse;
import com.example.store.dto.ProductDTO;
import com.example.store.dto.ProductRequest;
import com.example.store.entity.Product;
import com.example.store.exception.NotFoundException;
import com.example.store.mapper.ProductMapper;
import com.example.store.repository.ProductRepository;
import com.example.store.util.CursorPaginationUtil;

import lombok.RequiredArgsConstructor;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Transactional(readOnly = true)
    public CursorPageResponse<ProductDTO> fetchProducts(Long cursor, int size) {
        Pageable pageable = PageRequest.of(0, size);
        List<Product> items = productRepository.fetchNextPage(cursor, pageable);

        return CursorPaginationUtil.buildResponse(items, size, Product::getId, productMapper::productsToProductDTOs);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "products", key = "#id")
    public ProductDTO fetchProductById(Long id) {
        return productRepository
                .findById(id)
                .map(productMapper::productToProductDTO)
                .orElseThrow(() -> new NotFoundException("Product not found"));
    }

    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public ProductDTO create(ProductRequest request) {
        Product product = productMapper.productRequestToProduct(request);
        return productMapper.productToProductDTO(productRepository.save(product));
    }
}
