import { api } from "@/infrastructure/api/api";
import { tokenUtil } from "@/lib/tokenUtil";
import type { Product } from "@/modules/product/Model/models/product";
import type { ProductRequest } from "@/modules/product/Model/models/productRequest";
import { ProductImageRequest } from "../models/productImagesRequest";
import { ProductImages } from "../models/productImages";
import type { ProductVariationDto } from "../models/productVariationDto";
import type { ProductImageUpdateRequest } from "../models/productImageUpdateRequest";

export interface ProductPage {
  content: ProductVariationDto[];
  page: {
    size: number;
    totalElements: number;
    totalPages: number;
    number: number;
  };
}

export const productService = {
  async getAllProducts(page = 0, size = 20): Promise<ProductPage> {
    const token = tokenUtil.getAccessToken();
    const response = await api.get("/admin/products", {
      params: { page, size },
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return response.data;
  },

  async getProductById(id: number): Promise<Product> {
    const token = tokenUtil.getAccessToken();
    const response = await api.get(`/admin/products/${id}`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return response.data.product;
  },

  async createProduct(product: ProductRequest): Promise<Product> {
    const token = tokenUtil.getAccessToken();
    const response = await api.post('/admin/products', product, {
      headers: {
        Authorization: `Bearer ${token}`
      },
    });
    return response.data.product;
  },

  async uploadImages(request: ProductImageRequest): Promise<ProductImages[]> {
    const token = tokenUtil.getAccessToken();
    const formData = new FormData();
    formData.append('productId', String(request.productId));
    formData.append('mainImage', request.mainImage);
    request.carouselImages.forEach(file => formData.append('carouselImages', file));
    const response = await api.post('/admin/products/images', formData, {
      headers: {
        Authorization: `Bearer ${token}`
      },
    });
    return response.data.productImages;
  },

  async updateImages(request: ProductImageUpdateRequest): Promise<ProductImages[]> {
    const token = tokenUtil.getAccessToken();
    const formData = new FormData();
    if (request.newMainImage) formData.append('newMainImage', request.newMainImage);
    request.newCarouselImages.forEach(file => formData.append('newCarouselImages', file));
    request.removedImageIds.forEach(id => formData.append('removedImageIds', String(id)));
    request.existingCarouselIds.forEach(id => formData.append('existingCarouselIds', String(id)));
    const response = await api.put(`/admin/products/${request.productId}/images`, formData, {
      headers: {
        Authorization: `Bearer ${token}`
      },
    });
    return response.data.productImages;
  },

  async updateProduct(id: number, product: ProductRequest): Promise<Product> {
    const token = tokenUtil.getAccessToken();
    const patch = (Object.keys(product) as (keyof ProductRequest)[]).map(key => ({
      op: "replace",
      path: `/${key}`,
      value: product[key],
    }));
    const response = await api.patch(`/admin/products/${id}`, patch, {
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json-patch+json",
      },
    });
    return response.data.product;
  },

  async deleteProduct(id: number): Promise<void> {
    const token = tokenUtil.getAccessToken();
    await api.delete(`/admin/products/${id}`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
  },
};
