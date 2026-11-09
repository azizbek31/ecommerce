package uz.demo.ecommerce.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uz.demo.ecommerce.dto.PageResponse;
import uz.demo.ecommerce.dto.ProductRequest;
import uz.demo.ecommerce.dto.ProductResponse;
import uz.demo.ecommerce.entity.Category;
import uz.demo.ecommerce.entity.Product;
import uz.demo.ecommerce.exception.BadRequestException;
import uz.demo.ecommerce.exception.NotFoundException;
import uz.demo.ecommerce.repository.CategoryRepository;
import uz.demo.ecommerce.repository.ProductRepository;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    private static final Set<String> ALLOWED_SORT_FIELDS =
            Set.of("id", "name", "price", "stockQuantity", "createdDate");

    public PageResponse<ProductResponse> getAllProducts(Long categoryId, Pageable pageable) {
        pageable.getSort().forEach(order -> {
            if(!ALLOWED_SORT_FIELDS.contains(order.getProperty())) {
                throw new BadRequestException("Invalid sort field: " + order.getProperty());
            }
        });
        Page<Product> page;
        if (categoryId == null) {
            page = productRepository.findAll(pageable);
        } else{
            page = productRepository.findByCategoryId(categoryId, pageable);
        }
        Page<ProductResponse> map = page.map(this::toResponse);
        return new PageResponse<>(
                map.getContent(),
                map.getNumber(),
                map.getSize(),
                map.getTotalElements(),
                map.getTotalPages()
        );
    }

    public ProductResponse getProductById(Long id) {
        return toResponse(findProductByIdOrThrow(id));
    }

    public ProductResponse create(ProductRequest request) {
        Category category = findCategoryByIdOrThrow(request.categoryId());
        Product product = new Product();
        product.setName(request.name().trim());
        product.setDescription(request.description().trim());
        product.setPrice(request.price());
        product.setStockQuantity(request.stockQuantity());
        product.setCategory(category);
        return toResponse(productRepository.save(product));
    }

    public ProductResponse updateById(Long id, ProductRequest request) {
        Product product = findProductByIdOrThrow(id);
        Category category = findCategoryByIdOrThrow(request.categoryId());
        product.setName(request.name().trim());
        product.setDescription(request.description().trim());
        product.setPrice(request.price());
        product.setStockQuantity(request.stockQuantity());
        product.setCategory(category);
        return toResponse(productRepository.save(product));
    }

    public void deleteById(Long id) {
        productRepository.delete(findProductByIdOrThrow(id));
    }

    private Product findProductByIdOrThrow(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new NotFoundException("Product not found with id: " + id));
    }

    private Category findCategoryByIdOrThrow(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new NotFoundException("Category not found with id: " + id));
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getCategory().getId(),
                product.getCategory().getName());
    }
}
