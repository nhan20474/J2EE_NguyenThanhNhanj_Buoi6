package nguyenthanhnhan.controller;

import nguyenthanhnhan.service.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/wishlist")
public class WishlistController {

    @Autowired
    private WishlistService wishlistService;

    @GetMapping
    public String viewWishlist(Authentication authentication, Model model) {
        if (authentication == null) {
            return "redirect:/login";
        }
        String loginName = authentication.getName();
        model.addAttribute("wishlistItems", wishlistService.getWishlistProducts(loginName));
        model.addAttribute("wishlistCount", wishlistService.getWishlistCount(loginName));
        return "wishlist/view";
    }

    @PostMapping("/add/{productId}")
    public String addToWishlist(Authentication authentication, @PathVariable Long productId) {
        if (authentication == null) {
            return "redirect:/login";
        }
        wishlistService.addProduct(authentication.getName(), productId);
        return "redirect:/products";
    }

    @PostMapping("/remove/{productId}")
    public String removeFromWishlist(Authentication authentication, @PathVariable Long productId) {
        if (authentication == null) {
            return "redirect:/login";
        }
        wishlistService.removeProduct(authentication.getName(), productId);
        return "redirect:/wishlist";
    }

    @GetMapping("/check/{productId}")
    @ResponseBody
    public boolean checkWishlist(Authentication authentication, @PathVariable Long productId) {
        if (authentication == null) {
            return false;
        }
        return wishlistService.isProductInWishlist(authentication.getName(), productId);
    }
}
