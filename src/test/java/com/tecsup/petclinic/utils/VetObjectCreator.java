package com.tecsup.petclinic.utils;

import com.tecsup.petclinic.dtos.VetDTO;

import java.util.List;

/**
 * Datos de prueba reutilizables de Vet. Cada llamada construye objetos nuevos.
 */
public final class VetObjectCreator {

    private VetObjectCreator() {
    }

    /** Vet nuevo, sin id (para POST /vets) */
    public static VetDTO getNewVetTO() {
        return VetDTO.builder()
                .firstName("Ana")
                .lastName("Quispe")
                .email("ana.quispe@petclinic.com")
                .phone("6085550111")
                .build();
    }

    /** Vet 1 de los datos semilla */
    public static VetDTO getCarterVetTO() {
        return VetDTO.builder()
                .id(1)
                .firstName("James")
                .lastName("Carter")
                .email("james.carter@petclinic.com")
                .phone("6085551234")
                .active(true)
                .build();
    }

    /** Vet 6 de los datos semilla (inactivo) */
    public static VetDTO getJenkinsVetTO() {
        return VetDTO.builder()
                .id(6)
                .firstName("Sharon")
                .lastName("Jenkins")
                .email("sharon.jenkins@petclinic.com")
                .phone("6085556789")
                .active(false)
                .build();
    }

    /** Los 5 vets activos de los datos semilla */
    public static List<VetDTO> getActiveVetsTO() {
        return List.of(
                getCarterVetTO(),
                VetDTO.builder().id(2).firstName("Helen").lastName("Leary")
                        .email("helen.leary@petclinic.com").phone("6085552345").active(true).build(),
                VetDTO.builder().id(3).firstName("Linda").lastName("Douglas")
                        .email("linda.douglas@petclinic.com").phone("6085553456").active(true).build(),
                VetDTO.builder().id(4).firstName("Rafael").lastName("Ortega")
                        .email("rafael.ortega@petclinic.com").phone("6085554567").active(true).build(),
                VetDTO.builder().id(5).firstName("Henry").lastName("Stevens")
                        .email("henry.stevens@petclinic.com").phone("6085555678").active(true).build());
    }

    /** Los 6 vets de los datos semilla */
    public static List<VetDTO> getVetsTO() {
        List<VetDTO> vets = new java.util.ArrayList<>(getActiveVetsTO());
        vets.add(getJenkinsVetTO());
        return vets;
    }
}
