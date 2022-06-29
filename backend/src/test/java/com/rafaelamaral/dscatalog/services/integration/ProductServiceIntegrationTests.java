package com.rafaelamaral.dscatalog.services.integration;

import com.rafaelamaral.dscatalog.dto.ProductDTO;
import com.rafaelamaral.dscatalog.repositories.ProductRepository;
import com.rafaelamaral.dscatalog.services.ProductService;
import com.rafaelamaral.dscatalog.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class ProductServiceIntegrationTests {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository  productRepository;

    private Long existingId;
    private Long nonExistingId;
    private Long countTotalProducts;


    @BeforeEach
    public void setUp(){
        existingId = 1L;
        nonExistingId = 1000L;
        countTotalProducts = 25L;
    }


    @Test
    @DisplayName("Find all paged should return paged when page 0 size 10")
    public void findAllPagedShouldReturnPagedWhenPage0Size10(){
        PageRequest page = PageRequest.of(0 , 10);

        Page<ProductDTO> result = productService.findAllPaged(page);

        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(0 , result.getNumber());
        Assertions.assertEquals(10, result.getSize());
        Assertions.assertEquals(countTotalProducts , result.getTotalElements());

    }

    @Test
    @DisplayName("Find all paged should return empty page when does not exist page")
    public void findAllPagedShoulReturnEmptyPageWhenDoesNotExistPage(){
        PageRequest page = PageRequest.of(50 , 10);

        Page<ProductDTO> result = productService.findAllPaged(page);

        Assertions.assertTrue(result.isEmpty());

    }

    @Test
    @DisplayName("Find all paged should return sorted Page when sort page")
    public void findAllPagedShouldReturnSortedPageWhenSortPage(){
        PageRequest page = PageRequest.of(0 , 10 , Sort.by("name"));

        Page<ProductDTO> result = productService.findAllPaged(page);

        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals("Macbook Pro" , result.getContent().get(0).getName());
        Assertions.assertEquals("PC Gamer" , result.getContent().get(1).getName());
        Assertions.assertEquals("PC Gamer Alfa" , result.getContent().get(2).getName());

    }


    @Test
    @DisplayName("Delete should delete resource when id exist")
    public void deleteShouldDeleteResourceWhenIdExist(){

        productService.delete(existingId);

        Assertions.assertEquals(countTotalProducts - 1 , productRepository.count());

    }

    @Test
    @DisplayName("Delete should throw resource not found exception when id does not exist")
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist(){

        Assertions.assertThrows(ResourceNotFoundException.class , ()->{
            productService.delete(nonExistingId);
        });
    }
}
