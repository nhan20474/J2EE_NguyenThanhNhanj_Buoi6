package nguyenthanhnhan.service;

import nguyenthanhnhan.entity.Category;
import nguyenthanhnhan.repository.CategoryRepository;
import nguyenthanhnhan.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    public Category updateCategory(Long id, Category categoryDetails) {
        return categoryRepository.findById(id).map(category -> {
            category.setName(categoryDetails.getName());
            return categoryRepository.save(category);
        }).orElseThrow(() -> new RuntimeException("Category not found: " + id));
    }

    public void deleteCategory(Long id) {
        if (productRepository.existsByCategory_Id(id)) {
            throw new IllegalStateException("Không thể xóa danh mục vì đang có sản phẩm thuộc danh mục này.");
        }
        categoryRepository.deleteById(id);
    }
}
