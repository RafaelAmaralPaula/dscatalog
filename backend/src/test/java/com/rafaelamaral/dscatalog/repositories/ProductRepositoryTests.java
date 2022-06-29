package com.rafaelamaral.dscatalog.repositories;

import com.rafaelamaral.dscatalog.entities.Product;
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
public class ProductRepositoryTests {

    @Autowired
    private ProductRepository productRepository;

    private long existingId;
    private long nonExistingId;
    private long countTotalProducts;

    @BeforeEach
    void setUp() throws Exception{
        existingId = 1L;
        nonExistingId = 1000L;
        countTotalProducts = 25L;
    }

    @Test
    @DisplayName("Find by id should return non empty optional when id exist")
    public void findByIdShouldReturnNonEmptyOptionalWhenIdExist(){
        Optional<Product> result = productRepository.findById(existingId);

        Assertions.assertTrue(result.isPresent());
    }

    @Test
    @DisplayName("Find by id should return empty optional when id does not exist")
    public void findByIdShouldReturnEmptyOptionalWhenIdDoesNotExist(){
        Optional<Product> result = productRepository.findById(nonExistingId);

        Assertions.assertFalse(result.isPresent());
    }


    @Test
    @DisplayName("Save should persist with auto increment when id is null")
    public void saveShouldPersistWithAutoIncrementWhenIdIsNull(){
        Product product = Factory.createProduct();
        product.setId(null);

        product = productRepository.save(product);

        Assertions.assertNotNull(product.getId());
        Assertions.assertEquals(countTotalProducts + 1 , product.getId());

    }

    @Test
    @DisplayName("Update should update object when id is valid")
    public void updateShouldUpdateObjectWhenIdIsValid(){
        Product product = Factory.createProduct();
        product.setId(1L);
        product.setName("Tênis Nike AIR force");
        product.setDescription("Melhor tênis para atividades física");

        Product productUpdate = productRepository.save(product);

        Assertions.assertNotNull(productUpdate.getId());
        Assertions.assertEquals(productUpdate.getName() , product.getName());
        Assertions.assertEquals(productUpdate.getDescription() , product.getDescription());
        Assertions.assertEquals(productUpdate.getImgUrl() , product.getImgUrl());
        Assertions.assertEquals(productUpdate.getPrice() , product.getPrice());

    }

    @Test
    @DisplayName("Delete should delete object when id exist")
    public void deleteShouldDeleteObjectWhenIdExist(){
        productRepository.deleteById(existingId);

        Optional<Product> result = productRepository.findById(existingId);

        Assertions.assertFalse(result.isPresent());

    }

    @Test
    @DisplayName("Delete should throw EmptyResultDataAccessException when id does not exist")
    public void deleteShouldThrowEmptyResultDataAccessExceptionWheIdDoesNotExist(){
        Assertions.assertThrows(EmptyResultDataAccessException.class , ()->{
            productRepository.deleteById(nonExistingId);
        });
    }
}
