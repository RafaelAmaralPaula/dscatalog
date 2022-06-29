package com.rafaelamaral.dscatalog.services;

import com.rafaelamaral.dscatalog.dto.CategoryDTO;
import com.rafaelamaral.dscatalog.entities.Category;
import com.rafaelamaral.dscatalog.factory.Factory;
import com.rafaelamaral.dscatalog.repositories.CategoryRepository;
import com.rafaelamaral.dscatalog.services.exceptions.DataBaseException;
import com.rafaelamaral.dscatalog.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
public class CategoryServiceTests {

    @InjectMocks
    private CategoryService categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    private long existingId;
    private long nonExistingId;
    private long dependentId;
    private Category category;
    private CategoryDTO categoryDTO;
    private PageImpl<Category> page;


    @BeforeEach
    public void setUp(){
        existingId = 1L;
        nonExistingId = 100L;
        dependentId = 4L;
        category = Factory.createCategory();
        categoryDTO = Factory.createCategoryDTO();
        page = new PageImpl<>(List.of(category));

        Mockito.when(categoryRepository.findAll((Pageable) ArgumentMatchers.any())).thenReturn(page);

        Mockito.when(categoryRepository.save(ArgumentMatchers.any())).thenReturn(category);

        Mockito.when(categoryRepository.findById(existingId)).thenReturn(Optional.of(category));
        Mockito.when(categoryRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        Mockito.when(categoryRepository.getOne(existingId)).thenReturn(category);
        Mockito.when(categoryRepository.getOne(nonExistingId)).thenThrow(EntityNotFoundException.class);

        Mockito.doNothing().when(categoryRepository).deleteById(existingId);
        Mockito.doThrow(EmptyResultDataAccessException.class).when(categoryRepository).deleteById(nonExistingId);
        Mockito.doThrow(DataIntegrityViolationException.class).when(categoryRepository).deleteById(dependentId);
    }

    @Test
    @DisplayName("Find all paged should return page")
    public void findAllPagedShouldReturnPage(){
        Pageable pageable = PageRequest.of(0 , 10);
        Page<CategoryDTO> result = categoryService.findAllPaged(pageable);

        Assertions.assertNotNull(result);
        Mockito.verify(categoryRepository , Mockito.times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Find by id should category dto when id exist")
    public void findByIdShouldReturnCategoryDTOWhenIdExist(){
        CategoryDTO result = categoryService.findById(existingId);

        Assertions.assertNotNull(result);
        Mockito.verify(categoryRepository , Mockito.times(1)).findById(existingId);

    }

    @Test
    @DisplayName("Find by id should throw resource not found exception when id does not exist")
    public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist(){
        Assertions.assertThrows(ResourceNotFoundException.class , ()->{
           categoryService.findById(nonExistingId);
        });

        Mockito.verify(categoryRepository , Mockito.times(1)).findById(nonExistingId);

    }

    @Test
    @DisplayName("Update should return category dto when id exist")
    public void updateShouldReturnCategoryDTOWhenIdExist(){

        CategoryDTO result = categoryService.update(existingId , categoryDTO);

        Assertions.assertNotNull(result);
        Mockito.verify(categoryRepository , Mockito.times(1)).getOne(existingId);
    }

    @Test
    @DisplayName("Update should throw resource not found exception when id does not exist")
    public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist(){
        Assertions.assertThrows(ResourceNotFoundException.class , ()->{
           categoryService.update(nonExistingId , categoryDTO);
        });

        Mockito.verify(categoryRepository , Mockito.times(1)).getOne(nonExistingId);

    }

    @Test
    @DisplayName("Delete should do nothing when id exist")
    public void deleteShouldDoNothingWhenIdExist(){
        Assertions.assertDoesNotThrow(()->{
            categoryService.delete(existingId);
        });
        Mockito.verify(categoryRepository , Mockito.times(1)).deleteById(existingId);
    }

    @Test
    @DisplayName("Delete should throw resource not found exception when id does not exist")
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist(){
        Assertions.assertThrows(ResourceNotFoundException.class , ()->{
            categoryService.delete(nonExistingId);
        });
        Mockito.verify(categoryRepository , Mockito.times(1)).deleteById(nonExistingId);
    }

    @Test
    @DisplayName("Delete should throw data base exception when dependent id")
    public void deleteShouldThrowDataBaseExceptionWhenDependentId(){
        Assertions.assertThrows(DataBaseException.class , ()->{
           categoryService.delete(dependentId);
        });
        Mockito.verify(categoryRepository , Mockito.times(1)).deleteById(dependentId);

    }


}
