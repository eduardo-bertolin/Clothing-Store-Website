package com.fhcs.clothing_store.infrastructure.out.persistence.mapper;

import org.springframework.stereotype.Component;

import com.fhcs.clothing_store.core.domain.bo.product.CategoryBO;
import com.fhcs.clothing_store.core.domain.bo.product.CollectionBO;
import com.fhcs.clothing_store.core.domain.bo.product.ProductBO;
import com.fhcs.clothing_store.core.domain.bo.product.variation.ColorBO;
import com.fhcs.clothing_store.core.domain.bo.product.variation.ProductVariationBO;
import com.fhcs.clothing_store.core.domain.bo.product.variation.SizeBO;
import com.fhcs.clothing_store.infrastructure.out.persistence.entity.product.Category;
import com.fhcs.clothing_store.infrastructure.out.persistence.entity.product.Collection;
import com.fhcs.clothing_store.infrastructure.out.persistence.entity.product.Product;
import com.fhcs.clothing_store.infrastructure.out.persistence.entity.product.variation.Color;
import com.fhcs.clothing_store.infrastructure.out.persistence.entity.product.variation.ProductVariation;
import com.fhcs.clothing_store.infrastructure.out.persistence.entity.product.variation.Size;

@Component
public class ProductEntityMapper {

    public CategoryBO categoryToBO(Category entity) {
        if (entity == null) return null;
        CategoryBO bo = new CategoryBO();
        bo.setCategoryId(entity.getCategoryId());
        bo.setCategoryName(entity.getCategoryName());
        return bo;
    }

    public Category categoryToEntity(CategoryBO bo) {
        if (bo == null) return null;
        Category entity = new Category();
        entity.setCategoryId(bo.getCategoryId());
        entity.setCategoryName(bo.getCategoryName());
        return entity;
    }

    public CollectionBO collectionToBO(Collection entity) {
        if (entity == null) return null;
        CollectionBO bo = new CollectionBO();
        bo.setCollectionId(entity.getCollectionId());
        bo.setCollectionName(entity.getCollectionName());
        bo.setDescription(entity.getDescription());
        bo.setLaunchDate(entity.getLaunchDate());
        return bo;
    }

    public Collection collectionToEntity(CollectionBO bo) {
        if (bo == null) return null;
        Collection entity = new Collection();
        entity.setCollectionId(bo.getCollectionId());
        entity.setCollectionName(bo.getCollectionName());
        entity.setDescription(bo.getDescription());
        entity.setLaunchDate(bo.getLaunchDate());
        return entity;
    }

    public ColorBO colorToBO(Color entity) {
        if (entity == null) return null;
        ColorBO bo = new ColorBO();
        bo.setColorId(entity.getColorId());
        bo.setColor(entity.getColor());
        return bo;
    }

    public Color colorToEntity(ColorBO bo) {
        if (bo == null) return null;
        Color entity = new Color();
        entity.setColorId(bo.getColorId());
        entity.setColor(bo.getColor());
        return entity;
    }

    public SizeBO sizeToBO(Size entity) {
        if (entity == null) return null;
        SizeBO bo = new SizeBO();
        bo.setSizeId(entity.getSizeId());
        bo.setSize(entity.getSize());
        return bo;
    }

    public Size sizeToEntity(SizeBO bo) {
        if (bo == null) return null;
        Size entity = new Size();
        entity.setSizeId(bo.getSizeId());
        entity.setSize(bo.getSize());
        return entity;
    }

    public ProductBO productToBO(Product entity) {
        if (entity == null) return null;
        ProductBO bo = new ProductBO();
        bo.setProductId(entity.getProductId());
        bo.setCollection(collectionToBO(entity.getCollection()));
        bo.setCategory(categoryToBO(entity.getCategory()));
        bo.setName(entity.getName());
        bo.setDescription(entity.getDescription());
        bo.setPrice(entity.getPrice());
        bo.setScore(entity.getScore());
        return bo;
    }

    public Product productToEntity(ProductBO bo) {
        if (bo == null) return null;
        Product entity = new Product();
        entity.setProductId(bo.getProductId());
        entity.setCollection(collectionToEntity(bo.getCollection()));
        entity.setCategory(categoryToEntity(bo.getCategory()));
        entity.setName(bo.getName());
        entity.setDescription(bo.getDescription());
        entity.setPrice(bo.getPrice());
        entity.setScore(bo.getScore());
        return entity;
    }

    public ProductVariationBO variationToBO(ProductVariation entity) {
        if (entity == null) return null;
        ProductVariationBO bo = new ProductVariationBO();
        bo.setVariationId(entity.getVariationId());
        bo.setProduct(productToBO(entity.getProduct()));
        bo.setColor(colorToBO(entity.getColor()));
        bo.setSize(sizeToBO(entity.getSize()));
        bo.setSkuCode(entity.getSkuCode());
        bo.setStock(entity.getStock());
        return bo;
    }

    public ProductVariation variationToEntity(ProductVariationBO bo) {
        if (bo == null) return null;
        ProductVariation entity = new ProductVariation();
        entity.setVariationId(bo.getVariationId());
        entity.setProduct(productToEntity(bo.getProduct()));
        entity.setColor(colorToEntity(bo.getColor()));
        entity.setSize(sizeToEntity(bo.getSize()));
        entity.setSkuCode(bo.getSkuCode());
        entity.setStock(bo.getStock());
        return entity;
    }
}
