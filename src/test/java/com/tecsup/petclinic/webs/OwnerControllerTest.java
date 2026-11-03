package com.tecsup.petclinic.webs;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de integración para OwnerController (/owners)
 * Integrante A — Listado y consulta
 */
@AutoConfigureMockMvc
@SpringBootTest
@Slf4j
public class OwnerControllerTest {

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
}
