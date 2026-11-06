package com.tecsup.petclinic.webs;

import com.tecsup.petclinic.dtos.VisitDTO;
import com.tecsup.petclinic.entities.Visit;
import com.tecsup.petclinic.repositories.PetRepository;
import com.tecsup.petclinic.repositories.VisitRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/visits")
public class VisitController {
    private final VisitRepository visits;
    private final PetRepository pets;

    public VisitController(VisitRepository visits, PetRepository pets) {
        this.visits = visits;
        this.pets = pets;
    }

    @GetMapping
    public List<VisitDTO> findAll(@RequestParam(required = false) Integer petId,
                                  @RequestParam(required = false) Integer vetId,
                                  @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                  @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        if (from != null && to != null && from.isAfter(to)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid date range");
        }
        return visits.findAll((root, query, cb) -> {
            List<Predicate> filters = new ArrayList<>();
            if (petId != null) filters.add(cb.equal(root.get("pet").get("id"), petId));
            if (vetId != null) filters.add(cb.equal(root.get("vetId"), vetId));
            if (from != null) filters.add(cb.greaterThanOrEqualTo(root.get("visitDate"), from));
            if (to != null) filters.add(cb.lessThanOrEqualTo(root.get("visitDate"), to));
            return cb.and(filters.toArray(Predicate[]::new));
        }, Sort.by("id")).stream().map(VisitController::toDto).toList();
    }

    @GetMapping("/{id}")
    public VisitDTO findById(@PathVariable Long id) {
        return toDto(requireVisit(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VisitDTO create(@RequestBody VisitDTO body) {
        return save(new Visit(), body);
    }

    @PutMapping("/{id}")
    public VisitDTO update(@PathVariable Long id, @RequestBody VisitDTO body) {
        return save(requireVisit(id), body);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        visits.delete(requireVisit(id));
    }

    @GetMapping("/pet/{id}/total-cost")
    public BigDecimal totalCost(@PathVariable Integer id) {
        return Objects.requireNonNullElse(visits.sumCostByPetId(id), BigDecimal.ZERO);
    }

    @GetMapping("/pet/{id}/count")
    public long count(@PathVariable Integer id) {
        return visits.countByPetId(id);
    }

    private Visit requireVisit(Long id) {
        return visits.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    private VisitDTO save(Visit visit, VisitDTO body) {
        if (body.petId() == null || body.visitDate() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "petId and visitDate are required");
        }
        visit.setPet(pets.findById(body.petId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown petId")));
        visit.setVetId(body.vetId());
        visit.setVisitDate(body.visitDate());
        visit.setDescription(body.description());
        visit.setCost(body.cost());
        return toDto(visits.save(visit));
    }

    private static VisitDTO toDto(Visit visit) {
        return new VisitDTO(visit.getId(), visit.getPet().getId(), visit.getVetId(),
                visit.getVisitDate(), visit.getDescription(), visit.getCost());
    }
}
