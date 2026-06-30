"use client";

import { ChevronDown, ChevronUp, ImagePlus, X } from "lucide-react";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/shared/components/ui/dropdown-menu";
import { Input } from "@/shared/components/ui/input";
import { Label } from "@/shared/components/ui/label";
import { Textarea } from "@/shared/components/ui/textarea";
import { Button } from "@/shared/components/ui/button";
import { useEffect, useRef, useState } from "react";
import { categoryService, type Category } from "@/modules/product/Model/services/categoryService";
import { collectionService, type Collection } from "@/modules/product/Model/services/colectionService";
import { productService } from "@/modules/product/Model/services/productService";
import { ProductImages } from "../../Model/models/productImages";

type GalleryItem =
  | { kind: "existing"; id: number; url: string }
  | { kind: "new"; file: File; previewUrl: string };

interface ProductUpdateFormProps {
  productId: number;
  onSubmit: () => void;
  onCancel: () => void;
}

interface ProductFormData {
  name: string;
  description: string;
  categoryId: number | null;
  collectionId: number | null;
  price: string;
  score: number;
  images: ProductImages[] | null;
}

const ALLOWED_IMAGE_TYPES = ["image/jpeg", "image/png", "image/webp"];
const isValidImageType = (file: File) => ALLOWED_IMAGE_TYPES.includes(file.type);

export function ProductUpdateForm({ productId, onSubmit, onCancel }: ProductUpdateFormProps) {
  const [isLoading, setIsLoading] = useState(true);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [categories, setCategories] = useState<Category[]>([]);
  const [collections, setCollections] = useState<Collection[]>([]);

  const mainImageInputRef = useRef<HTMLInputElement>(null);
  const galleryInputRef = useRef<HTMLInputElement>(null);
  const originalCarouselIdsRef = useRef<number[]>([]);

  const [formData, setFormData] = useState<ProductFormData>({
    name: "",
    description: "",
    categoryId: null,
    collectionId: null,
    price: "",
    score: 0,
    images: null,
  });

  const [selectedCategory, setSelectedCategory] = useState<string | null>(null);
  const [selectedCollection, setSelectedCollection] = useState<string | null>(null);

  const [existingMainImage, setExistingMainImage] = useState<ProductImages | null>(null);
  const [newMainImageFile, setNewMainImageFile] = useState<File | null>(null);
  const [mainImagePreview, setMainImagePreview] = useState<string | null>(null);
  const [galleryItems, setGalleryItems] = useState<GalleryItem[]>([]);
  const [removedImageIds, setRemovedImageIds] = useState<number[]>([]);

  useEffect(() => {
    Promise.all([
      productService.getProductById(productId),
      categoryService.getAllCategories(),
      collectionService.getAllCollection(),
    ])
      .then(([product, cats, cols]) => {
        setCategories(cats);
        setCollections(cols);
        setFormData({
          name: product.name,
          description: product.description,
          categoryId: product.category?.id ?? null,
          collectionId: product.collection?.id ?? null,
          price: String(product.price),
          score: product.score ?? 0,
          images: product.images ?? null,
        });
        setSelectedCategory(product.category?.name ?? null);
        setSelectedCollection(product.collection?.name ?? null);

        if (product.images) {
          const mainImg = product.images.find(img => img.type === "MAIN") ?? null;
          setExistingMainImage(mainImg);
          if (mainImg) setMainImagePreview(mainImg.imageUrl);

          const sortedCarousel = product.images
            .filter(img => img.type === "CAROUSEL")
            .sort((a, b) => a.position - b.position)

          const carouselItems: GalleryItem[] = sortedCarousel
            .map(img => ({ kind: "existing" as const, id: img.id, url: img.imageUrl }));
          setGalleryItems(carouselItems);
          originalCarouselIdsRef.current = sortedCarousel.map(img => img.id);
        }
      })
      .catch(() => setError("Erro ao carregar dados do produto."))
      .finally(() => setIsLoading(false));
  }, [productId]);

  const handleFieldChange = (field: keyof ProductFormData, value: string | number | null) => {
    setFormData(prev => ({ ...prev, [field]: value }));
  };

  const handleCategorySelect = (category: Category) => {
    setSelectedCategory(category.categoryName);
    handleFieldChange("categoryId", category.categoryId);
  };

  const handleCollectionSelect = (collection: Collection) => {
    setSelectedCollection(collection.collectionName);
    handleFieldChange("collectionId", collection.collectionId);
  };

  const handleMainImageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;
    if (!isValidImageType(file)) {
      setError("A imagem principal deve ser JPEG, PNG ou WebP.");
      e.target.value = "";
      return;
    }
    setError(null);
    setNewMainImageFile(file);
    setMainImagePreview(URL.createObjectURL(file));
  };

  const handleGalleryImagesChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const files = Array.from(e.target.files ?? []);
    if (!files.length) return;
    const invalidFile = files.find(f => !isValidImageType(f));
    if (invalidFile) {
      setError(`"${invalidFile.name}" não é um tipo de imagem válido. Use JPEG, PNG ou WebP.`);
      e.target.value = "";
      return;
    }
    setError(null);
    const newItems: GalleryItem[] = files.map(f => ({
      kind: "new",
      file: f,
      previewUrl: URL.createObjectURL(f),
    }));
    setGalleryItems(prev => [...prev, ...newItems]);
    e.target.value = "";
  };

  const removeGalleryImage = (index: number) => {
    const item = galleryItems[index];
    if (item.kind === "existing") {
      setRemovedImageIds(prev => [...prev, item.id]);
    }
    setGalleryItems(prev => prev.filter((_, i) => i !== index));
  };

  const moveGalleryImage = (index: number, dir: "up" | "down") => {
    setGalleryItems(prev => {
      const next = [...prev];
      const swapIdx = dir === "up" ? index - 1 : index + 1;
      [next[index], next[swapIdx]] = [next[swapIdx], next[index]];
      return next;
    });
  };

  const positionsChanged = () => {
    const currentIds = galleryItems
      .filter((i): i is Extract<GalleryItem, { kind: "existing" }> => i.kind === "existing")
      .map(i => i.id);
    const orig = originalCarouselIdsRef.current;
    return currentIds.length !== orig.length || currentIds.some((id, idx) => id !== orig[idx]);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);

    if (!formData.name || !formData.categoryId || !formData.collectionId || !formData.price) {
      setError("Preencha todos os campos obrigatórios.");
      return;
    }

    try {
      setIsSubmitting(true);

      await productService.updateProduct(productId, {
        name: formData.name,
        description: formData.description,
        categoryId: formData.categoryId,
        collectionId: formData.collectionId,
        price: parseFloat(formData.price),
        score: formData.score,
      });

      const hasExistingImages = !!(formData.images && formData.images.length > 0);
      const newCarouselFiles = galleryItems
        .filter((i): i is Extract<GalleryItem, { kind: "new" }> => i.kind === "new")
        .map(i => i.file);
      const existingCarouselIds = galleryItems
        .filter((i): i is Extract<GalleryItem, { kind: "existing" }> => i.kind === "existing")
        .map(i => i.id);

      const imageChanged =
        newMainImageFile !== null ||
        removedImageIds.length > 0 ||
        newCarouselFiles.length > 0 ||
        positionsChanged();

      if (!hasExistingImages && newMainImageFile) {
        await productService.uploadImages({
          productId,
          mainImage: newMainImageFile,
          carouselImages: newCarouselFiles,
        });
      } else if (hasExistingImages && imageChanged) {
        await productService.updateImages({
          productId,
          newMainImage: newMainImageFile ?? undefined,
          newCarouselImages: newCarouselFiles,
          removedImageIds,
          existingCarouselIds,
        });
      }

      onSubmit();
    } catch {
      setError("Erro ao atualizar produto.");
    } finally {
      setIsSubmitting(false);
    }
  };

  if (isLoading) {
    return <p className="text-gray-500">Carregando produto...</p>;
  }

  return (
    <form onSubmit={handleSubmit} className="flex flex-col gap-12 bg-white p-6 rounded-2xl shadow-lg max-w-275">
      {error && (
        <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg text-sm">
          {error}
        </div>
      )}

      <div className="flex gap-12">
        <div className="flex flex-col lg:flex-row gap-8">
          <div className="flex flex-col gap-5 max-w-[520px]">
            <Label className="font-alexandria" htmlFor="name">Nome do Produto</Label>
            <Input
              id="name"
              type="text"
              className="md:w-130 md:h-11"
              value={formData.name}
              onChange={e => handleFieldChange("name", e.target.value)}
            />

            <Label className="font-alexandria" htmlFor="description">Descrição</Label>
            <Textarea
              id="description"
              className="md:w-130 md:h-24"
              value={formData.description}
              onChange={e => handleFieldChange("description", e.target.value)}
            />

            <Label className="font-alexandria" htmlFor="category">Categoria</Label>
            <DropdownMenu>
              <DropdownMenuTrigger className="w-full md:w-130 h-11 border rounded-md text-left px-3 flex items-center justify-between">
                {selectedCategory || "Selecione uma categoria"}
                <ChevronDown className="w-4 h-4 ml-auto" />
              </DropdownMenuTrigger>
              <DropdownMenuContent className="w-full md:w-130">
                {categories.map(c => (
                  <DropdownMenuItem key={c.categoryId} onSelect={() => handleCategorySelect(c)}>
                    {c.categoryName}
                  </DropdownMenuItem>
                ))}
              </DropdownMenuContent>
            </DropdownMenu>

            <div className="flex gap-4">
              <div className="flex flex-col gap-4">
                <Label className="font-alexandria" htmlFor="collection">Coleção</Label>
                <DropdownMenu>
                  <DropdownMenuTrigger className="w-full md:w-60 h-11 border rounded-md text-left px-3 flex items-center justify-between">
                    {selectedCollection || "Selecione uma coleção"}
                    <ChevronDown className="w-4 h-4 ml-auto" />
                  </DropdownMenuTrigger>
                  <DropdownMenuContent className="w-full md:w-60">
                    {collections.map(c => (
                      <DropdownMenuItem key={c.collectionId} onSelect={() => handleCollectionSelect(c)}>
                        {c.collectionName}
                      </DropdownMenuItem>
                    ))}
                  </DropdownMenuContent>
                </DropdownMenu>
              </div>

              <div className="flex flex-col gap-4">
                <Label className="font-alexandria" htmlFor="price">Preço</Label>
                <Input
                  id="price"
                  type="number"
                  className="md:w-60 md:h-11"
                  placeholder="Ex: 109.90"
                  value={formData.price}
                  onChange={e => handleFieldChange("price", e.target.value)}
                />
              </div>
            </div>
          </div>
        </div>

        {/* Right column — images */}
        <div className="flex flex-col gap-6 lg:w-90">
          <div className="flex flex-col gap-2">
            <button
              type="button"
              onClick={() => mainImageInputRef.current?.click()}
              className="w-full aspect-square border-2 border-dashed border-gray-300 rounded-lg bg-gray-50 hover:bg-gray-100 transition-colors flex flex-col items-center justify-center gap-2 overflow-hidden relative"
            >
              {mainImagePreview ? (
                <img
                  src={mainImagePreview}
                  alt="Preview"
                  className="w-full h-full object-cover"
                />
              ) : (
                <>
                  <ImagePlus className="w-10 h-10 text-gray-400" />
                  <span className="text-sm text-gray-500 font-alexandria">Adicionar imagem</span>
                </>
              )}
            </button>
            <input
              ref={mainImageInputRef}
              type="file"
              accept="image/jpeg,image/png,image/webp"
              className="hidden"
              onChange={handleMainImageChange}
            />

            <div className="flex flex-col gap-2">
              <Label className="font-medium text-gray-700">Galeria do produto</Label>
              <div className="w-full min-h-[150px] border-2 border-dashed border-gray-300 rounded-lg bg-gray-50 p-3 flex flex-wrap gap-2">
                {galleryItems.map((item, i) => (
                  <div
                    key={item.kind === "existing" ? item.id : item.previewUrl}
                    className="relative w-30 h-30 rounded overflow-hidden group"
                  >
                    <img
                      src={item.kind === "existing" ? item.url : item.previewUrl}
                      alt=""
                      className="w-full h-full object-cover"
                    />

                    {/* reorder buttons */}
                    <div className="absolute top-1 left-1 flex gap-1 opacity-0 group-hover:opacity-100 transition-opacity z-10">
                      {i > 0 && (
                        <button
                          type="button"
                          onClick={() => moveGalleryImage(i, "up")}
                          className="bg-black/60 text-white rounded p-0.5 hover:bg-black/80 transition-colors"
                        >
                          <ChevronUp className="w-3 h-3" />
                        </button>
                      )}
                      {i < galleryItems.length - 1 && (
                        <button
                          type="button"
                          onClick={() => moveGalleryImage(i, "down")}
                          className="bg-black/60 text-white rounded p-0.5 hover:bg-black/80 transition-colors"
                        >
                          <ChevronDown className="w-3 h-3" />
                        </button>
                      )}
                    </div>

                    {/* remove button */}
                    <button
                      type="button"
                      onClick={() => removeGalleryImage(i)}
                      className="absolute inset-0 bg-black/40 opacity-0 group-hover:opacity-100 transition-opacity flex items-center justify-center"
                    >
                      <X className="w-4 h-4 text-white" />
                    </button>
                  </div>
                ))}

                <button
                  type="button"
                  onClick={() => galleryInputRef.current?.click()}
                  className="w-30 h-30 border-2 border-dashed border-gray-300 rounded flex flex-col items-center justify-center gap-1 text-gray-400 hover:bg-gray-100 transition-colors"
                >
                  <ImagePlus className="w-5 h-5" />
                  <span className="text-xs">Adicionar</span>
                </button>
              </div>
              <input
                ref={galleryInputRef}
                type="file"
                accept="image/jpeg,image/png,image/webp"
                multiple
                className="hidden"
                onChange={handleGalleryImagesChange}
              />
            </div>
          </div>
        </div>
      </div>

      <div className="flex gap-3 pt-2">
        <Button
          type="submit"
          disabled={isSubmitting}
          className="bg-gray-900 text-white px-6 py-2.5 rounded-md text-sm font-medium hover:bg-gray-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
        >
          {isSubmitting ? "Salvando..." : "Salvar alterações"}
        </Button>
        <Button
          type="button"
          variant="outline"
          onClick={onCancel}
          className="border border-gray-300 text-gray-700 px-6 py-2.5 rounded-md text-sm font-medium hover:bg-gray-50 transition-colors"
        >
          Cancelar
        </Button>
      </div>
    </form>
  );
}
