export interface ProductImageUpdateRequest {
  productId: number;
  newMainImage?: File;
  newCarouselImages: File[];
  removedImageIds: number[];
  existingCarouselIds: number[];
}
