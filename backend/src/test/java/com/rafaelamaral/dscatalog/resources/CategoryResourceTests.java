package com.rafaelamaral.dscatalog.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rafaelamaral.dscatalog.dto.CategoryDTO;
import com.rafaelamaral.dscatalog.factory.Factory;
import com.rafaelamaral.dscatalog.services.CategoryService;
import com.rafaelamaral.dscatalog.services.exceptions.DataBaseException;
import com.rafaelamaral.dscatalog.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryResource.class)
public class CategoryResourceTests {

    static String CATEGORY_PATH = "/categories";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    private Long existingId;
    private Long nonExistingId;
    private Long dependentId;
    private CategoryDTO categoryDTO;
    private PageImpl<CategoryDTO> page;


    @BeforeEach
    public void setUp(){
        existingId = 1L;
        nonExistingId = 100L;
        dependentId = 4L;
        categoryDTO = Factory.createCategoryDTO();
        page = new PageImpl<>(List.of(categoryDTO));


        when(categoryService.findAllPaged(any())).thenReturn(page);

        when(categoryService.findById(existingId)).thenReturn(categoryDTO);
        when(categoryService.findById(nonExistingId)).thenThrow(new ResourceNotFoundException("Entity not found"));

        when(categoryService.update(eq(existingId) , any())).thenReturn(categoryDTO);
        when(categoryService.update(eq(nonExistingId), any())).thenThrow(new ResourceNotFoundException("Entity not found"));

        when(categoryService.save(categoryDTO)).thenReturn(categoryDTO);

        doNothing().when(categoryService).delete(existingId);
        doThrow(new ResourceNotFoundException("Entity not found")).when(categoryService).delete(nonExistingId);
        doThrow(new DataBaseException("Data base exception")).when(categoryService).delete(dependentId);
    }

    @Test
    @DisplayName("Find All should return paged")
    public void findAllShouldReturnPaged() throws Exception {
        ResultActions result =
                mockMvc.perform(get(CATEGORY_PATH)
                       .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());

    }

    @Test
    @DisplayName("Find by id should return category dto when id exist")
    public void findByIdShouldReturnCategoryDTOWhenIdExist() throws Exception {
        ResultActions result =
                 mockMvc.perform(get(CATEGORY_PATH.concat("/" + existingId))
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").exists());
        result.andExpect(jsonPath("$.name").exists());
    }

    @Test
    @DisplayName("Find by id should not found when id does exist")
    public void findByShouldNotFoundWhenIdDoesExist() throws Exception {
        ResultActions result =
                 mockMvc.perform(get(CATEGORY_PATH.concat("/" + nonExistingId))
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());
        result.andExpect(jsonPath("$.timesStamp").exists());
        result.andExpect(jsonPath("$.status").exists());
        result.andExpect(jsonPath("$.error").exists());
        result.andExpect(jsonPath("$.message").exists());
        result.andExpect(jsonPath("$.path").exists());

    }

    @Test
    @DisplayName("Save should return category dto created")
    public void saveShouldReturnCategoryDTOCreated() throws Exception {
        String json = new ObjectMapper().writeValueAsString(categoryDTO);

        ResultActions result =
                     mockMvc.perform(post(CATEGORY_PATH)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(json));

        result.andExpect(status().isCreated());
        result.andExpect(jsonPath("$.id").exists());
        result.andExpect(jsonPath("$.name").exists());

    }

    @Test
    @DisplayName("Update should return categoory dto when id exist")
    public void updateShouldReturnCategoryDTOWhenIdExist() throws Exception {
        String json = new ObjectMapper().writeValueAsString(categoryDTO);

        ResultActions result =
                 mockMvc.perform(put(CATEGORY_PATH.concat("/" + existingId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(json));


        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").exists());
        result.andExpect(jsonPath("$.name").exists());

    }

    @Test
    @DisplayName("Update should return entity not found when id does not exist")
    public void updateShouldReturnEntityNotFoundWhenIdDoesNotExist() throws Exception {
        String json = new ObjectMapper().writeValueAsString(categoryDTO);

        ResultActions result =
                     mockMvc.perform(put(CATEGORY_PATH.concat("/" + nonExistingId))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(json));

        result.andExpect(status().isNotFound());
        result.andExpect(jsonPath("$.timesStamp").exists());
        result.andExpect(jsonPath("$.status").exists());
        result.andExpect(jsonPath("$.error").exists());
        result.andExpect(jsonPath("$.message").exists());
        result.andExpect(jsonPath("$.path").exists());

    }

    @Test
    @DisplayName("Delete should return no content when id exist")
    public void deleteShouldReturnNoContentWhenIdExist() throws Exception {
        ResultActions result =
                     mockMvc.perform(delete(CATEGORY_PATH.concat("/" + existingId))
                            .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNoContent());

    }

    @Test
    @DisplayName("Delete should return not found when id does nothing exist")
    public void deleteShouldNotFoundWhenIdDoesNothingExist() throws Exception {
        ResultActions result =
                     mockMvc.perform(delete(CATEGORY_PATH.concat("/" + nonExistingId))
                            .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());
        result.andExpect(jsonPath("$.timesStamp").exists());
        result.andExpect(jsonPath("$.status").exists());
        result.andExpect(jsonPath("$.error").exists());
        result.andExpect(jsonPath("$.message").exists());
        result.andExpect(jsonPath("$.path").exists());

    }

    @Test
    @DisplayName("Delete should return bad request when dependent is id")
    public void deleteShouldReturnBadRequestWhenDependentIsId() throws Exception {
        ResultActions result =
                     mockMvc.perform(delete(CATEGORY_PATH.concat("/" + dependentId))
                            .accept(MediaType.APPLICATION_JSON));


        result.andExpect(status().isBadRequest());
        result.andExpect(jsonPath("$.timesStamp").exists());
        result.andExpect(jsonPath("$.status").exists());
        result.andExpect(jsonPath("$.error").exists());
        result.andExpect(jsonPath("$.message").exists());
        result.andExpect(jsonPath("$.path").exists());
    }

}
