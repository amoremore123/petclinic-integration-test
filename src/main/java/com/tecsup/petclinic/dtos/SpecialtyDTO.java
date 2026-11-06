package com.tecsup.petclinic.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de Specialty
 * openHour = h_open, closeHour = h_close
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class SpecialtyDTO {

	private Integer id;
	private String name;
	private String office;
	private Integer openHour;
	private Integer closeHour;
}
