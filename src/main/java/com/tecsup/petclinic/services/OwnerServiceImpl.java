package com.tecsup.petclinic.services;

import com.tecsup.petclinic.dtos.OwnerDTO;
import com.tecsup.petclinic.entities.Owner;
import com.tecsup.petclinic.exceptions.OwnerNotFoundException;
import com.tecsup.petclinic.mapper.OwnerMapper;
import com.tecsup.petclinic.repositories.OwnerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementación del servicio de Owner
 */
@Service
@Slf4j
public class OwnerServiceImpl implements OwnerService {

	private final OwnerRepository ownerRepository;
	private final OwnerMapper ownerMapper;

	public OwnerServiceImpl(OwnerRepository ownerRepository, OwnerMapper ownerMapper) {
		this.ownerRepository = ownerRepository;
		this.ownerMapper = ownerMapper;
	}

	@Override
	public OwnerDTO create(OwnerDTO ownerDTO) {
		Owner newOwner = ownerRepository.save(ownerMapper.mapToEntity(ownerDTO));
		return ownerMapper.mapToDto(newOwner);
	}

	@Override
	public OwnerDTO update(OwnerDTO ownerDTO) {
		Owner updated = ownerRepository.save(ownerMapper.mapToEntity(ownerDTO));
		return ownerMapper.mapToDto(updated);
	}

	@Override
	public void delete(Long id) throws OwnerNotFoundException {
		OwnerDTO owner = findById(id);
		ownerRepository.delete(ownerMapper.mapToEntity(owner));
	}

	@Override
	public OwnerDTO findById(Long id) throws OwnerNotFoundException {
		Optional<Owner> owner = ownerRepository.findById(id);
		if (owner.isEmpty())
			throw new OwnerNotFoundException("Record not found...!");
		return ownerMapper.mapToDto(owner.get());
	}

	@Override
	public List<OwnerDTO> findByLastName(String lastName) {
		List<Owner> owners = ownerRepository.findByLastName(lastName);
		owners.forEach(owner -> log.info("" + owner));
		return ownerMapper.mapToDtoList(owners);
	}

	@Override
	public List<OwnerDTO> findAll() {
		return ownerMapper.mapToDtoList(ownerRepository.findAll());
	}
}
