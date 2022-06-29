package com.rafaelamaral.dscatalog.services;

import com.rafaelamaral.dscatalog.dto.ProductDTO;
import com.rafaelamaral.dscatalog.entities.Category;
import com.rafaelamaral.dscatalog.entities.Product;
import com.rafaelamaral.dscatalog.factory.Factory;
import com.rafaelamaral.dscatalog.repositories.CategoryRepository;
import com.rafaelamaral.dscatalog.repositories.ProductRepository;
import com.rafaelamaral.dscatalog.services.exceptions.DataBaseException;
import com.rafaelamaral.dscatalog.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    private long existingId;
    private long nonExistingId;
    private long dependentId;
    private PageImpl<Product> page;
    private Product product;
    private ProductDTO productDTO;
    private Category category;


    @BeforeEach
    public void setUp(){
        existingId = 1L;
        nonExistingId = 100L;
        dependentId = 4L;
        product = Factory.createProduct();
        productDTO =  Factory.createProductDTO();
        page = new PageImpl<>(List.of(product));
        category = Factory.createCategory();


        Mockito.when(productRepository.findAll((Pageable)ArgumentMatchers.any())).thenReturn(page);

        Mockito.when(productRepository.save(ArgumentMatchers.any())).thenReturn(product);

        Mockito.when(productRepository.findById(existingId)).thenReturn(Optional.of(product));
        Mockito.when(productRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        Mockito.when(productRepository.getOne(existingId)).thenReturn(product);
        Mockito.when(productRepository.getOne(nonExistingId)).thenThrow(EntityNotFoundException.class);

        Mockito.when(categoryRepository.getOne(existingId)).thenReturn(category);
        Mockito.when(categoryRepository.getOne(nonExistingId)).thenThrow(EntityNotFoundException.class);


        Mockito.doNothing().when(productRepository).deleteById(existingId);
        Mockito.doThrow(EmptyResultDataAccessException.class).when(productRepository).deleteById(nonExistingId);
        Mockito.doThrow(DataIntegrityViolationException.class).when(productRepository).deleteById(dependentId);
    }

    @Test
    @DisplayName("Find all paged should return page")
    public void findAllPagedShouldReturnPage(){
        Pageable pageable = PageRequest.of(0 , 10);
        Page<ProductDTO> result = productService.findAllPaged(pageable);

        Assertions.assertNotNull(result);
        Mockito.verify(productRepository , Mockito.times(1)).findAll(pageable);

    }

    @Test
    @DisplayName("Find by id should return product dto when id exist")
    public void findByIdShouldReturnProductDTOWhenIdExist(){
        ProductDTO result = productService.findById(existingId);

        Assertions.assertNotNull(result);
        Mockito.verify(productRepository , Mockito.times(1)).findById(existingId);
    }

    @Test
    @DisplayName("Find by id should return throw resource not found exception when id does not exist")
    public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist(){

        Assertions.assertThrows(ResourceNotFoundException.class , ()->{
           productService.findById(nonExistingId);
        });

        Mockito.verify(productRepository, Mockito.times(1)).findById(nonExistingId);
    }

    @Test
    @DisplayName("Update should return product dto when id exist")
    public void updateShouldReturnProductDTOWhenIdExist(){
        ProductDTO result = productService.update(existingId , productDTO);

        Assertions.assertNotNull(result);
        Mockito.verify(productRepository, Mockito.times(1)).getOne(existingId);

    }

    @Test
    @DisplayName("Update should throw resource not found exception when does not exist")
    public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist(){
        Assertions.assertThrows(ResourceNotFoundException.class , ()->{
            productService.update(nonExistingId , productDTO);
        });
        Mockito.verify(productRepository , Mockito.times(1)).getOne(nonExistingId);
    }

    @Test
    @DisplayName("Delete should do nothing when id exist")
    public void deleteShouldDoNothingWhenIdExist(){
        Assertions.assertDoesNotThrow(() ->{
            productService.delete(existingId);
        });
        Mockito.verify(productRepository , Mockito.times(1)).deleteById(existingId);
    }

    @Test
    @DisplayName("Delete should throw resource not found exception when id does not exist")
    public void deleteShouldThrowResourceNotFoundExceptionExceptionWhenIdDoesNotExist(){
        Assertions.assertThrows(ResourceNotFoundException.class , ()->{
           productService.delete(nonExistingId);
        });

        Mockito.verify(productRepository).deleteById(nonExistingId);
    }

    @Test
    @DisplayName("Delete should throw data base exception when dependent id")
    public void deleteShouldThrowDataBaseExceptionWheDependenId(){
        Assertions.assertThrows(DataBaseException.class , ()->{
           productService.delete(dependentId);
        });
        Mockito.verify(productRepository , Mockito.times(1)).deleteById(dependentId);
    }
}
