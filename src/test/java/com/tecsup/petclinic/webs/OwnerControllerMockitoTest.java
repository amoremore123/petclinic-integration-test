package com.tecsup.petclinic.webs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tecsup.petclinic.dtos.OwnerDTO;
import com.tecsup.petclinic.exceptions.OwnerNotFoundException;
import com.tecsup.petclinic.services.OwnerService;
import com.tecsup.petclinic.utils.OwnerObjectCreator;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Grupo 1 — Pruebas de OwnerController con OwnerService mockeado.
 */
@AutoConfigureMockMvc
@SpringBootTest
public class OwnerControllerMockitoTest {

    private static final ObjectMapper om = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OwnerService ownerService;

    // Integrante A — Listado y consulta

    @Test
    public void testFindAllOwners() throws Exception {
        List<OwnerDTO> owners = OwnerObjectCreator.getOwnersTO();
        Mockito.when(ownerService.findAll()).thenReturn(owners);

        mockMvc.perform(get("/owners"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(owners.size()))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(content().json(om.writeValueAsString(owners)));

        Mockito.verify(ownerService).findAll();
        Mockito.verifyNoMoreInteractions(ownerService);
    }

    @Test
    public void testFindOwnerOK() throws Exception {
        OwnerDTO owner = OwnerObjectCreator.getFranklinOwnerTO();
        Mockito.when(ownerService.findById(1L)).thenReturn(owner);

        mockMvc.perform(get("/owners/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value(owner.getFirstName()))
                .andExpect(jsonPath("$.lastName").value("Franklin"))
                .andExpect(jsonPath("$.address").value(owner.getAddress()))
                .andExpect(jsonPath("$.city").value(owner.getCity()))
                .andExpect(jsonPath("$.telephone").value(owner.getTelephone()));

        Mockito.verify(ownerService).findById(1L);
        Mockito.verifyNoMoreInteractions(ownerService);
    }

    @Test
    public void testFindOwnerKO() throws Exception {
        final Long id = 666L;
        Mockito.when(ownerService.findById(id))
                .thenThrow(new OwnerNotFoundException("Record not found...!"));

        mockMvc.perform(get("/owners/{id}", id))
                .andExpect(status().isNotFound());

        Mockito.verify(ownerService).findById(id);
        Mockito.verifyNoMoreInteractions(ownerService);
    }

    // Integrante C — Actualización y borrado

    @Test
    public void testUpdateOwner() throws Exception {
        final Long id = 10000L;
        OwnerDTO newOwner = OwnerObjectCreator.getOwnerTO();
        OwnerDTO createdOwner = OwnerObjectCreator.getOwnerTO();
        createdOwner.setId(id);
        OwnerDTO updatedOwner = OwnerObjectCreator.getUpdatedOwnerTO();
        updatedOwner.setId(id);

        Mockito.when(ownerService.create(newOwner)).thenReturn(createdOwner);
        Mockito.when(ownerService.findById(id)).thenReturn(createdOwner, updatedOwner);
        Mockito.when(ownerService.update(updatedOwner)).thenReturn(updatedOwner);

        String response = mockMvc.perform(post("/owners")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(newOwner)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.intValue()))
                .andReturn().getResponse().getContentAsString();

        Long createdId = om.readValue(response, OwnerDTO.class).getId();

        mockMvc.perform(put("/owners/{id}", createdId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(updatedOwner)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(om.writeValueAsString(updatedOwner)));

        mockMvc.perform(get("/owners/{id}", createdId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(id.intValue()))
                .andExpect(jsonPath("$.firstName").value(updatedOwner.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(updatedOwner.getLastName()))
                .andExpect(jsonPath("$.address").value(updatedOwner.getAddress()))
                .andExpect(jsonPath("$.city").value(updatedOwner.getCity()))
                .andExpect(jsonPath("$.telephone").value(updatedOwner.getTelephone()));

        Mockito.verify(ownerService).create(newOwner);
        Mockito.verify(ownerService).update(updatedOwner);
        Mockito.verify(ownerService, Mockito.times(2)).findById(id);
        Mockito.verifyNoMoreInteractions(ownerService);
    }

    @Test
    public void testDeleteOwner() throws Exception {
        final Long id = 10001L;
        OwnerDTO newOwner = OwnerObjectCreator.getOwnerTO();
        OwnerDTO createdOwner = OwnerObjectCreator.getOwnerTO();
        createdOwner.setId(id);

        Mockito.when(ownerService.create(newOwner)).thenReturn(createdOwner);
        Mockito.doNothing().when(ownerService).delete(id);

        String response = mockMvc.perform(post("/owners")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(newOwner)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.intValue()))
                .andReturn().getResponse().getContentAsString();

        Long createdId = om.readValue(response, OwnerDTO.class).getId();

        mockMvc.perform(delete("/owners/{id}", createdId))
                .andDo(print())
                .andExpect(status().isOk());

        Mockito.when(ownerService.findById(id))
                .thenThrow(new OwnerNotFoundException("Record not found...!"));

        mockMvc.perform(get("/owners/{id}", createdId))
                .andExpect(status().isNotFound());

        Mockito.verify(ownerService).create(newOwner);
        Mockito.verify(ownerService).delete(id);
        Mockito.verify(ownerService).findById(id);
        Mockito.verifyNoMoreInteractions(ownerService);
    }

    @Test
    public void testDeleteOwnerKO() throws Exception {
        final Long id = 1000L;

        Mockito.doThrow(new OwnerNotFoundException("Record not found...!"))
                .when(ownerService).delete(id);

        mockMvc.perform(delete("/owners/{id}", id))
                .andExpect(status().isNotFound());

        Mockito.verify(ownerService).delete(id);
        Mockito.verifyNoMoreInteractions(ownerService);
    }
}
