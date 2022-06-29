package com.rafaelamaral.dscatalog.services.integration;

import com.rafaelamaral.dscatalog.dto.CategoryDTO;
import com.rafaelamaral.dscatalog.repositories.CategoryRepository;
import com.rafaelamaral.dscatalog.services.CategoryService;
import com.rafaelamaral.dscatalog.services.exceptions.DataBaseException;
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

@SpringBootTest
public class CategoryServiceIntegrationTests {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    private Long exisitingId;
    private Long nonExistingId;
    private Long countTotalCategories;

    @BeforeEach
    public void setUp(){
        exisitingId = 1L;
        nonExistingId = 1000L;
        countTotalCategories = 3L;
    }

    @Test
    @DisplayName("Find all paged should return paged when page 0 size 10")
    public void findAllPagedShouldReturnPageWhenPage0Size10() {
        PageRequest page = PageRequest.of(0, 10);

        Page<CategoryDTO> result = categoryService.findAllPaged(page);

        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(0, result.getNumber());
        Assertions.assertEquals(10 , result.getSize());
        Assertions.assertEquals(countTotalCategories , result.getTotalElements());

    }

    @Test
    @DisplayName("Find all page should return empty page when page does not exist")
    public void findAllPagedShouldReturnEmptyPageWhenPageDoesNotExist(){
        PageRequest page = PageRequest.of(50 , 10);

        Page<CategoryDTO> result = categoryService.findAllPaged(page);

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Find all paged should return sorted page when sort page")
    public void findAllPagedShouldReturnSortedPageWhenSortPage(){
        PageRequest page = PageRequest.of(0 , 10 , Sort.by("name"));

        Page<CategoryDTO> result = categoryService.findAllPaged(page);

        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals("Computadores" , result.getContent().get(0).getName());
        Assertions.assertEquals("Eletrônicos" , result.getContent().get(1).getName());
        Assertions.assertEquals("Livros" , result.getContent().get(2).getName());
    }


    @Test
    @DisplayName("Delete should not delete resource when id exist and dependent")
    public void deleteShouldNotDeleteResourceWhenIdExistAndDependent(){

       Assertions.assertThrows(DataBaseException.class , ()->{
           categoryService.delete(exisitingId);
       }) ;
    }

    @Test
    @DisplayName("Delete should throw resource not found exception when id does not exist")
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist(){

        Assertions.assertThrows(ResourceNotFoundException.class , ()->{
           categoryService.delete(nonExistingId);
        });
    }

}
