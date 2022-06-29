package com.rafaelamaral.dscatalog.resources.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rafaelamaral.dscatalog.dto.CategoryDTO;
import com.rafaelamaral.dscatalog.factory.Factory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class CategoryResourceIntegrationTests {

    static final String CATEGORY_PATH = "/categories";

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    private Long existingId;
    private Long nonExistingId;
    private Long countTotalCategory;

    @BeforeEach
    public void setUp(){
        existingId = 1L;
        nonExistingId = 1000L;
        countTotalCategory = 3L;
    }

    @Test
    @DisplayName("Find all paged should return sorted page when sort page by name")
    public void findAllPagedShouldReturnSortedPageWhenSortPageByName() throws Exception{
        ResultActions result =
                         mockMvc.perform(get(CATEGORY_PATH.concat("?page=0&size=10&sort=name,asc"))
                                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.totalElements").value(countTotalCategory));
        result.andExpect(jsonPath("$.content").exists());
        result.andExpect(jsonPath("$.content[0].name").value("Computadores"));
        result.andExpect(jsonPath("$.content[1].name").value("Eletrônicos"));
        result.andExpect(jsonPath("$.content[2].name").value("Livros"));

    }

    @Test
    @DisplayName("Update should return category dto when id exist")
    public void updateShouldReturnCategoryDTOWhenIdExist() throws Exception{
        CategoryDTO categoryDTO = Factory.createCategoryDTO();

        String expectedName = categoryDTO.getName();
        String json = objectMapper.writeValueAsString(categoryDTO);

        ResultActions result =
                        mockMvc.perform(put(CATEGORY_PATH.concat("/" + existingId))
                                .content(json)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").isNotEmpty());
        result.andExpect(jsonPath("$.name").value(expectedName));
    }

    @Test
    @DisplayName("Update should return not found when id does not exist")
    public void updateShouldReturnNotFoundWhenIdDoesNotExist() throws Exception{
        CategoryDTO categoryDTO = Factory.createCategoryDTO();

        String json = objectMapper.writeValueAsString(categoryDTO);

        ResultActions result =
                     mockMvc.perform(put(CATEGORY_PATH.concat("/" + nonExistingId))
                            .content(json)
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());
        result.andExpect(jsonPath("$.timesStamp").isNotEmpty());
        result.andExpect(jsonPath("$.status").value("404"));
        result.andExpect(jsonPath("$.error").value("Resource not found"));
        result.andExpect(jsonPath("$.message").isNotEmpty());
        result.andExpect(jsonPath("$.path").isNotEmpty());

    }

}
