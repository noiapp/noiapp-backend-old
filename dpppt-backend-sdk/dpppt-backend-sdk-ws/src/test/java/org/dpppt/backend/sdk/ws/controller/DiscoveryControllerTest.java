package org.dpppt.backend.sdk.ws.controller;

import org.apache.commons.io.IOUtils;
import org.dpppt.backend.sdk.ws.Application;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("dev")
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
public class DiscoveryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldGetDiscoveryFile() throws Exception {
        mockMvc.perform(get("/discovery/discovery.json")).andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(content().bytes(IOUtils.toByteArray(new ClassPathResource("discovery.json").getInputStream())));
    }


}
