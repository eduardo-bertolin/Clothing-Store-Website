package com.fhcs.clothing_store.application.service.admin;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.fhcs.clothing_store.application.port.in.service.admin.AdminProductServicePort;
import com.fhcs.clothing_store.application.port.out.persistence.ImageRepositoryPort;
import com.fhcs.clothing_store.application.port.out.persistence.product.ProductRepositoryPort;
import com.fhcs.clothing_store.core.domain.bo.image.ProductImageBO;
import com.fhcs.clothing_store.core.domain.bo.product.CategoryBO;
import com.fhcs.clothing_store.core.domain.bo.product.CollectionBO;
import com.fhcs.clothing_store.core.domain.bo.product.ProductBO;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.ProductPatchDto;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.request.product.ProductRequest;

@Service
public class AdminProductUseCase implements AdminProductServicePort {

    private final ProductRepositoryPort productRepository;
    private final ImageRepositoryPort imageRepository;
    private final AdminCollectionUseCase collectionUseCase;
    private final AdminCategoryUseCase categoryUseCase;

    public AdminProductUseCase(ProductRepositoryPort productRepository,
            ImageRepositoryPort imageRepository,
            AdminCollectionUseCase collectionUseCase, AdminCategoryUseCase categoryUseCase) {
        this.productRepository = productRepository;
        this.imageRepository = imageRepository;
        this.collectionUseCase = collectionUseCase;
        this.categoryUseCase = categoryUseCase;
    }

    @Override
    public ProductBO createProduct(ProductRequest request) {
        CollectionBO collection = collectionUseCase.getCollectionById(request.getCollectionId());
        CategoryBO category = categoryUseCase.getCategoryById(request.getCategoryId());

        ProductBO product = new ProductBO();
        product.setCollection(collection);
        product.setCategory(category);
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setScore(request.getScore());

        return productRepository.save(product);
    }

    @Override
    public ProductBO getProductById(Integer productId) {
        ProductBO product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado."));
        product.setImages(imageRepository.findByProductId(productId));
        return product;
    }

    @Override
    public Page<ProductBO> getAllProducts(Pageable pageable) {
        Page<ProductBO> products = productRepository.findAll(pageable);

        List<Integer> productIds = products.stream()
                .map(ProductBO::getProductId)
                .toList();

        Map<Integer, List<ProductImageBO>> imagesByProductId = imageRepository
                .findByProductIds(productIds)
                .stream()
                .collect(Collectors.groupingBy(img -> img.getProduct().getProductId()));

        products.forEach(product ->
                product.setImages(imagesByProductId.getOrDefault(product.getProductId(), List.of())));

        return products;
    }

    @Override
    public ProductBO updateProduct(Integer productId, ProductPatchDto patch) {
        ProductBO product = getProductById(productId);

        if (patch.getName() != null) product.setName(patch.getName());
        if (patch.getDescription() != null) product.setDescription(patch.getDescription());
        if (patch.getPrice() != null) product.setPrice(patch.getPrice());
        if (patch.getScore() != null) product.setScore(patch.getScore());
        if (patch.getCategoryId() != null) {
            product.setCategory(categoryUseCase.getCategoryById(patch.getCategoryId()));
        }
        if (patch.getCollectionId() != null) {
            product.setCollection(collectionUseCase.getCollectionById(patch.getCollectionId()));
        }

        return productRepository.save(product);
    }

    @Override
    public void deleteProduct(Integer productId) {
        ProductBO product = getProductById(productId);
        productRepository.delete(product);
    }
}
