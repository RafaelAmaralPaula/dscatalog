package com.rafaelamaral.dscatalog.repositories;

import com.rafaelamaral.dscatalog.entities.Category;
import com.rafaelamaral.dscatalog.factory.Factory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.Optional;

@DataJpaTest
public class CategoryRepositoryTests {

    @Autowired
    private CategoryRepository categoryRepository;

    private long existingId;
    private long nonExistingId;
    private long countTotalCategory;

    @BeforeEach
    public void setUp(){
        existingId = 1L;
        nonExistingId = 1000L;
        countTotalCategory = 3;
    }

    @Test
    @DisplayName("Find by id should return optional when id exist")
    public void findByIdShouldReturnOptionalWhenIdExist(){
        Optional<Category> result = categoryRepository.findById(existingId);

        Assertions.assertTrue(result.isPresent());

    }

    @Test
    @DisplayName("Find by id return empty optional when id does not exist")
    public void findByIdReturnEmptyOptionalWhenIdDoesNotExist(){
        Optional<Category> result = categoryRepository.findById(nonExistingId);

        Assertions.assertFalse(result.isPresent());

    }

    @Test
    @DisplayName("Save should persist with auto increment when id is null")
    public void saveShouldPersistWithAutoIncrementWhenIdIsNull(){
        Category category = Factory.createCategory();
        category.setId(null);

        category = categoryRepository.save(category);

        Assertions.assertNotNull(category.getId());
        Assertions.assertEquals(category.getName() , "Livros");
        Assertions.assertEquals(countTotalCategory + 1 , category.getId());

    }

    @Test
    @DisplayName("Update should update category when id exist")
    public void updateShouldUpdateCategoryWhenIdExist(){
        Category category = Factory.createCategory();
        category.setName("Eletrônicos");

        Category categoryUpdated = categoryRepository.save(category);

        Assertions.assertNotNull(categoryUpdated);
        Assertions.assertEquals(category.getName() ,  categoryUpdated.getName());
    }

    @Test
    @DisplayName("Delete should delete category when id exist")
    public void deleteShouldDeleteCategoryWhenIdExist(){
        categoryRepository.deleteById(existingId);

        Optional<Category> result = categoryRepository.findById(existingId);

        Assertions.assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Delete should throw empty result data access exception when id does not exist")
    public void deleteShouldThrowEmptyResultDataAccessExceptionWhenIdDoesNotExist(){
        Assertions.assertThrows(EmptyResultDataAccessException.class , ()->{
            categoryRepository.deleteById(nonExistingId);
        });

    }

}
