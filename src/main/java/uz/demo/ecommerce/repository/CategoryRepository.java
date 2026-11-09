package uz.demo.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.demo.ecommerce.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
}
