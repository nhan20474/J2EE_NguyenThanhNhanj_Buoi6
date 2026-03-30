package nguyenthanhnhan.service;

import nguyenthanhnhan.dto.CartItem;
import nguyenthanhnhan.entity.Account;
import nguyenthanhnhan.entity.Cart;
import nguyenthanhnhan.entity.CartEntry;
import nguyenthanhnhan.entity.Product;
import nguyenthanhnhan.repository.AccountRepository;
import nguyenthanhnhan.repository.CartRepository;
import nguyenthanhnhan.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class CartSessionService {

    private final CartRepository cartRepository;
    private final AccountRepository accountRepository;
    private final ProductRepository productRepository;

    public CartSessionService(
            CartRepository cartRepository,
            AccountRepository accountRepository,
            ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.accountRepository = accountRepository;
        this.productRepository = productRepository;
    }

    public Map<Long, CartItem> getCart(String loginName) {
        Cart cart = getOrCreateCart(loginName);
        Map<Long, CartItem> result = new LinkedHashMap<>();
        for (CartEntry entry : cart.getItems()) {
            Product product = entry.getProduct();
            result.put(product.getId(), new CartItem(
                    product.getId(),
                    product.getName(),
                    entry.getUnitPrice(),
                    entry.getQuantity()
            ));
        }
        return result;
    }

    public void addOrUpdate(String loginName, Long productId, Double price, int quantity) {
        if (quantity < 1) {
            quantity = 1;
        }

        Cart cart = getOrCreateCart(loginName);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalStateException("Sản phẩm không tồn tại: " + productId));

        CartEntry existing = cart.getItems().stream()
                .filter(entry -> entry.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);

        if (existing == null) {
            CartEntry entry = new CartEntry();
            entry.setCart(cart);
            entry.setProduct(product);
            entry.setQuantity(quantity);
            entry.setUnitPrice(price);
            cart.getItems().add(entry);
        } else {
            existing.setQuantity(existing.getQuantity() + quantity);
            existing.setUnitPrice(price);
        }

        cartRepository.save(cart);
    }

    public void setQuantity(String loginName, Long productId, int quantity) {
        Cart cart = getOrCreateCart(loginName);
        CartEntry existing = cart.getItems().stream()
                .filter(entry -> entry.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);
        if (existing == null) {
            return;
        }

        if (quantity < 1) {
            cart.getItems().remove(existing);
        } else {
            existing.setQuantity(quantity);
        }

        cartRepository.save(cart);
    }

    public void remove(String loginName, Long productId) {
        Cart cart = getOrCreateCart(loginName);
        cart.getItems().removeIf(entry -> entry.getProduct().getId().equals(productId));
        cartRepository.save(cart);
    }

    public void clear(String loginName) {
        Cart cart = getOrCreateCart(loginName);
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    public double sumTotal(Map<Long, CartItem> cart) {
        return cart.values().stream()
                .mapToDouble(i -> i.getPrice() * i.getQuantity())
                .sum();
    }

    public int getCartItemCount(String loginName) {
        Map<Long, CartItem> cart = getCart(loginName);
        return cart.values().stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

    private Cart getOrCreateCart(String loginName) {
        return cartRepository.findByAccount_LoginName(loginName)
                .orElseGet(() -> {
                    Account account = accountRepository.findByLoginName(loginName)
                            .orElseThrow(() -> new IllegalStateException("Account not found: " + loginName));
                    Cart cart = new Cart();
                    cart.setAccount(account);
                    return cartRepository.save(cart);
                });
    }
}
