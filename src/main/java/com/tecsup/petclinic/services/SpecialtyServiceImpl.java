package com.tecsup.petclinic.services;

import com.tecsup.petclinic.dtos.SpecialtyDTO;
import com.tecsup.petclinic.entities.Specialty;
import com.tecsup.petclinic.exceptions.SpecialtyNotFoundException;
import com.tecsup.petclinic.mapper.SpecialtyMapper;
import com.tecsup.petclinic.repositories.SpecialtyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementación del servicio de Specialty
 */
@Service
@Slf4j
public class SpecialtyServiceImpl implements SpecialtyService {

	private final SpecialtyRepository specialtyRepository;
	private final SpecialtyMapper specialtyMapper;

	public SpecialtyServiceImpl(SpecialtyRepository specialtyRepository, SpecialtyMapper specialtyMapper) {
		this.specialtyRepository = specialtyRepository;
		this.specialtyMapper = specialtyMapper;
	}

	@Override
	public SpecialtyDTO create(SpecialtyDTO specialtyDTO) {
		Specialty saved = specialtyRepository.save(specialtyMapper.mapToEntity(specialtyDTO));
		return specialtyMapper.mapToDto(saved);
	}

	@Override
	public void delete(Integer id) throws SpecialtyNotFoundException {
		specialtyRepository.delete(findEntity(id));
	}

	@Override
	public SpecialtyDTO findById(Integer id) throws SpecialtyNotFoundException {
		return specialtyMapper.mapToDto(findEntity(id));
	}

	@Override
	public List<SpecialtyDTO> findByName(String name) {
		return specialtyMapper.mapToDtoList(specialtyRepository.findByName(name));
	}

	@Override
	public List<SpecialtyDTO> findByOffice(String office) {
		return specialtyMapper.mapToDtoList(specialtyRepository.findByOffice(office));
	}

	@Override
	public List<SpecialtyDTO> findAll() {
		return specialtyMapper.mapToDtoList(specialtyRepository.findAll());
	}

	private Specialty findEntity(Integer id) throws SpecialtyNotFoundException {
		return specialtyRepository.findById(id)
				.orElseThrow(() -> new SpecialtyNotFoundException("Record not found...!"));
	}
}
