package com.tecsup.petclinic.services;

import com.tecsup.petclinic.dtos.VetDTO;
import com.tecsup.petclinic.entities.Vet;
import com.tecsup.petclinic.exceptions.VetNotFoundException;
import com.tecsup.petclinic.mapper.VetMapper;
import com.tecsup.petclinic.repositories.VetRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementación del servicio de Vet
 */
@Service
@Slf4j
public class VetServiceImpl implements VetService {

	private final VetRepository vetRepository;
	private final VetMapper vetMapper;

	public VetServiceImpl(VetRepository vetRepository, VetMapper vetMapper) {
		this.vetRepository = vetRepository;
		this.vetMapper = vetMapper;
	}

	@Override
	public VetDTO create(VetDTO vetDTO) {
		Vet vet = vetMapper.mapToEntity(vetDTO);
		// Un vet nuevo queda activo si no se indica lo contrario
		if (vet.getActive() == null) {
			vet.setActive(true);
		}
		return vetMapper.mapToDto(vetRepository.save(vet));
	}

	@Override
	public VetDTO update(Integer id, VetDTO vetDTO) throws VetNotFoundException {
		// Se modifica la entidad cargada para no perder sus especialidades
		Vet vet = findEntity(id);
		vet.setFirstName(vetDTO.getFirstName());
		vet.setLastName(vetDTO.getLastName());
		vet.setEmail(vetDTO.getEmail());
		vet.setPhone(vetDTO.getPhone());
		if (vetDTO.getActive() != null) {
			vet.setActive(vetDTO.getActive());
		}
		return vetMapper.mapToDto(vetRepository.save(vet));
	}

	@Override
	public VetDTO setActive(Integer id, boolean active) throws VetNotFoundException {
		Vet vet = findEntity(id);
		vet.setActive(active);
		return vetMapper.mapToDto(vetRepository.save(vet));
	}

	@Override
	public void delete(Integer id) throws VetNotFoundException {
		vetRepository.delete(findEntity(id));
	}

	@Override
	public VetDTO findById(Integer id) throws VetNotFoundException {
		return vetMapper.mapToDto(findEntity(id));
	}

	@Override
	public List<VetDTO> findByActive(Boolean active) {
		return vetMapper.mapToDtoList(vetRepository.findByActive(active));
	}

	@Override
	public List<VetDTO> findAll() {
		return vetMapper.mapToDtoList(vetRepository.findAll());
	}

	private Vet findEntity(Integer id) throws VetNotFoundException {
		return vetRepository.findById(id)
				.orElseThrow(() -> new VetNotFoundException("Record not found...!"));
	}
}
