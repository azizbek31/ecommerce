package uz.demo.ecommerce.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.demo.ecommerce.dto.CategoryRequest;
import uz.demo.ecommerce.dto.CategoryResponse;
import uz.demo.ecommerce.entity.Category;
import uz.demo.ecommerce.exception.DuplicateResourceException;
import uz.demo.ecommerce.exception.NotFoundException;
import uz.demo.ecommerce.exception.ResourceInUseException;
import uz.demo.ecommerce.repository.CategoryRepository;
import uz.demo.ecommerce.repository.ProductRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public List<CategoryResponse> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream().map(this::toResponse).toList();
    }

    public CategoryResponse getById(Long id) {
        return toResponse(findByIdOrThrow(id));
    }

    public CategoryResponse create(CategoryRequest request) {
        if (categoryRepository.existsByNameIgnoreCase(request.name())){
            throw new DuplicateResourceException("Category already exists " + request.name());
        }
        Category category = new Category();
        category.setName(request.name().trim());
        category.setDescription(request.description().trim());
        return toResponse(categoryRepository.save(category));
    }

    public CategoryResponse updateById(Long id, CategoryRequest request) {
        Category category = findByIdOrThrow(id);
        if (categoryRepository.existsByNameIgnoreCaseAndIdNot(request.name().trim(), category.getId())) {
            throw new DuplicateResourceException("Category already exists " + request.name());
        }
        category.setName(request.name().trim());
        category.setDescription(request.description().trim());
        return toResponse(categoryRepository.save(category));
    }

    public void deleteById(Long id) {
        Category category = findByIdOrThrow(id);
        if (productRepository.existsByCategoryId(category.getId())){
            throw new ResourceInUseException("Category has products and cannot be deleted");
        }
        categoryRepository.delete(category);
    }

    private Category findByIdOrThrow(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new NotFoundException("Category not found with id " + id));
    }

    private CategoryResponse toResponse(Category category) {
        return new CategoryResponse(category.getId(), category.getName(), category.getDescription());
    }

}
