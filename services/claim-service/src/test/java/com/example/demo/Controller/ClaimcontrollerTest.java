package com.example.demo.Controller;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class ClaimcontrollerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetStatus() throws Exception {

        mockMvc.perform(
                get("/claims/1/status"))
                .andExpect(status().isOk());
    }
}