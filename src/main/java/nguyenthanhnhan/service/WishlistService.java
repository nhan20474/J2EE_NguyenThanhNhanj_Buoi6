package nguyenthanhnhan.service;

import nguyenthanhnhan.entity.Account;
import nguyenthanhnhan.entity.Product;
import nguyenthanhnhan.entity.Wishlist;
import nguyenthanhnhan.repository.AccountRepository;
import nguyenthanhnhan.repository.ProductRepository;
import nguyenthanhnhan.repository.WishlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WishlistService {

    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ProductRepository productRepository;

    public Wishlist getWishlist(String loginName) {
        return wishlistRepository.findByAccount_LoginName(loginName)
                .orElseGet(() -> createWishlist(loginName));
    }

    private Wishlist createWishlist(String loginName) {
        Account account = accountRepository.findByLoginName(loginName)
                .orElseThrow(() -> new RuntimeException("Account not found: " + loginName));
        Wishlist wishlist = new Wishlist();
        wishlist.setAccount(account);
        return wishlistRepository.save(wishlist);
    }

    public void addProduct(String loginName, Long productId) {
        Wishlist wishlist = getWishlist(loginName);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found: " + productId));

        if (!wishlist.getProducts().contains(product)) {
            wishlist.getProducts().add(product);
            wishlistRepository.save(wishlist);
        }
    }

    public void removeProduct(String loginName, Long productId) {
        Wishlist wishlist = getWishlist(loginName);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found: " + productId));
        wishlist.getProducts().remove(product);
        wishlistRepository.save(wishlist);
    }

    public boolean isProductInWishlist(String loginName, Long productId) {
        Wishlist wishlist = getWishlist(loginName);
        return wishlist.getProducts().stream()
                .anyMatch(p -> p.getId().equals(productId));
    }

    public List<Product> getWishlistProducts(String loginName) {
        return getWishlist(loginName).getProducts();
    }

    public int getWishlistCount(String loginName) {
        return getWishlist(loginName).getProducts().size();
    }
}
