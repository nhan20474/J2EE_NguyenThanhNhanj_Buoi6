package nguyenthanhnhan.controller;

import nguyenthanhnhan.dto.CartItem;
import nguyenthanhnhan.entity.Order;
import nguyenthanhnhan.service.CartSessionService;
import nguyenthanhnhan.service.OrderService;
import nguyenthanhnhan.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartSessionService cartSessionService;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    @GetMapping("")
    public String viewCart(Authentication authentication, Model model) {
        Map<Long, CartItem> cart = cartSessionService.getCart(authentication.getName());
        int cartCount = cartSessionService.getCartItemCount(authentication.getName());
        model.addAttribute("cart", cart);
        model.addAttribute("cartItems", cart.values());
        model.addAttribute("cartTotal", cartSessionService.sumTotal(cart));
        model.addAttribute("cartCount", cartCount);
        return "cart/view";
    }

    @PostMapping("/add")
    public String addToCart(
            @RequestParam Long productId,
            @RequestParam(defaultValue = "1") int quantity,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        var productOpt = productService.getProductById(productId);
        if (productOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy sản phẩm.");
            return "redirect:/products";
        }
        var p = productOpt.get();
        cartSessionService.addOrUpdate(authentication.getName(), p.getId(), p.getPrice(), quantity);
        redirectAttributes.addFlashAttribute("message", "Đã thêm vào giỏ hàng.");
        return "redirect:/products";
    }

    @PostMapping("/update")
    public String updateQuantity(
            @RequestParam Long productId,
            @RequestParam int quantity,
            Authentication authentication) {
        cartSessionService.setQuantity(authentication.getName(), productId, quantity);
        return "redirect:/cart";
    }

    @PostMapping("/remove")
    public String removeItem(@RequestParam Long productId, Authentication authentication) {
        cartSessionService.remove(authentication.getName(), productId);
        return "redirect:/cart";
    }

    @PostMapping("/checkout")
    public String checkout(Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            Order order = orderService.checkout(authentication.getName());
            redirectAttributes.addFlashAttribute("orderId", order.getId());
            redirectAttributes.addFlashAttribute("orderTotal", order.getTotalAmount());
            return "redirect:/cart/success";
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/cart";
        }
    }

    @GetMapping("/success")
    public String orderSuccess() {
        return "cart/success";
    }
}
