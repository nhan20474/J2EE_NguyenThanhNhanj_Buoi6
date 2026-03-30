package nguyenthanhnhan.controller;

import nguyenthanhnhan.service.CartSessionService;
import nguyenthanhnhan.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private CartSessionService cartSessionService;

    @GetMapping
    public String orderHistory(Authentication authentication, Model model) {
        String loginName = authentication.getName();
        model.addAttribute("orders", orderService.getOrderHistory(loginName));
        model.addAttribute("cartCount", cartSessionService.getCartItemCount(loginName));
        return "orders/list";
    }

    @GetMapping("/{id}")
    public String orderDetail(
            @PathVariable Long id,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {
        String loginName = authentication.getName();
        var orderOpt = orderService.getOrderForUser(id, loginName);
        if (orderOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy đơn hàng.");
            return "redirect:/orders";
        }

        model.addAttribute("order", orderOpt.get());
        model.addAttribute("cartCount", cartSessionService.getCartItemCount(loginName));
        return "orders/detail";
    }
}
