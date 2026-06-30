package com.fhcs.clothing_store.infrastructure.in.rest.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.fhcs.clothing_store.core.domain.bo.product.CategoryBO;
import com.fhcs.clothing_store.core.domain.bo.product.CollectionBO;
import com.fhcs.clothing_store.core.domain.bo.product.ProductBO;
import com.fhcs.clothing_store.core.domain.bo.product.variation.ProductVariationBO;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.response.product.CategoryResponse;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.response.product.CollectionResponse;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.response.product.ProductResponse;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.response.product.variation.ProductVariationDto;
import com.fhcs.clothing_store.infrastructure.out.persistence.entity.product.Category;
import com.fhcs.clothing_store.infrastructure.out.persistence.entity.product.Collection;

@Component
public class ProductDtoMapper {

    public ProductVariationDto toProductVariationDto(ProductVariationBO v) {
        return ProductVariationDto.builder()
                .variationId(v.getVariationId())
                .productId(v.getProduct().getProductId())
                .productName(v.getProduct().getName())
                .productDescription(v.getProduct().getDescription())
                .price(v.getProduct().getPrice())
                .score(v.getProduct().getScore())
                .categoryName(v.getProduct().getCategory().getCategoryName())
                .collectionName(v.getProduct().getCollection().getCollectionName())
                .color(v.getColor().getColor())
                .size(v.getSize().getSize())
                .skuCode(v.getSkuCode())
                .stock(v.getStock())
                .inStock(v.getStock() > 0)
                .build();
    }

    public List<ProductVariationDto> toProductVariationDtoList(List<ProductVariationBO> bos) {
        return bos.stream().map(this::toProductVariationDto).toList();
    }

    public CategoryResponse toCategoryResponse(List<CategoryBO> bos, String message) {
        List<Category> cats = bos.stream().map(bo -> {
            Category c = new Category();
            c.setCategoryId(bo.getCategoryId());
            c.setCategoryName(bo.getCategoryName());
            return c;
        }).toList();
        return CategoryResponse.builder().success(true).message(message).categories(cats).build();
    }

    public CollectionResponse toCollectionResponse(List<CollectionBO> bos, String message) {
        List<Collection> colls = bos.stream().map(bo -> {
            Collection c = new Collection();
            c.setCollectionId(bo.getCollectionId());
            c.setCollectionName(bo.getCollectionName());
            c.setDescription(bo.getDescription());
            c.setLaunchDate(bo.getLaunchDate());
            return c;
        }).toList();
        return CollectionResponse.builder().success(true).message(message).collections(colls).build();
    }

    public ProductResponse toProductResponse(ProductBO bo, String message) {
        return ProductResponse.success(bo, message);
    }
}
