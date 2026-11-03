package com.tecsup.petclinic.webs;

import com.tecsup.petclinic.dtos.OwnerDTO;
import com.tecsup.petclinic.exceptions.OwnerNotFoundException;
import com.tecsup.petclinic.services.OwnerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST de Owner (/owners)
 */
@RestController
@Slf4j
public class OwnerController {

	private final OwnerService ownerService;

	public OwnerController(OwnerService ownerService) {
		this.ownerService = ownerService;
	}

	/**
	 * Listar todos los owners, o filtrar por apellido: GET /owners?lastName=Davis
	 */
	@GetMapping(value = "/owners")
	public ResponseEntity<List<OwnerDTO>> findAllOwners(
			@RequestParam(value = "lastName", required = false) String lastName) {

		List<OwnerDTO> owners = (lastName == null)
				? ownerService.findAll()
				: ownerService.findByLastName(lastName);

		owners.forEach(item -> log.info("Owner >> {}", item));
		return ResponseEntity.ok(owners);
	}

	/**
	 * Buscar owner por id
	 */
	@GetMapping(value = "/owners/{id}")
	public ResponseEntity<OwnerDTO> findById(@PathVariable Long id) {
		try {
			return ResponseEntity.ok(ownerService.findById(id));
		} catch (OwnerNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}

	/**
	 * Crear owner (lastName es obligatorio)
	 */
	@PostMapping(value = "/owners")
	public ResponseEntity<OwnerDTO> create(@RequestBody OwnerDTO ownerDTO) {
		if (ownerDTO.getLastName() == null || ownerDTO.getLastName().isBlank()) {
			return ResponseEntity.badRequest().build();
		}
		OwnerDTO newOwner = ownerService.create(ownerDTO);
		return ResponseEntity.status(HttpStatus.CREATED).body(newOwner);
	}

	/**
	 * Actualizar owner
	 */
	@PutMapping(value = "/owners/{id}")
	public ResponseEntity<OwnerDTO> update(@RequestBody OwnerDTO ownerDTO, @PathVariable Long id) {
		try {
			OwnerDTO current = ownerService.findById(id);
			current.setFirstName(ownerDTO.getFirstName());
			current.setLastName(ownerDTO.getLastName());
			current.setAddress(ownerDTO.getAddress());
			current.setCity(ownerDTO.getCity());
			current.setTelephone(ownerDTO.getTelephone());
			return ResponseEntity.ok(ownerService.update(current));
		} catch (OwnerNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}

	/**
	 * Eliminar owner
	 */
	@DeleteMapping(value = "/owners/{id}")
	public ResponseEntity<String> delete(@PathVariable Long id) {
		try {
			ownerService.delete(id);
			return ResponseEntity.ok(" Delete ID :" + id);
		} catch (OwnerNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}
}
