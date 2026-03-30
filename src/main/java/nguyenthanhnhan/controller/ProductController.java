package nguyenthanhnhan.controller;

import nguyenthanhnhan.entity.Product;
import nguyenthanhnhan.service.CartSessionService;
import nguyenthanhnhan.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/products")
public class ProductController {
    
    @Autowired
    private ProductService productService;

    @Autowired
    private CartSessionService cartSessionService;
    
    private static final int PAGE_SIZE = 5;

    @GetMapping("")
    public String listProducts(
            Authentication authentication,
            @RequestParam(required = false) String keyword,
            @RequestParam(name = "categoryId", required = false) String categoryIdParam,
            @RequestParam(required = false, defaultValue = "default") String sort,
            @RequestParam(defaultValue = "0") int page,
            Model model) {
        String kw = keyword != null ? keyword : "";
        Long categoryId = null;
        if (categoryIdParam != null && !categoryIdParam.isBlank()) {
            try {
                categoryId = Long.parseLong(categoryIdParam);
            } catch (NumberFormatException ignored) {
                // bỏ qua giá trị không hợp lệ
            }
        }
        Page<Product> productPage = productService.searchProducts(kw, categoryId, sort, page, PAGE_SIZE);
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("productPage", productPage);
        model.addAttribute("keyword", kw);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("sort", sort);
        model.addAttribute("categories", productService.getAllCategories());
        if (authentication != null) {
            int cartCount = cartSessionService.getCartItemCount(authentication.getName());
            model.addAttribute("cartCount", cartCount);
        }
        return "products/list";
    }

    @GetMapping("/{id}")
    public String productDetail(
            @PathVariable Long id,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {
        var productOpt = productService.getProductById(id);
        if (productOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy sản phẩm.");
            return "redirect:/products";
        }

        model.addAttribute("product", productOpt.get());
        if (authentication != null) {
            int cartCount = cartSessionService.getCartItemCount(authentication.getName());
            model.addAttribute("cartCount", cartCount);
        }
        return "products/detail";
    }
    
    // Show create product form
    @GetMapping("/add")
    public String showCreateForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", productService.getAllCategories());
        return "products/form";
    }
    
    // Save new product
    @PostMapping("/add")
    public String createProduct(@ModelAttribute Product product) {
        productService.saveProduct(product);
        return "redirect:/products";
    }
    
    // Show edit product form
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id).orElse(null);
        model.addAttribute("product", product);
        model.addAttribute("categories", productService.getAllCategories());
        return "products/form";
    }
    
    // Update product
    @PostMapping("/{id}/edit")
    public String updateProduct(@PathVariable Long id, @ModelAttribute Product product) {
        productService.updateProduct(id, product);
        return "redirect:/products";
    }
    
    // Delete product
    @GetMapping("/{id}/delete")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return "redirect:/products";
    }
    
    // Redirect / to /products
    @GetMapping("/")
    public String home() {
        return "redirect:/products";
    }
}
