package com.tecsup.petclinic.utils;

import com.tecsup.petclinic.dtos.OwnerDTO;

/**
 * Datos de prueba reutilizables de Owner. Cada llamada devuelve un DTO nuevo.
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
}
