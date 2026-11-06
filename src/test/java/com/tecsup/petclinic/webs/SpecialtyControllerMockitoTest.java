package com.tecsup.petclinic.webs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tecsup.petclinic.dtos.SpecialtyDTO;
import com.tecsup.petclinic.exceptions.SpecialtyNotFoundException;
import com.tecsup.petclinic.services.SpecialtyService;
import com.tecsup.petclinic.utils.SpecialtyObjectCreator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Ejercicio 3 — Pruebas de SpecialtyController con SpecialtyService mockeado.
 * Solo se prueba la lógica del controlador y el contrato JSON, sin tocar la BD.
 */
@AutoConfigureMockMvc
@SpringBootTest
@Slf4j
public class SpecialtyControllerMockitoTest {

    private static final ObjectMapper om = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SpecialtyService specialtyService;

    // ---------- Listado y consulta ----------

    @Test
    public void testFindAllSpecialties() throws Exception {
        List<SpecialtyDTO> specialties = SpecialtyObjectCreator.getSpecialtiesTO();
        Mockito.when(specialtyService.findAll()).thenReturn(specialties);

        mockMvc.perform(get("/specialties"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()", is(3)))
                .andExpect(content().json(om.writeValueAsString(specialties)));

        Mockito.verify(specialtyService).findAll();
    }

    @Test
    public void testFindSpecialtyOK() throws Exception {
        SpecialtyDTO radiology = SpecialtyObjectCreator.getRadiologyTO();
        Mockito.when(specialtyService.findById(1)).thenReturn(radiology);

        mockMvc.perform(get("/specialties/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("radiology")))
                .andExpect(jsonPath("$.office", is(radiology.getOffice())));
    }

    @Test
    public void testFindSpecialtyKO() throws Exception {
        Mockito.when(specialtyService.findById(666))
                .thenThrow(new SpecialtyNotFoundException("Record not found...!"));

        mockMvc.perform(get("/specialties/666"))
                .andExpect(status().isNotFound());
    }

    // ---------- Creación y búsqueda ----------

    @Test
    public void testCreateSpecialty() throws Exception {
        SpecialtyDTO newSpecialty = SpecialtyObjectCreator.getNewSpecialtyTO();
        SpecialtyDTO created = SpecialtyObjectCreator.getNewSpecialtyTO();
        created.setId(4);

        Mockito.when(specialtyService.create(ArgumentMatchers.any(SpecialtyDTO.class))).thenReturn(created);

        mockMvc.perform(post("/specialties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(newSpecialty)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(4)))
                .andExpect(jsonPath("$.name", is(newSpecialty.getName())))
                .andExpect(jsonPath("$.openHour", is(newSpecialty.getOpenHour())))
                .andExpect(jsonPath("$.closeHour", is(newSpecialty.getCloseHour())));
    }

    @Test
    public void testFindByName() throws Exception {
        Mockito.when(specialtyService.findByName("surgery"))
                .thenReturn(List.of(SpecialtyObjectCreator.getSurgeryTO()));

        mockMvc.perform(get("/specialties").param("name", "surgery"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)))
                .andExpect(jsonPath("$[0].name", is("surgery")));

        Mockito.verify(specialtyService).findByName("surgery");
    }

    @Test
    public void testFindByOffice() throws Exception {
        Mockito.when(specialtyService.findByOffice("Farewell"))
                .thenReturn(List.of(SpecialtyObjectCreator.getRadiologyTO()));

        mockMvc.perform(get("/specialties").param("office", "Farewell"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)))
                .andExpect(jsonPath("$[0].name", is("radiology")));

        Mockito.verify(specialtyService).findByOffice("Farewell");
    }

    // ---------- Validaciones y borrado ----------

    @Test
    public void testCreateSpecialty_InvalidSchedule() throws Exception {
        mockMvc.perform(post("/specialties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(SpecialtyObjectCreator.getInvalidScheduleTO())))
                .andExpect(status().isBadRequest());

        // El controlador rechaza el pedido sin llamar al servicio
        Mockito.verify(specialtyService, Mockito.never()).create(ArgumentMatchers.any());
    }

    @Test
    public void testCreateSpecialty_InvalidHourRange() throws Exception {
        mockMvc.perform(post("/specialties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(SpecialtyObjectCreator.getInvalidHourRangeTO())))
                .andExpect(status().isBadRequest());

        Mockito.verify(specialtyService, Mockito.never()).create(ArgumentMatchers.any());
    }

    @Test
    public void testDeleteSpecialty() throws Exception {
        SpecialtyDTO created = SpecialtyObjectCreator.getNewSpecialtyTO();
        created.setId(4);
        Mockito.when(specialtyService.create(ArgumentMatchers.any(SpecialtyDTO.class))).thenReturn(created);
        Mockito.doNothing().when(specialtyService).delete(4);

        mockMvc.perform(post("/specialties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(SpecialtyObjectCreator.getNewSpecialtyTO())))
                .andExpect(status().isCreated());

        mockMvc.perform(delete("/specialties/4"))
                .andDo(print())
                .andExpect(status().isOk());

        Mockito.verify(specialtyService).delete(4);
    }
}
