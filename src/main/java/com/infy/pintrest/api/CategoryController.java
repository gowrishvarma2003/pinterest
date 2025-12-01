package com.infy.pintrest.api;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.infy.pinterest.enums.Category;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("categories")
public class CategoryController {

    @GetMapping
    public ResponseEntity<List<String>> getAllCategories() {
        List<String> categories = Arrays.stream(Category.values())
                .map(Category::getDisplayName)
                .collect(Collectors.toList());
        return ResponseEntity.ok(categories);
    }
}
