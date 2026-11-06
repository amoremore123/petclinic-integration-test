package com.tecsup.petclinic.utils;

import com.tecsup.petclinic.dtos.SpecialtyDTO;

import java.util.List;

/**
 * Datos de prueba reutilizables de Specialty. Cada llamada construye objetos nuevos.
 */
public final class SpecialtyObjectCreator {

    private SpecialtyObjectCreator() {
    }

    /** Especialidad nueva y válida, sin id (para POST /specialties) */
    public static SpecialtyDTO getNewSpecialtyTO() {
        return SpecialtyDTO.builder()
                .name("cardiology")
                .office("Lima")
                .openHour(9)
                .closeHour(17)
                .build();
    }

    /** Horario inválido: abre a la misma hora o después de cerrar */
    public static SpecialtyDTO getInvalidScheduleTO() {
        return SpecialtyDTO.builder()
                .name("dermatology")
                .office("Lima")
                .openHour(18)
                .closeHour(10)
                .build();
    }

    /** Horas fuera del rango 0-23 */
    public static SpecialtyDTO getInvalidHourRangeTO() {
        return SpecialtyDTO.builder()
                .name("neurology")
                .office("Lima")
                .openHour(8)
                .closeHour(25)
                .build();
    }

    public static SpecialtyDTO getRadiologyTO() {
        return SpecialtyDTO.builder().id(1).name("radiology").office("Farewell")
                .openHour(8).closeHour(18).build();
    }

    public static SpecialtyDTO getSurgeryTO() {
        return SpecialtyDTO.builder().id(2).name("surgery").office("Maryland")
                .openHour(8).closeHour(12).build();
    }

    public static SpecialtyDTO getDentistryTO() {
        return SpecialtyDTO.builder().id(3).name("dentistry").office("Terranova")
                .openHour(9).closeHour(19).build();
    }

    /** Las 3 especialidades de data.sql */
    public static List<SpecialtyDTO> getSpecialtiesTO() {
        return List.of(getRadiologyTO(), getSurgeryTO(), getDentistryTO());
    }
}
