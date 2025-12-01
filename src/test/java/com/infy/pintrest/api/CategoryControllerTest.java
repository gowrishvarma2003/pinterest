package com.infy.pintrest.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CategoryController.class)
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

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

    @Nested
    @DisplayName("Invalid Request Tests")
    class InvalidRequestTests {

        @Test
        @DisplayName("Should fail with POST method")
        void categories_PostMethod_Failure() throws Exception {
            mockMvc.perform(post("/categories"))
                    .andExpect(status().isMethodNotAllowed());
        }

        @Test
        @DisplayName("Should fail with PUT method")
        void categories_PutMethod_Failure() throws Exception {
            mockMvc.perform(put("/categories"))
                    .andExpect(status().isMethodNotAllowed());
        }

        @Test
        @DisplayName("Should fail with DELETE method")
        void categories_DeleteMethod_Failure() throws Exception {
            mockMvc.perform(delete("/categories"))
                    .andExpect(status().isMethodNotAllowed());
        }
    }
}
