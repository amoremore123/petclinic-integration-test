package com.tecsup.petclinic.webs;

import com.tecsup.petclinic.dtos.SpecialtyDTO;
import com.tecsup.petclinic.exceptions.SpecialtyNotFoundException;
import com.tecsup.petclinic.services.SpecialtyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST de Specialty (/specialties)
 */
@RestController
@Slf4j
public class SpecialtyController {

	private final SpecialtyService specialtyService;

	public SpecialtyController(SpecialtyService specialtyService) {
		this.specialtyService = specialtyService;
	}

	/**
	 * Listar todas, o filtrar: GET /specialties?name=surgery  o  ?office=Farewell
	 */
	@GetMapping(value = "/specialties")
	public ResponseEntity<List<SpecialtyDTO>> findAllSpecialties(
			@RequestParam(value = "name", required = false) String name,
			@RequestParam(value = "office", required = false) String office) {

		List<SpecialtyDTO> specialties;
		if (name != null) {
			specialties = specialtyService.findByName(name);
		} else if (office != null) {
			specialties = specialtyService.findByOffice(office);
		} else {
			specialties = specialtyService.findAll();
		}

		specialties.forEach(item -> log.info("Specialty >> {}", item));
		return ResponseEntity.ok(specialties);
	}

	/**
	 * Buscar especialidad por id
	 */
	@GetMapping(value = "/specialties/{id}")
	public ResponseEntity<SpecialtyDTO> findById(@PathVariable Integer id) {
		try {
			return ResponseEntity.ok(specialtyService.findById(id));
		} catch (SpecialtyNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}

	/**
	 * Crear especialidad.
	 * Las horas deben estar entre 0 y 23, y la de apertura debe ser menor que la de cierre.
	 */
	@PostMapping(value = "/specialties")
	public ResponseEntity<SpecialtyDTO> create(@RequestBody SpecialtyDTO specialtyDTO) {
		if (!isValidSchedule(specialtyDTO)) {
			return ResponseEntity.badRequest().build();
		}
		SpecialtyDTO newSpecialty = specialtyService.create(specialtyDTO);
		return ResponseEntity.status(HttpStatus.CREATED).body(newSpecialty);
	}

	/**
	 * Eliminar especialidad
	 */
	@DeleteMapping(value = "/specialties/{id}")
	public ResponseEntity<String> delete(@PathVariable Integer id) {
		try {
			specialtyService.delete(id);
			return ResponseEntity.ok(" Delete ID :" + id);
		} catch (SpecialtyNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}

	private boolean isValidSchedule(SpecialtyDTO dto) {
		Integer open = dto.getOpenHour();
		Integer close = dto.getCloseHour();
		if (open == null || close == null) {
			return false;
		}
		boolean inRange = open >= 0 && open <= 23 && close >= 0 && close <= 23;
		return inRange && open < close;
	}
}
