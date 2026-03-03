package com.example.demo.Controller;

import com.example.demo.entity.ClaimStatus;
import com.example.demo.service.ClaimWorkflowservice;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ClaimControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClaimWorkflowservice service;

    @Test
    void testGetStatus() throws Exception {
        // Mock the service response
        Mockito.when(service.getStatus(1L)).thenReturn(ClaimStatus.SUBMITTED);

        mockMvc.perform(get("/claims/1/status"))
                .andExpect(status().isOk())
                .andExpect(content().string("\"SUBMITTED\""));
    }
}