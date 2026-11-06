package com.tecsup.petclinic.repositories;

import com.tecsup.petclinic.entities.Vet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio de Vet
 */
@Repository
public interface VetRepository extends JpaRepository<Vet, Integer> {

	// Buscar vets activos o inactivos
	List<Vet> findByActive(Boolean active);
}
