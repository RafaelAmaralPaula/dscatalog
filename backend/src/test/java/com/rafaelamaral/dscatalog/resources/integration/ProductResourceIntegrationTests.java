package com.rafaelamaral.dscatalog.resources.integration;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rafaelamaral.dscatalog.dto.ProductDTO;
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
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProductResourceIntegrationTests {

    static final String PRODUCT_PATH = "/products";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Long existingId;
    private Long nonExistingId;
    private Long countTotalProduct;

    @BeforeEach
    public void setUp(){
        existingId = 1L;
        nonExistingId = 1000L;
        countTotalProduct = 25L;
    }

    @Test
    @DisplayName("Find all paged should return sorted page when sort by name")
    public void findAllPageShouldReturnSortedPageWhenSortByName() throws Exception{
        ResultActions result =
                         mockMvc.perform(get(PRODUCT_PATH.concat("?page=0&size=12&sort=name,asc"))
                                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("content").exists());
        result.andExpect(jsonPath("$.totalElements").value(countTotalProduct));
        result.andExpect(jsonPath("$.content[0].name").value("Macbook Pro"));
        result.andExpect(jsonPath("$.content[1].name").value("PC Gamer"));
        result.andExpect(jsonPath("$.content[2].name").value("PC Gamer Alfa"));


    }

    @Test
    @DisplayName("Update should return product dto when id exist")
    public void updateShouldReturnProductDTOWhenIdExist() throws Exception {
        ProductDTO productDTO = Factory.createProductDTO();

        String json = objectMapper.writeValueAsString(productDTO);

        String expectedName = productDTO.getName();
        String expectedDescription = productDTO.getDescription();
        String categories = productDTO.getCategories().toString();

        ResultActions result =
                     mockMvc.perform(put(PRODUCT_PATH.concat("/" + existingId))
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json));


        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.name").value(expectedName));
        result.andExpect(jsonPath("$.description").value(expectedDescription));


    }

    @Test
    @DisplayName("Update should return not found when id does not exist")
    public void updateShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
        ProductDTO productDTO = Factory.createProductDTO();

        String json = objectMapper.writeValueAsString(productDTO);

        ResultActions result =
                      mockMvc.perform(put(PRODUCT_PATH.concat("/" + nonExistingId))
                             .accept(MediaType.APPLICATION_JSON)
                             .contentType(MediaType.APPLICATION_JSON)
                             .content(json));

        result.andExpect(status().isNotFound());
        result.andExpect(jsonPath("$.timesStamp").exists());
        result.andExpect(jsonPath("$.status").value("404"));
        result.andExpect(jsonPath("$.error").value("Resource not found"));
        result.andExpect(jsonPath("$.message").isNotEmpty());
        result.andExpect(jsonPath("$.path").isNotEmpty());

    }

}
