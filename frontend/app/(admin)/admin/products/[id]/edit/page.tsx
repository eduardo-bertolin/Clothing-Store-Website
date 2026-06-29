"use client";

import { useRouter, useParams } from "next/navigation";
import { AdminHeader } from "@/modules/admin/View/components/AdminHeader";
import { ProductUpdateForm } from "@/modules/product/View/components/ProductUpdateForm";

export default function ProductEditPage() {
  const router = useRouter();
  const params = useParams();
  const productId = Number(params.id);

  const handleSubmit = () => {
    router.push("/admin/products");
  };

  const handleCancel = () => {
    router.push("/admin/products");
  };

  return (
    <div className="p-6 bg-[#C8C8C8] min-h-screen">
      <AdminHeader
        title="Editar Produto"
        breadcrumb={["Inicio", "Produtos", "Editar Produto"]}
      />
      <div className="mt-6">
        <ProductUpdateForm
          productId={productId}
          onSubmit={handleSubmit}
          onCancel={handleCancel}
        />
      </div>
    </div>
  );
}
