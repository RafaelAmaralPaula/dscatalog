package com.rafaelamaral.dscatalog.services;

import com.rafaelamaral.dscatalog.dto.CategoryDTO;
import com.rafaelamaral.dscatalog.dto.ProductDTO;
import com.rafaelamaral.dscatalog.entities.Category;
import com.rafaelamaral.dscatalog.entities.Product;
import com.rafaelamaral.dscatalog.repositories.CategoryRepository;
import com.rafaelamaral.dscatalog.repositories.ProductRepository;
import com.rafaelamaral.dscatalog.services.exceptions.DataBaseException;
import com.rafaelamaral.dscatalog.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository repository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public Page<ProductDTO> findAllPaged(Pageable pageable){
       Page<Product> list = repository.findAll(pageable);
       return list.map(X -> new ProductDTO(X));
    }

    @Transactional(readOnly = true)
    public ProductDTO findById(Long id) {
        Optional<Product> obj = repository.findById(id);
        Product entity = obj.orElseThrow(() -> new ResourceNotFoundException("Entity not found"));
        return new ProductDTO(entity , entity.getCategories());
    }

    @Transactional
    public ProductDTO save(ProductDTO dto) {
        Product entity = new Product();
        copyDtoToEntity(entity , dto);
        entity = repository.save(entity);
        return new ProductDTO(entity);

    }

    @Transactional
    public ProductDTO update(Long id, ProductDTO dto) {
        try{
            Product entity = repository.getOne(id);
            copyDtoToEntity(entity , dto);
            entity = repository.save(entity);

            return new ProductDTO(entity);
        }
        catch (EntityNotFoundException ex){
            throw new ResourceNotFoundException("Id not found : " + id);
        }
    }

    public void delete(Long id) {
        try{
            repository.deleteById(id);
        }
        catch (EmptyResultDataAccessException ex ){
            throw new ResourceNotFoundException("Id not found : " + id);
        }
        catch (DataIntegrityViolationException ex){
            throw new DataBaseException("Integrity Violation");

        }
    }

    private void copyDtoToEntity(Product entity, ProductDTO dto) {
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setPrice(dto.getPrice());
        entity.setImgUrl(dto.getImgUrl());
        entity.setDate(dto.getDate());

        entity.getCategories().clear();

        for(CategoryDTO catDto : dto.getCategories()){
            Category category = categoryRepository.getOne(catDto.getId());
            entity.getCategories().add(category);
        }

    }



}
