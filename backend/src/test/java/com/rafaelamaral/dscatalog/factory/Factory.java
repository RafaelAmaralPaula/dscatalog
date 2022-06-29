package com.rafaelamaral.dscatalog.factory;

import com.rafaelamaral.dscatalog.dto.CategoryDTO;
import com.rafaelamaral.dscatalog.dto.ProductDTO;
import com.rafaelamaral.dscatalog.entities.Category;
import com.rafaelamaral.dscatalog.entities.Product;

import java.time.Instant;

public class Factory {

    public static Product createProduct(){
        Product product = new Product(1L , "Caixa de som JBL" , "Caixinha portátil com design sensacional" , 300.00 , "https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/1-big.jpg" , Instant.parse("2020-07-13T20:50:07.123450Z"));
        product.getCategories().add(createCategory());
        return product;
    }

    public static ProductDTO createProductDTO(){
        Product product = createProduct();
        return new ProductDTO(product , product.getCategories());
    }

    public static Category createCategory(){
        return new Category(1L , "Livros");
    }

    public static CategoryDTO createCategoryDTO(){
        Category category = createCategory();
        return new CategoryDTO(category);
    }

}
