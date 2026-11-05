package com.tecsup.petclinic.webs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tecsup.petclinic.dtos.OwnerDTO;
import com.tecsup.petclinic.utils.OwnerObjectCreator;
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
 * Pruebas de integración para OwnerController (/owners)
 * Integrantes A y B — Listado, consulta y creación
 * Integrante C — Actualización y borrado
 */
@AutoConfigureMockMvc
@SpringBootTest
@Slf4j
public class OwnerControllerTest {

    private static final ObjectMapper om = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    /**
     * GET /owners -> 200 OK, JSON, primer registro con id = 1
     */
    @Test
    public void testFindAllOwners() throws Exception {

        final int ID_FIRST_RECORD = 1;

        this.mockMvc.perform(get("/owners"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$[0].id", is(ID_FIRST_RECORD)));
    }

    /**
     * GET /owners/1 -> 200 OK, lastName = "Franklin"
     */
    @Test
    public void testFindOwnerOK() throws Exception {

        final int OWNER_ID = 1;
        final String FIRST_NAME = "George";
        final String LAST_NAME = "Franklin";
        final String CITY = "Madison";

        this.mockMvc.perform(get("/owners/" + OWNER_ID))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(OWNER_ID)))
                .andExpect(jsonPath("$.firstName", is(FIRST_NAME)))
                .andExpect(jsonPath("$.lastName", is(LAST_NAME)))
                .andExpect(jsonPath("$.city", is(CITY)));
    }

    /**
     * GET /owners/666 -> 404 Not Found
     */
    @Test
    public void testFindOwnerKO() throws Exception {

        this.mockMvc.perform(get("/owners/666"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void testCreateOwner() throws Exception {
        this.mockMvc.perform(post("/owners")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"firstName":"Ana","lastName":"Torres","address":"123 Main St",
                                 "city":"Madison","telephone":"6085550199"}
                                """))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.firstName").value("Ana"))
                .andExpect(jsonPath("$.lastName").value("Torres"))
                .andExpect(jsonPath("$.address").value("123 Main St"))
                .andExpect(jsonPath("$.city").value("Madison"))
                .andExpect(jsonPath("$.telephone").value("6085550199"));
    }

    @Test
    public void testCreateOwner_BadRequest() throws Exception {
        this.mockMvc.perform(post("/owners")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"firstName":"Ana","address":"123 Main St",
                                 "city":"Madison","telephone":"6085550199"}
                                """))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testFindByLastName() throws Exception {
        this.mockMvc.perform(get("/owners").param("lastName", "Davis"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].lastName").value("Davis"))
                .andExpect(jsonPath("$[1].lastName").value("Davis"));
    }

    /**
     * Integrante C: POST + PUT + GET confirma la actualización en H2.
     */
    @Test
    @Transactional
    public void testUpdateOwner() throws Exception {
        OwnerDTO newOwner = OwnerObjectCreator.getOwnerTO();

        String response = mockMvc.perform(post("/owners")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(newOwner)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andReturn().getResponse().getContentAsString();

        Long id = om.readValue(response, OwnerDTO.class).getId();
        OwnerDTO updatedOwner = OwnerObjectCreator.getUpdatedOwnerTO();
        updatedOwner.setId(id);

        mockMvc.perform(put("/owners/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(updatedOwner)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(om.writeValueAsString(updatedOwner)));

        mockMvc.perform(get("/owners/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(id.intValue()))
                .andExpect(jsonPath("$.firstName").value(updatedOwner.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(updatedOwner.getLastName()))
                .andExpect(jsonPath("$.address").value(updatedOwner.getAddress()))
                .andExpect(jsonPath("$.city").value(updatedOwner.getCity()))
                .andExpect(jsonPath("$.telephone").value(updatedOwner.getTelephone()));
    }

    /**
     * Integrante C: crea un propietario propio, lo borra y confirma su ausencia.
     */
    @Test
    @Transactional
    public void testDeleteOwner() throws Exception {
        OwnerDTO newOwner = OwnerObjectCreator.getOwnerTO();

        String response = mockMvc.perform(post("/owners")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(newOwner)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andReturn().getResponse().getContentAsString();

        Long id = om.readValue(response, OwnerDTO.class).getId();

        mockMvc.perform(delete("/owners/{id}", id))
                .andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(get("/owners/{id}", id))
                .andExpect(status().isNotFound());
    }
}
