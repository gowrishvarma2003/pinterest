package com.infy.pintrest.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class CategoryControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new CategoryController()).build();
    }
    
    @Nested
    @DisplayName("GET /categories - Get All Categories Tests")
    class GetAllCategoriesTests {

        @Test
        @DisplayName("Should get all categories successfully")
        void getAllCategories_Success() throws Exception {
            mockMvc.perform(get("/categories"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(18))
                    .andExpect(jsonPath("$[0]").value("Travel"))
                    .andExpect(jsonPath("$[1]").value("Food"))
                    .andExpect(jsonPath("$[2]").value("Fitness"));
        }

        @Test
        @DisplayName("Should return all category display names")
        void getAllCategories_ContainsAllCategories_Success() throws Exception {
            mockMvc.perform(get("/categories"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[?(@=='Travel')]").exists())
                    .andExpect(jsonPath("$[?(@=='Food')]").exists())
                    .andExpect(jsonPath("$[?(@=='Fitness')]").exists())
                    .andExpect(jsonPath("$[?(@=='Technology')]").exists())
                    .andExpect(jsonPath("$[?(@=='Fashion')]").exists())
                    .andExpect(jsonPath("$[?(@=='Photography')]").exists())
                    .andExpect(jsonPath("$[?(@=='DIY')]").exists())
                    .andExpect(jsonPath("$[?(@=='Motivation')]").exists())
                    .andExpect(jsonPath("$[?(@=='Art')]").exists())
                    .andExpect(jsonPath("$[?(@=='Music')]").exists())
                    .andExpect(jsonPath("$[?(@=='Gaming')]").exists())
                    .andExpect(jsonPath("$[?(@=='Beauty')]").exists())
                    .andExpect(jsonPath("$[?(@=='Home Decor')]").exists())
                    .andExpect(jsonPath("$[?(@=='Nature')]").exists())
                    .andExpect(jsonPath("$[?(@=='Sports')]").exists())
                    .andExpect(jsonPath("$[?(@=='Education')]").exists())
                    .andExpect(jsonPath("$[?(@=='Lifestyle')]").exists())
                    .andExpect(jsonPath("$[?(@=='Business')]").exists());
        }

        @Test
        @DisplayName("Should return non-empty list")
        void getAllCategories_NonEmpty_Success() throws Exception {
            mockMvc.perform(get("/categories"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isNotEmpty());
        }

        @Test
        @DisplayName("Should return categories as strings")
        void getAllCategories_StringValues_Success() throws Exception {
            mockMvc.perform(get("/categories"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0]").isString());
        }
    }

}
