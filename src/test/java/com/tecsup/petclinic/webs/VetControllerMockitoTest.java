package com.tecsup.petclinic.webs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tecsup.petclinic.dtos.VetDTO;
import com.tecsup.petclinic.exceptions.VetNotFoundException;
import com.tecsup.petclinic.services.VetService;
import com.tecsup.petclinic.utils.VetObjectCreator;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Ejercicio 1 — Pruebas de VetController con VetService mockeado.
 * Solo se prueba la lógica del controlador y el contrato JSON, sin tocar la BD.
 */
@AutoConfigureMockMvc
@SpringBootTest
@Slf4j
public class VetControllerMockitoTest {

    private static final ObjectMapper om = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VetService vetService;

    // ---------- Listado y consulta ----------

    @Test
    public void testFindAllVets() throws Exception {
        List<VetDTO> vets = VetObjectCreator.getVetsTO();
        Mockito.when(vetService.findAll()).thenReturn(vets);

        mockMvc.perform(get("/vets"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()", is(vets.size())))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(content().json(om.writeValueAsString(vets)));

        Mockito.verify(vetService).findAll();
    }

    @Test
    public void testFindVetOK() throws Exception {
        VetDTO vet = VetObjectCreator.getCarterVetTO();
        Mockito.when(vetService.findById(1)).thenReturn(vet);

        mockMvc.perform(get("/vets/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(vet.getId())))
                .andExpect(jsonPath("$.firstName", is(vet.getFirstName())))
                .andExpect(jsonPath("$.lastName", is("Carter")));
    }

    @Test
    public void testFindVetKO() throws Exception {
        Mockito.when(vetService.findById(666))
                .thenThrow(new VetNotFoundException("Record not found...!"));

        mockMvc.perform(get("/vets/666"))
                .andExpect(status().isNotFound());
    }

    // ---------- Creación y soft delete ----------

    @Test
    public void testCreateVet() throws Exception {
        VetDTO newVet = VetObjectCreator.getNewVetTO();
        VetDTO created = VetObjectCreator.getNewVetTO();
        created.setId(7);
        created.setActive(true);

        Mockito.when(vetService.create(ArgumentMatchers.any(VetDTO.class))).thenReturn(created);

        mockMvc.perform(post("/vets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(newVet)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(7)))
                .andExpect(jsonPath("$.firstName", is(newVet.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(newVet.getLastName())))
                .andExpect(jsonPath("$.active", is(true)));
    }

    @Test
    public void testDeactivateVet() throws Exception {
        VetDTO deactivated = VetObjectCreator.getCarterVetTO();
        deactivated.setActive(false);
        Mockito.when(vetService.setActive(1, false)).thenReturn(deactivated);

        mockMvc.perform(put("/vets/1/deactivate"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.active", is(false)));

        Mockito.verify(vetService).setActive(1, false);
    }

    @Test
    public void testReactivateVet() throws Exception {
        VetDTO reactivated = VetObjectCreator.getJenkinsVetTO();
        reactivated.setActive(true);
        Mockito.when(vetService.setActive(6, true)).thenReturn(reactivated);

        mockMvc.perform(put("/vets/6/activate"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(6)))
                .andExpect(jsonPath("$.active", is(true)));

        Mockito.verify(vetService).setActive(6, true);
    }

    // ---------- Filtros y borrado ----------

    @Test
    public void testFindActiveVets() throws Exception {
        List<VetDTO> activeVets = VetObjectCreator.getActiveVetsTO();
        Mockito.when(vetService.findByActive(true)).thenReturn(activeVets);

        mockMvc.perform(get("/vets").param("active", "true"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(5)))
                .andExpect(jsonPath("$[*].active", org.hamcrest.Matchers.everyItem(is(true))));

        Mockito.verify(vetService).findByActive(true);
    }

    @Test
    public void testFindInactiveVets() throws Exception {
        List<VetDTO> inactiveVets = List.of(VetObjectCreator.getJenkinsVetTO());
        Mockito.when(vetService.findByActive(false)).thenReturn(inactiveVets);

        mockMvc.perform(get("/vets").param("active", "false"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)))
                .andExpect(jsonPath("$[0].id", is(6)))
                .andExpect(jsonPath("$[0].active", is(false)));

        Mockito.verify(vetService).findByActive(false);
    }

    @Test
    public void testDeleteVetKO() throws Exception {
        Mockito.doThrow(new VetNotFoundException("Record not found...!"))
                .when(vetService).delete(1000);

        mockMvc.perform(delete("/vets/1000"))
                .andExpect(status().isNotFound());
    }
}
