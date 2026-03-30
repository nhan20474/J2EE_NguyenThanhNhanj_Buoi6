package nguyenthanhnhan.service;

import nguyenthanhnhan.entity.Product;
import nguyenthanhnhan.entity.Category;
import nguyenthanhnhan.repository.CartEntryRepository;
import nguyenthanhnhan.repository.OrderDetailRepository;
import nguyenthanhnhan.repository.ProductRepository;
import nguyenthanhnhan.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CartEntryRepository cartEntryRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;
    
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }
    
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }
    
    @Transactional
    public void deleteProduct(Long id) {
        orderDetailRepository.deleteByProduct_Id(id);
        cartEntryRepository.deleteByProduct_Id(id);
        productRepository.deleteById(id);
    }
    
    public Product updateProduct(Long id, Product productDetails) {
        Optional<Product> existingProduct = productRepository.findById(id);
        if (existingProduct.isPresent()) {
            Product product = existingProduct.get();
            product.setName(productDetails.getName());
            product.setPrice(productDetails.getPrice());
            product.setCategory(productDetails.getCategory());
            product.setImage(productDetails.getImage());
            return productRepository.save(product);
        }
        return null;
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Page<Product> searchProducts(String keyword, Long categoryId, String sortKey, int page, int pageSize) {
        Specification<Product> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (keyword != null && !keyword.isBlank()) {
                String pattern = "%" + keyword.toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get("name")), pattern));
            }
            if (categoryId != null) {
                predicates.add(cb.equal(root.get("category").get("id"), categoryId));
            }
            if (predicates.isEmpty()) {
                return cb.conjunction();
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        };

        Sort sort = switch (sortKey != null ? sortKey : "default") {
            case "priceAsc" -> Sort.by("price").ascending();
            case "priceDesc" -> Sort.by("price").descending();
            default -> Sort.by("id").ascending();
        };

        Pageable pageable = PageRequest.of(Math.max(page, 0), pageSize, sort);
        return productRepository.findAll(spec, pageable);
    }
}
