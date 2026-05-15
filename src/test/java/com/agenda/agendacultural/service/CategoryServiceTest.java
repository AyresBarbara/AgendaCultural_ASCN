package com.agenda.agendacultural.service;

import com.agenda.agendacultural.dto.CategoryDTO;
import com.agenda.agendacultural.model.Category;
import com.agenda.agendacultural.repository.CategoryRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category category1;
    private Category category2;
    private CategoryDTO categoryDTO1;

    @BeforeEach
    void setUp() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        category1 = new Category();
        category1.setIdCategory(id1);
        category1.setName("Música");
        category1.setDescription("Eventos musicais");

        category2 = new Category();
        category2.setIdCategory(id2);
        category2.setName("Teatro");
        category2.setDescription("Peças de teatro");

        categoryDTO1 = new CategoryDTO();
        categoryDTO1.setIdCategory(id1);
        categoryDTO1.setName("Música");
        categoryDTO1.setDescription("Eventos musicais");
    }

    @Test
    void getAllCategories_shouldReturnListOfCategoryDTOs() {
        when(categoryRepository.findAll()).thenReturn(Arrays.asList(category1, category2));

        List<CategoryDTO> result = categoryService.getAllCategories();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Música", result.get(0).getName());
        assertEquals("Teatro", result.get(1).getName());
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    void getCategoryById_whenCategoryExists_shouldReturnCategoryDTO() {
        when(categoryRepository.findById(category1.getIdCategory())).thenReturn(Optional.of(category1));

        CategoryDTO result = categoryService.getCategoryById(category1.getIdCategory());

        assertNotNull(result);
        assertEquals(category1.getIdCategory(), result.getIdCategory());
        assertEquals("Música", result.getName());
        verify(categoryRepository, times(1)).findById(category1.getIdCategory());
    }

    @Test
    void getCategoryById_whenCategoryDoesNotExist_shouldThrowException() {
        UUID nonExistentId = UUID.randomUUID();
        when(categoryRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            categoryService.getCategoryById(nonExistentId);
        });
        verify(categoryRepository, times(1)).findById(nonExistentId);
    }

    @Test
    void createCategory_shouldReturnCreatedCategoryDTO() {
        // Mock the save operation to return the category with an ID
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> {
            Category catToSave = invocation.getArgument(0);
            // Simulate saving by assigning the ID if it's null (though service sets it)
            if (catToSave.getIdCategory() == null) {
                 catToSave.setIdCategory(UUID.randomUUID());
            }
            return catToSave;
        });

        CategoryDTO newCategoryDTO = new CategoryDTO();
        newCategoryDTO.setName("Dança");
        newCategoryDTO.setDescription("Eventos de dança");

        CategoryDTO result = categoryService.createCategory(newCategoryDTO);

        assertNotNull(result);
        assertNotNull(result.getIdCategory()); // ID should be generated and returned
        assertEquals("Dança", result.getName());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void updateCategory_whenCategoryExists_shouldReturnUpdatedCategoryDTO() {
        when(categoryRepository.findById(category1.getIdCategory())).thenReturn(Optional.of(category1));
        when(categoryRepository.save(any(Category.class))).thenReturn(category1); // Return the updated entity

        CategoryDTO updatedInfo = new CategoryDTO();
        updatedInfo.setName("Música Atualizada");
        updatedInfo.setDescription("Descrição Atualizada");

        CategoryDTO result = categoryService.updateCategory(category1.getIdCategory(), updatedInfo);

        assertNotNull(result);
        assertEquals(category1.getIdCategory(), result.getIdCategory());
        assertEquals("Música Atualizada", result.getName());
        assertEquals("Descrição Atualizada", result.getDescription());
        verify(categoryRepository, times(1)).findById(category1.getIdCategory());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

     @Test
    void updateCategory_whenCategoryDoesNotExist_shouldThrowException() {
        UUID nonExistentId = UUID.randomUUID();
        when(categoryRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        CategoryDTO updatedInfo = new CategoryDTO();
        updatedInfo.setName("Update Fail");

        assertThrows(RuntimeException.class, () -> {
            categoryService.updateCategory(nonExistentId, updatedInfo);
        });
        verify(categoryRepository, times(1)).findById(nonExistentId);
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void deleteCategory_whenCategoryExists_shouldCallDeleteById() {
        when(categoryRepository.existsById(category1.getIdCategory())).thenReturn(true);
        doNothing().when(categoryRepository).deleteById(category1.getIdCategory());

        categoryService.deleteCategory(category1.getIdCategory());

        verify(categoryRepository, times(1)).existsById(category1.getIdCategory());
        verify(categoryRepository, times(1)).deleteById(category1.getIdCategory());
    }

    @Test
    void deleteCategory_whenCategoryDoesNotExist_shouldThrowException() {
        UUID nonExistentId = UUID.randomUUID();
        when(categoryRepository.existsById(nonExistentId)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> {
            categoryService.deleteCategory(nonExistentId);
        });

        verify(categoryRepository, times(1)).existsById(nonExistentId);
        verify(categoryRepository, never()).deleteById(any(UUID.class));
    }
}

