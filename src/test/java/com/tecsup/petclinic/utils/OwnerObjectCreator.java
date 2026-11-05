package com.tecsup.petclinic.utils;

import com.tecsup.petclinic.dtos.OwnerDTO;

import java.util.List;

/**
 * Datos de prueba reutilizables de Owner. Cada llamada construye objetos nuevos.
 */
public final class OwnerObjectCreator {

    private OwnerObjectCreator() {
    }

    public static OwnerDTO getOwnerTO() {
        return OwnerDTO.builder()
                .firstName("Carlos")
                .lastName("Torres")
                .address("123 Main St")
                .city("Madison")
                .telephone("6085550198")
                .build();
    }

    public static OwnerDTO getUpdatedOwnerTO() {
        return OwnerDTO.builder()
                .firstName("Luis")
                .lastName("Ramirez")
                .address("456 Oak St")
                .city("Sun Prairie")
                .telephone("6085550197")
                .build();
    }

    public static OwnerDTO getFranklinOwnerTO() {
        return OwnerDTO.builder()
                .id(1L)
                .firstName("George")
                .lastName("Franklin")
                .address("110 W. Liberty St.")
                .city("Madison")
                .telephone("6085551023")
                .build();
    }

    public static List<OwnerDTO> getDavisOwnersTO() {
        return List.of(
                OwnerDTO.builder()
                        .id(2L)
                        .firstName("Betty")
                        .lastName("Davis")
                        .address("638 Cardinal Ave.")
                        .city("Sun Prairie")
                        .telephone("6085551749")
                        .build(),
                OwnerDTO.builder()
                        .id(4L)
                        .firstName("Harold")
                        .lastName("Davis")
                        .address("563 Friendly St.")
                        .city("Windsor")
                        .telephone("6085553198")
                        .build());
    }

    public static List<OwnerDTO> getOwnersTO() {
        List<OwnerDTO> davisOwners = getDavisOwnersTO();
        return List.of(getFranklinOwnerTO(), davisOwners.get(0), davisOwners.get(1));
    }
}
