package com.tecsup.petclinic.webs;

import com.tecsup.petclinic.dtos.VetDTO;
import com.tecsup.petclinic.exceptions.VetNotFoundException;
import com.tecsup.petclinic.services.VetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST de Vet (/vets)
 */
@RestController
@Slf4j
public class VetController {

	private final VetService vetService;

	public VetController(VetService vetService) {
		this.vetService = vetService;
	}

	/**
	 * Listar todos los vets, o filtrar por estado: GET /vets?active=true
	 */
	@GetMapping(value = "/vets")
	public ResponseEntity<List<VetDTO>> findAllVets(
			@RequestParam(value = "active", required = false) Boolean active) {

		List<VetDTO> vets = (active == null)
				? vetService.findAll()
				: vetService.findByActive(active);

		vets.forEach(item -> log.info("Vet >> {}", item));
		return ResponseEntity.ok(vets);
	}

	/**
	 * Buscar vet por id
	 */
	@GetMapping(value = "/vets/{id}")
	public ResponseEntity<VetDTO> findById(@PathVariable Integer id) {
		try {
			return ResponseEntity.ok(vetService.findById(id));
		} catch (VetNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}

	/**
	 * Crear vet
	 */
	@PostMapping(value = "/vets")
	public ResponseEntity<VetDTO> create(@RequestBody VetDTO vetDTO) {
		VetDTO newVet = vetService.create(vetDTO);
		return ResponseEntity.status(HttpStatus.CREATED).body(newVet);
	}

	/**
	 * Actualizar vet
	 */
	@PutMapping(value = "/vets/{id}")
	public ResponseEntity<VetDTO> update(@RequestBody VetDTO vetDTO, @PathVariable Integer id) {
		try {
			return ResponseEntity.ok(vetService.update(id, vetDTO));
		} catch (VetNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}

	/**
	 * Desactivar vet (soft delete): PUT /vets/{id}/deactivate
	 */
	@PutMapping(value = "/vets/{id}/deactivate")
	public ResponseEntity<VetDTO> deactivate(@PathVariable Integer id) {
		try {
			return ResponseEntity.ok(vetService.setActive(id, false));
		} catch (VetNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}

	/**
	 * Reactivar vet: PUT /vets/{id}/activate
	 */
	@PutMapping(value = "/vets/{id}/activate")
	public ResponseEntity<VetDTO> activate(@PathVariable Integer id) {
		try {
			return ResponseEntity.ok(vetService.setActive(id, true));
		} catch (VetNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}

	/**
	 * Eliminar vet
	 */
	@DeleteMapping(value = "/vets/{id}")
	public ResponseEntity<String> delete(@PathVariable Integer id) {
		try {
			vetService.delete(id);
			return ResponseEntity.ok(" Delete ID :" + id);
		} catch (VetNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}
}
