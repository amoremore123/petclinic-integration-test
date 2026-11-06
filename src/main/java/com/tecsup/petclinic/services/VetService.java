package com.tecsup.petclinic.services;

import com.tecsup.petclinic.dtos.VetDTO;
import com.tecsup.petclinic.exceptions.VetNotFoundException;

import java.util.List;

/**
 * Servicio de Vet
 */
public interface VetService {

	VetDTO create(VetDTO vetDTO);

	VetDTO update(Integer id, VetDTO vetDTO) throws VetNotFoundException;

	VetDTO setActive(Integer id, boolean active) throws VetNotFoundException;

	void delete(Integer id) throws VetNotFoundException;

	VetDTO findById(Integer id) throws VetNotFoundException;

	List<VetDTO> findByActive(Boolean active);

	List<VetDTO> findAll();
}
