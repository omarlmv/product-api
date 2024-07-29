
package com.example.productapi.service.impl;

import com.example.productapi.entity.Product;
import com.example.productapi.enums.ProductStatus;
import com.example.productapi.exception.ProductNotFoundException;
import com.example.productapi.mapper.ProductMapper;
import com.example.productapi.model.ProductRequest;
import com.example.productapi.model.ProductResponse;
import com.example.productapi.repository.ProductRepository;
import com.example.productapi.service.ProductService;
import com.example.productapi.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final StatisticsService statisticsService;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper, StatisticsService statisticsService) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.statisticsService = statisticsService;
    }

    @Override
    public Mono<ProductResponse> createProduct(ProductRequest productRequest) {
        Product product = productMapper.toEntity(productRequest);
        product.setStatus(ProductStatus.ACTIVE.getValue());
        return productRepository.save(product)
                .flatMap(savedProduct -> {
                    return statisticsService.createStatistics(
                            savedProduct.getCreatedAt().toString(),
                            1,
                            0,
                            0,
                             savedProduct.getCategoryId().toString())
                            .thenReturn(productMapper.toResponse(savedProduct));
                });
    }

    @Override
    public Mono<ProductResponse> updateProduct(Integer id, ProductRequest productRequest) {
        return productRepository.findById(id)
                .switchIfEmpty(Mono.error(new ProductNotFoundException(id)))
                .flatMap(existingProduct -> {
                    productMapper.updateProductFromDto(productRequest, existingProduct);
                    return productRepository.save(existingProduct)
                            .flatMap(updatedProduct -> {
                                return statisticsService.createStatistics(
                                        updatedProduct.getCreatedAt().toString(),
                                        0,
                                        1,
                                        0,
                                        updatedProduct.getCategoryId().toString())
                                        .thenReturn(productMapper.toResponse(updatedProduct));
                            });
                });
    }

    @Override
    public Mono<Void> deleteProduct(Integer id) {
        return productRepository.findById(id)
                .switchIfEmpty(Mono.error(new ProductNotFoundException(id)))
                .flatMap(existingProduct -> {
                    existingProduct.setStatus(ProductStatus.DELETED.getValue());
                    return productRepository.save(existingProduct)
                            .flatMap(deletedProduct -> {
                                return statisticsService.createStatistics(
                                        deletedProduct.getCreatedAt().toString(),
                                        0,
                                        0,
                                        1,
                                        deletedProduct.getCategoryId().toString())
                                        .then();
                            });
                })
                .then();
    }

    @Override
    public Flux<ProductResponse> getAllProducts() {
        return productRepository.findAllByStatusNot(ProductStatus.DELETED.getValue())
                .map(productMapper::toResponse);
    }

    @Override
    public Mono<ProductResponse> getProductById(Integer id) {
        return productRepository.findByIdAndStatus(id, ProductStatus.ACTIVE.getValue())
                .switchIfEmpty(Mono.error(new ProductNotFoundException(id)))
                .map(productMapper::toResponse);
    }
}
