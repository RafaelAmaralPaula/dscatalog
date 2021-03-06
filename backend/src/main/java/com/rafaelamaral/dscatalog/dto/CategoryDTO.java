package com.rafaelamaral.dscatalog.dto;

import com.rafaelamaral.dscatalog.entities.Category;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;

public class CategoryDTO implements Serializable {

    private static final Long serialVersionUID = 1L;

    private Long id;

    @NotBlank(message = "Campo deve ser requerido")
    @Size(min = 10 , max = 60 , message = "Campo deve ter entre 10 a 60 caracteres")
    private String name;

    public CategoryDTO(){}

    public CategoryDTO(Long id , String name){
        this.id = id;
        this.name = name;
    }

    public CategoryDTO(Category entity){
        this.id = entity.getId();
        this.name = entity.getName();
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CategoryDTO)) return false;
        CategoryDTO that = (CategoryDTO) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
