"use client";

import { useRouter } from "next/navigation";
import { AdminHeader } from "@/modules/admin/View/components/AdminHeader";
import { useProducts } from "@/modules/product/ViewModel/useProducts";
import { ProductCard } from "@/modules/product/View/components/ProductCard";

const PAGE_SIZE_OPTIONS = [10, 20, 40];

export default function ProductsPage() {
  const router = useRouter();
  const { products, loading, error, page, size, totalPages, totalElements, goToPage, changeSize, removeProduct } = useProducts();

  return (
    <div className="p-6">
      <AdminHeader
        title="Todos os produtos"
        breadcrumb={["Inicio", "Todos os produtos"]}
        showCreateButton
        createButtonLabel="CADASTRAR PRODUTO"
        onCreateClick={() => router.push("/admin/products/create")}
      />

      {loading && <p className="mt-6 text-gray-500">Carregando...</p>}
      {error && <p className="mt-6 text-red-500">{error}</p>}

      {!loading && !error && (
        <>
          <div className="mt-6 grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
            {products.map(product => (
              <ProductCard key={product.productId} product={product} onDeleteSuccess={removeProduct} />
            ))}
          </div>

          <div className="mt-8 flex flex-col sm:flex-row items-center justify-between gap-4">
            <div className="flex items-center gap-2 text-sm text-gray-600">
              <span>Itens por página:</span>
              <select
                value={size}
                onChange={e => changeSize(Number(e.target.value))}
                className="border rounded px-2 py-1 text-sm"
              >
                {PAGE_SIZE_OPTIONS.map(opt => (
                  <option key={opt} value={opt}>{opt}</option>
                ))}
              </select>
              <span className="ml-2">
                {totalElements} produto{totalElements !== 1 ? "s" : ""} no total
              </span>
            </div>

            <div className="flex items-center gap-2">
              <button
                onClick={() => goToPage(page - 1)}
                disabled={page === 0}
                className="px-3 py-1 border rounded text-sm disabled:opacity-40 disabled:cursor-not-allowed hover:bg-gray-50"
              >
                Anterior
              </button>

              {Array.from({ length: totalPages }, (_, i) => (
                <button
                  key={i}
                  onClick={() => goToPage(i)}
                  className={`px-3 py-1 border rounded text-sm ${
                    i === page ? "bg-gray-900 text-white border-gray-900" : "hover:bg-gray-50"
                  }`}
                >
                  {i + 1}
                </button>
              ))}

              <button
                onClick={() => goToPage(page + 1)}
                disabled={page >= totalPages - 1}
                className="px-3 py-1 border rounded text-sm disabled:opacity-40 disabled:cursor-not-allowed hover:bg-gray-50"
              >
                Próximo
              </button>
            </div>
          </div>
        </>
      )}
    </div>
  );
}
