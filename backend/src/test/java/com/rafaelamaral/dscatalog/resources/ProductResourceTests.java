package com.rafaelamaral.dscatalog.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rafaelamaral.dscatalog.dto.ProductDTO;
import com.rafaelamaral.dscatalog.factory.Factory;
import com.rafaelamaral.dscatalog.services.ProductService;
import com.rafaelamaral.dscatalog.services.exceptions.DataBaseException;
import com.rafaelamaral.dscatalog.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductResource.class)
public class ProductResourceTests {

    static String PRODUCTS_PATH = "/products";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    private ProductDTO productDTO;
    private PageImpl<ProductDTO> page;
    private long existingId;
    private long nonExistingId;
    private long dependentId;

    @BeforeEach
    public void setUp(){
        existingId = 1L;
        nonExistingId = 100L;
        dependentId = 4L;
        productDTO = Factory.createProductDTO();
        page = new PageImpl<>(List.of(productDTO));

        Mockito.when(productService.findAllPaged(ArgumentMatchers.any())).thenReturn(page);

        Mockito.when(productService.findById(existingId)).thenReturn(productDTO);
        Mockito.when(productService.findById(nonExistingId)).thenThrow(new ResourceNotFoundException("Entity not found"));

        Mockito.when(productService.update(ArgumentMatchers.eq(existingId), ArgumentMatchers.any())).thenReturn(productDTO);
        Mockito.when(productService.update(ArgumentMatchers.eq(nonExistingId), ArgumentMatchers.any())).thenThrow(new ResourceNotFoundException("Entity not found"));

        Mockito.when(productService.save(ArgumentMatchers.any())).thenReturn(productDTO);

        Mockito.doNothing().when(productService).delete(existingId);
        Mockito.doThrow(new ResourceNotFoundException("Entity not found")).when(productService).delete(nonExistingId);
        Mockito.doThrow(new DataBaseException("Data base exception ")).when(productService).delete(dependentId);
    }

    @Test
    @DisplayName("Find all should return page")
    public void findAllShouldReturnPage() throws Exception {
        ResultActions result =
               mockMvc.perform(get(PRODUCTS_PATH)
                      .accept(MediaType.APPLICATION_JSON));


      result.andExpect(status().isOk());
    }

    @Test
    @DisplayName("Find by id should return product dto when id exist")
    public void findByIdShouldReturnProductDTOWhenIdExist() throws Exception {
        ResultActions result =
                     mockMvc.perform(get(PRODUCTS_PATH.concat("/" + existingId))
                            .accept(MediaType.APPLICATION_JSON));


        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").exists());
        result.andExpect(jsonPath("$.name").exists());
        result.andExpect(jsonPath("$.description").exists());
        result.andExpect(jsonPath("$.price").exists());
        result.andExpect(jsonPath("$.imgUrl").exists());
        result.andExpect(jsonPath("$.date").exists());
        result.andExpect(jsonPath("$.categories").exists());
    }

    @Test
    @DisplayName("Find by id should return not found when id does not exist")
    public void findByIdShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
        ResultActions result =
                            mockMvc.perform(get(PRODUCTS_PATH.concat("/" + nonExistingId))
                                    .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());
        result.andExpect(jsonPath("$.timesStamp").exists());
        result.andExpect(jsonPath("$.status").exists());
        result.andExpect(jsonPath("$.error").exists());
        result.andExpect(jsonPath("$.message").exists());
        result.andExpect(jsonPath("$.path").exists());

    }

    @Test
    @DisplayName("Save should return product dto created")
    public void saveShouldReturnProductDTOCreated() throws Exception {
        String json = objectMapper.writeValueAsString(productDTO);
        ResultActions result =
                 mockMvc.perform(MockMvcRequestBuilders.post(PRODUCTS_PATH)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json));

        result.andExpect(status().isCreated());
        result.andExpect(MockMvcResultMatchers.header().exists("location"));
        result.andExpect(jsonPath("$.id").exists());
        result.andExpect(jsonPath("$.name").exists());
        result.andExpect(jsonPath("$.description").exists());
        result.andExpect(jsonPath("$.price").exists());
        result.andExpect(jsonPath("$.date").exists());
        result.andExpect(jsonPath("$.imgUrl").exists());
        result.andExpect(jsonPath("$.categories").exists());
        result.andExpect(jsonPath("$.categories").isNotEmpty());
    }

    @Test
    @DisplayName("Update should return product dto when id exist")
    public void updateShouldReturnProductDTOWhenIdExist() throws Exception {
        String json = objectMapper.writeValueAsString(productDTO);
        ResultActions result =
                        mockMvc.perform(put(PRODUCTS_PATH.concat("/" + existingId))
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json));

        result.andExpect(status().isOk());


    }

    @Test
    @DisplayName("Update should return not found when id does exist")
    public void updateShouldReturnNotFoundWhenIdDoesExist() throws Exception{
        String json = objectMapper.writeValueAsString(productDTO);

        ResultActions result =
                        mockMvc.perform(put(PRODUCTS_PATH.concat("/" + nonExistingId))
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
    public void deleteShouldReturnNoContentWheIdExist() throws Exception {
        ResultActions result =
                     mockMvc.perform(delete(PRODUCTS_PATH.concat("/" + existingId))
                            .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Delete should return not found when id does not exist")
    public void deleteShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
        ResultActions result =
                      mockMvc.perform(delete(PRODUCTS_PATH.concat("/" + nonExistingId))
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
                       mockMvc.perform(delete(PRODUCTS_PATH.concat("/" + dependentId))
                              .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isBadRequest());
        result.andExpect(jsonPath("$.timesStamp").exists());
        result.andExpect(jsonPath("$.status").exists());
        result.andExpect(jsonPath("$.error").exists());
        result.andExpect(jsonPath("$.message").exists());
        result.andExpect(jsonPath("$.path").exists());
    }

}
