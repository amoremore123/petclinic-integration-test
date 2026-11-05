package com.tecsup.petclinic.webs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tecsup.petclinic.dtos.OwnerDTO;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integrante C — Actualización y borrado con OwnerService mockeado.
 */
@AutoConfigureMockMvc
@SpringBootTest
public class OwnerControllerMockitoTest {

    private static final ObjectMapper om = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OwnerService ownerService;

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
}
