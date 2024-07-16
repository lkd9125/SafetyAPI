package com.safety.law.domain.law.model.category;

import java.util.ArrayList;
import java.util.List;

import com.safety.law.domain.law.constant.CategoryConstant;

import lombok.Data;

@Data
public class SearchCategoryRS {

    public List<Category> getCategories(){

        List<Category> result = new ArrayList<>();

        for(CategoryConstant constant : CategoryConstant.values()){
            Category node = new Category();
            node.setCategoryNumber(constant.getNumber());
            node.setCategoryDesc(constant.getDesc());

            result.add(node);
        }

        return result;
    }

    @Data
    public static class Category{

        private Integer categoryNumber;

        private String categoryDesc;
    }
}
