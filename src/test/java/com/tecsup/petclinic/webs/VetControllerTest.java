package com.tecsup.petclinic.webs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tecsup.petclinic.dtos.VetDTO;
import com.tecsup.petclinic.utils.VetObjectCreator;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Ejercicio 1 — Pruebas de integración de VetController (/vets).
 * Sin mocks: controlador -> servicio -> repositorio -> H2 (datos de data.sql).
 * Las pruebas que modifican datos usan @Transactional para revertir los cambios.
 */
@AutoConfigureMockMvc
@SpringBootTest
@Slf4j
public class VetControllerTest {

    private static final ObjectMapper om = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    // ---------- Listado y consulta ----------

    /** GET /vets -> 200 OK, lista JSON con los 6 vets */
    @Test
    public void testFindAllVets() throws Exception {
        mockMvc.perform(get("/vets"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()", is(6)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].lastName", is("Carter")));
    }

    /** GET /vets/1 -> 200 OK, lastName = Carter */
    @Test
    public void testFindVetOK() throws Exception {
        mockMvc.perform(get("/vets/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstName", is("James")))
                .andExpect(jsonPath("$.lastName", is("Carter")))
                .andExpect(jsonPath("$.active", is(true)));
    }

    /** GET /vets/666 -> 404 Not Found */
    @Test
    public void testFindVetKO() throws Exception {
        mockMvc.perform(get("/vets/666"))
                .andExpect(status().isNotFound());
    }

    // ---------- Creación y soft delete ----------

    /** POST /vets -> 201 Created, con los campos enviados y activo por defecto */
    @Test
    @Transactional
    public void testCreateVet() throws Exception {
        VetDTO newVet = VetObjectCreator.getNewVetTO();

        mockMvc.perform(post("/vets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(newVet)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.firstName", is(newVet.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(newVet.getLastName())))
                .andExpect(jsonPath("$.email", is(newVet.getEmail())))
                .andExpect(jsonPath("$.active", is(true)));
    }

    /** PUT /vets/1/deactivate -> 200 OK, active = false */
    @Test
    @Transactional
    public void testDeactivateVet() throws Exception {
        mockMvc.perform(put("/vets/1/deactivate"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.active", is(false)));

        mockMvc.perform(get("/vets/1"))
                .andExpect(jsonPath("$.active", is(false)));
    }

    /** PUT /vets/6/activate -> el vet 6 (inactivo) queda active = true */
    @Test
    @Transactional
    public void testReactivateVet() throws Exception {
        mockMvc.perform(put("/vets/6/activate"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(6)))
                .andExpect(jsonPath("$.lastName", is("Jenkins")))
                .andExpect(jsonPath("$.active", is(true)));
    }

    // ---------- Filtros y borrado ----------

    /** GET /vets?active=true -> solo los 5 activos */
    @Test
    public void testFindActiveVets() throws Exception {
        mockMvc.perform(get("/vets").param("active", "true"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(5)))
                .andExpect(jsonPath("$[*].active", org.hamcrest.Matchers.everyItem(is(true))));
    }

    /** GET /vets?active=false -> solo 1 inactivo (Jenkins) */
    @Test
    public void testFindInactiveVets() throws Exception {
        mockMvc.perform(get("/vets").param("active", "false"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)))
                .andExpect(jsonPath("$[0].id", is(6)))
                .andExpect(jsonPath("$[0].lastName", is("Jenkins")))
                .andExpect(jsonPath("$[0].active", is(false)));
    }

    /** DELETE /vets/1000 -> 404 Not Found */
    @Test
    public void testDeleteVetKO() throws Exception {
        mockMvc.perform(delete("/vets/1000"))
                .andExpect(status().isNotFound());
    }
}
