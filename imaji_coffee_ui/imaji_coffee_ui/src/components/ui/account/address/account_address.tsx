import { ReactElement, useState } from "react";
import { IoArrowBack } from "react-icons/io5";
import { useNavigate } from "react-router-dom";
import { Spinner } from "@heroui/spinner";
import toast from "react-hot-toast";

import DefaultLayout from "@/layouts/default.tsx";
import AccountHeader from "@/components/ui/account/account_header.tsx";
import PrimaryButton from "@/components/ui/button/primary_button.tsx";
import Modal from "@/components/layouts/modal.tsx";
import {
  useCreateAddressMutation,
  useDeleteAddressMutation,
  useGetAddressesQuery,
  useSetDefaultAddressMutation,
  useUpdateAddressMutation,
} from "@/api/account/accountApi.ts";
import { AddressResponseDto } from "@/types";
import AddressForm from "@/components/ui/account/address_form.tsx";

export default function AccountAddress(): ReactElement {
  return (
    <DefaultLayout>
      <AccountHeader
        content={"Here you can manage your address"}
        title={"Address"}
      />
      <AccountAddressContent />
    </DefaultLayout>
  );
}

function AccountAddressContent(): ReactElement {
  const navigate = useNavigate();
  const {
    data: addresses,
    isLoading,
    isError,
    refetch,
  } = useGetAddressesQuery();
  const [deleteAddress, { isLoading: isDeleting }] = useDeleteAddressMutation();
  const [setDefault, { isLoading: isSettingDefault }] =
    useSetDefaultAddressMutation();

  const [isFormOpen, setIsFormOpen] = useState(false);
  const [editingAddress, setEditingAddress] =
    useState<AddressResponseDto | null>(null);
  const [deletingId, setDeletingId] = useState<number | null>(null);

  const handleDelete = async () => {
    if (deletingId == null) return;
    try {
      await deleteAddress(deletingId).unwrap();
      toast.success("Address deleted");
      setDeletingId(null);
    } catch (error) {
      toast.error(extractApiError(error));
    }
  };

  const handleSetDefault = async (id: number) => {
    try {
      await setDefault(id).unwrap();
      toast.success("Default address updated");
    } catch (error) {
      toast.error(extractApiError(error));
    }
  };

  const openCreate = () => {
    setEditingAddress(null);
    setIsFormOpen(true);
  };

  const openEdit = (address: AddressResponseDto) => {
    setEditingAddress(address);
    setIsFormOpen(true);
  };

  const closeForm = () => {
    setIsFormOpen(false);
    setEditingAddress(null);
  };

  return (
    <div
      className={
        "px-5 py-[48px] lg:px-[124px] lg:py-[80px] flex flex-col gap-[48px]"
      }
    >
      <div
        className={
          "flex flex-col lg:flex-row gap-6 lg:gap-8 items-start lg:items-center justify-between"
        }
      >
        <div className="flex items-center gap-6">
          <PrimaryButton type={"button"} onPress={() => navigate(-1)}>
            <IoArrowBack />
            <span>Back</span>
          </PrimaryButton>
          <p className={"font-medium text-4xl lg:text-5xl"}>Address List</p>
        </div>
        <PrimaryButton
          className={"bg-primary text-white"}
          type={"button"}
          onPress={openCreate}
        >
          Add Address
        </PrimaryButton>
      </div>

      {isLoading ? (
        <div className={"flex justify-center"}>
          <Spinner color={"primary"} />
        </div>
      ) : isError ? (
        <div className={"flex flex-col gap-4 items-center"}>
          <p>Failed to load addresses.</p>
          <PrimaryButton type={"button"} onPress={() => refetch()}>
            Retry
          </PrimaryButton>
        </div>
      ) : addresses && addresses.length > 0 ? (
        <div
          className={
            "flex flex-col gap-8 border border-light-grey-40 p-6 lg:p-8"
          }
        >
          {addresses.map((address) => (
            <AddressCard
              key={address.addressId}
              address={address}
              isDeleting={isDeleting}
              isSettingDefault={isSettingDefault}
              onDelete={() => setDeletingId(address.addressId)}
              onEdit={() => openEdit(address)}
              onSetDefault={() => handleSetDefault(address.addressId)}
            />
          ))}
        </div>
      ) : (
        <div className={"border border-light-grey-40 p-6 lg:p-8 text-center"}>
          <p className={"text-lg lg:text-xl"}>
            You have no saved addresses yet.
          </p>
        </div>
      )}

      <AddressFormModal
        address={editingAddress}
        isOpen={isFormOpen}
        onClose={closeForm}
      />

      <Modal
        cancelText="Cancel"
        confirmText="Delete"
        isOpen={deletingId != null}
        onClose={() => setDeletingId(null)}
        onConfirm={handleDelete}
      >
        <p className="text-lg py-4">
          Are you sure you want to delete this address?
        </p>
      </Modal>
    </div>
  );
}

function AddressCard({
  address,
  isDeleting,
  isSettingDefault,
  onDelete,
  onEdit,
  onSetDefault,
}: {
  address: AddressResponseDto;
  isDeleting: boolean;
  isSettingDefault: boolean;
  onDelete: () => void;
  onEdit: () => void;
  onSetDefault: () => void;
}): ReactElement {
  return (
    <div
      className={
        "flex flex-col gap-3 lg:flex-row border-b border-light-grey-40 pb-5 lg:pb-8 last:border-b-0 last:pb-0"
      }
    >
      <div className={"flex flex-col gap-3 lg:flex-8/12"}>
        <h5 className={"text-primary text-base font-medium lg:text-xl"}>
          {address.isDefault ? "House - Main Address" : "House"}
        </h5>
        <p className={"text-xl font-medium lg:text-4xl"}>{address.name}</p>
        <p className={"text-base lg:text-xl"}>{address.phoneNumber}</p>
        <p className={"text-base lg:text-xl"}>
          {address.street}
          {address.apartment ? `, ${address.apartment}` : ""}
        </p>
        <p className={"text-base lg:text-xl"}>
          {address.city}, {address.province} {address.postalCode}
        </p>
        <p className={"text-base lg:text-xl"}>{address.country}</p>
      </div>
      <div className={"flex flex-wrap gap-4 lg:flex-4/12 lg:justify-end"}>
        {!address.isDefault && (
          <PrimaryButton
            className={"bg-primary text-white"}
            content={isSettingDefault ? "..." : "Set Default"}
            disabled={isSettingDefault}
            type={"button"}
            onPress={onSetDefault}
          />
        )}
        <PrimaryButton content="Edit" type={"button"} onPress={onEdit} />
        <PrimaryButton
          className={"bg-transparent border-red-500 text-red-500"}
          content={isDeleting ? "..." : "Delete"}
          disabled={isDeleting}
          type={"button"}
          onPress={onDelete}
        />
      </div>
    </div>
  );
}

function AddressFormModal({
  address,
  isOpen,
  onClose,
}: {
  address: AddressResponseDto | null;
  isOpen: boolean;
  onClose: () => void;
}): ReactElement {
  const [createAddress, { isLoading: isCreating }] = useCreateAddressMutation();
  const [updateAddress, { isLoading: isUpdating }] = useUpdateAddressMutation();

  const handleSubmit = async (data: AddressResponseDto) => {
    try {
      if (address) {
        await updateAddress({ id: address.addressId, data }).unwrap();
        toast.success("Address updated");
      } else {
        await createAddress(data).unwrap();
        toast.success("Address created");
      }
      onClose();
    } catch (error) {
      toast.error(extractApiError(error));
    }
  };

  return (
    <Modal
      cancelText="Cancel"
      confirmText=""
      haveFooter={false}
      isOpen={isOpen}
      size="2xl"
      onClose={onClose}
    >
      <div className="py-4">
        <h3 className="text-2xl font-medium mb-6">
          {address ? "Edit Address" : "Add Address"}
        </h3>
        <AddressForm
          address={address}
          isLoading={isCreating || isUpdating}
          onCancel={onClose}
          onSubmit={handleSubmit}
        />
      </div>
    </Modal>
  );
}

function extractApiError(error: unknown): string {
  const data =
    typeof error === "object" && error !== null && "data" in error
      ? (error as { data?: { message?: string } }).data
      : undefined;

  return data?.message ?? "Something went wrong. Please try again.";
}
