package com.tecsup.petclinic.webs;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("h2")
@Transactional
class VisitControllerTest {
    private static final String VISIT = """
            {"petId":7,"vetId":2,"visitDate":"2025-01-15","description":"checkup","cost":55.25}
            """;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper json;

    @Test
    void testFindAllVisits() throws Exception {
        mvc.perform(get("/visits"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(6));
    }

    @Test
    void testFindVisitOK() throws Exception {
        mvc.perform(get("/visits/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("rabies shot"));
    }

    @Test
    void testFindVisitKO() throws Exception {
        mvc.perform(get("/visits/666"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateVisit() throws Exception {
        mvc.perform(post("/visits").contentType(MediaType.APPLICATION_JSON).content(VISIT))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.petId").value(7))
                .andExpect(jsonPath("$.vetId").value(2))
                .andExpect(jsonPath("$.visitDate").value("2025-01-15"))
                .andExpect(jsonPath("$.description").value("checkup"))
                .andExpect(jsonPath("$.cost").value(55.25));
    }

    @Test
    void testFindByPetId() throws Exception {
        mvc.perform(get("/visits").param("petId", "7"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id", containsInAnyOrder(1, 4)));
    }

    @Test
    void testFindByVetId() throws Exception {
        mvc.perform(get("/visits").param("vetId", "2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id", containsInAnyOrder(1, 2)));
    }

    @Test
    void testCalculateTotalCostByPet() throws Exception {
        mvc.perform(get("/visits/pet/7/total-cost"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(comparesEqualTo(new BigDecimal("320.00")), BigDecimal.class));
    }

    @Test
    void testFindByDateBetween() throws Exception {
        mvc.perform(get("/visits").param("from", "2024-01-01").param("to", "2024-12-31"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id", containsInAnyOrder(5, 6)));
    }

    @Test
    void testFindByVetIdAndDateBetween() throws Exception {
        mvc.perform(get("/visits").param("vetId", "2").param("from", "2011-03-04").param("to", "2011-03-04"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id", containsInAnyOrder(2)));
    }

    @Test
    void testCountVisitsByPet() throws Exception {
        mvc.perform(get("/visits/pet/7/count"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(2));
    }

    @Test
    void testUpdateVisit() throws Exception {
        long id = createVisit();
        mvc.perform(put("/visits/{id}", id).contentType(MediaType.APPLICATION_JSON)
                        .content(VISIT.replace("checkup", "follow-up")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("follow-up"));
        mvc.perform(get("/visits/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("follow-up"));
    }

    @Test
    void testDeleteVisit() throws Exception {
        long id = createVisit();
        mvc.perform(delete("/visits/{id}", id))
                .andDo(print())
                .andExpect(status().isOk());
        mvc.perform(get("/visits/{id}", id)).andExpect(status().isNotFound());
    }

    private long createVisit() throws Exception {
        String body = mvc.perform(post("/visits").contentType(MediaType.APPLICATION_JSON).content(VISIT))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return json.readTree(body).get("id").asLong();
    }
}
