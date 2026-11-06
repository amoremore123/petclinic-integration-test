package com.tecsup.petclinic.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

public record VisitDTO(Long id, Integer petId, Integer vetId, LocalDate visitDate,
                       String description, BigDecimal cost) {
}
