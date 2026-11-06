package com.tecsup.petclinic.webs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tecsup.petclinic.dtos.SpecialtyDTO;
import com.tecsup.petclinic.utils.SpecialtyObjectCreator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Ejercicio 3 — Pruebas de integración de SpecialtyController (/specialties).
 * Sin mocks: controlador -> servicio -> repositorio -> H2 (datos de data.sql).
 * Las pruebas que modifican datos usan @Transactional para revertir los cambios.
 */
@AutoConfigureMockMvc
@SpringBootTest
@Slf4j
public class SpecialtyControllerTest {

    private static final ObjectMapper om = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    // ---------- Listado y consulta ----------

    /** GET /specialties -> 200 OK, lista JSON con las 3 especialidades */
    @Test
    public void testFindAllSpecialties() throws Exception {
        mockMvc.perform(get("/specialties"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()", is(3)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("radiology")));
    }

    /** GET /specialties/1 -> 200 OK, name = radiology */
    @Test
    public void testFindSpecialtyOK() throws Exception {
        mockMvc.perform(get("/specialties/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("radiology")))
                .andExpect(jsonPath("$.office", is("Farewell")))
                .andExpect(jsonPath("$.openHour", is(8)))
                .andExpect(jsonPath("$.closeHour", is(18)));
    }

    /** GET /specialties/666 -> 404 Not Found */
    @Test
    public void testFindSpecialtyKO() throws Exception {
        mockMvc.perform(get("/specialties/666"))
                .andExpect(status().isNotFound());
    }

    // ---------- Creación y búsqueda ----------

    /** POST /specialties -> 201 Created, con los campos enviados */
    @Test
    @Transactional
    public void testCreateSpecialty() throws Exception {
        SpecialtyDTO newSpecialty = SpecialtyObjectCreator.getNewSpecialtyTO();

        mockMvc.perform(post("/specialties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(newSpecialty)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name", is(newSpecialty.getName())))
                .andExpect(jsonPath("$.office", is(newSpecialty.getOffice())))
                .andExpect(jsonPath("$.openHour", is(newSpecialty.getOpenHour())))
                .andExpect(jsonPath("$.closeHour", is(newSpecialty.getCloseHour())));
    }

    /** GET /specialties?name=surgery -> la especialidad surgery */
    @Test
    public void testFindByName() throws Exception {
        mockMvc.perform(get("/specialties").param("name", "surgery"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)))
                .andExpect(jsonPath("$[0].id", is(2)))
                .andExpect(jsonPath("$[0].name", is("surgery")))
                .andExpect(jsonPath("$[0].office", is("Maryland")));
    }

    /** GET /specialties?office=Farewell -> radiology */
    @Test
    public void testFindByOffice() throws Exception {
        mockMvc.perform(get("/specialties").param("office", "Farewell"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)))
                .andExpect(jsonPath("$[0].name", is("radiology")))
                .andExpect(jsonPath("$[0].office", is("Farewell")));
    }

    // ---------- Validaciones y borrado ----------

    /** POST con h_open >= h_close -> 400 Bad Request */
    @Test
    public void testCreateSpecialty_InvalidSchedule() throws Exception {
        mockMvc.perform(post("/specialties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(SpecialtyObjectCreator.getInvalidScheduleTO())))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    /** POST con horas fuera de 0-23 -> 400 Bad Request */
    @Test
    public void testCreateSpecialty_InvalidHourRange() throws Exception {
        mockMvc.perform(post("/specialties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(SpecialtyObjectCreator.getInvalidHourRangeTO())))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    /** POST + DELETE /specialties/{id} -> 200 OK, y luego GET da 404 */
    @Test
    @Transactional
    public void testDeleteSpecialty() throws Exception {
        String response = mockMvc.perform(post("/specialties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(SpecialtyObjectCreator.getNewSpecialtyTO())))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Integer id = om.readValue(response, SpecialtyDTO.class).getId();

        mockMvc.perform(delete("/specialties/{id}", id))
                .andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(get("/specialties/{id}", id))
                .andExpect(status().isNotFound());
    }
}
