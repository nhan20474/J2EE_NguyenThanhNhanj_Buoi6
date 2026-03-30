package nguyenthanhnhan.service;

import nguyenthanhnhan.dto.CartItem;
import nguyenthanhnhan.entity.Account;
import nguyenthanhnhan.entity.Order;
import nguyenthanhnhan.entity.OrderDetail;
import nguyenthanhnhan.entity.Product;
import nguyenthanhnhan.repository.AccountRepository;
import nguyenthanhnhan.repository.OrderRepository;
import nguyenthanhnhan.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CartSessionService cartSessionService;

    @Transactional
    public Order checkout(String loginName) {
        Map<Long, CartItem> cart = cartSessionService.getCart(loginName);
        if (cart.isEmpty()) {
            throw new IllegalStateException("Giỏ hàng trống");
        }

        Account account = accountRepository.findByLoginName(loginName)
                .orElseThrow(() -> new IllegalStateException("Account not found: " + loginName));

        Order order = new Order();
        order.setCreatedAt(LocalDateTime.now());
        order.setAccount(account);
        double total = 0;

        for (CartItem item : cart.values()) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new IllegalStateException("Sản phẩm không tồn tại: " + item.getProductId()));
            double lineTotal = item.getPrice() * item.getQuantity();
            total += lineTotal;

            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setProduct(product);
            detail.setQuantity(item.getQuantity());
            detail.setUnitPrice(item.getPrice());
            order.getDetails().add(detail);
        }

        order.setTotalAmount(total);
        Order saved = orderRepository.save(order);
        cartSessionService.clear(loginName);
        return saved;
    }

    public List<Order> getOrderHistory(String loginName) {
        return orderRepository.findByAccount_LoginNameOrderByCreatedAtDesc(loginName);
    }

    public Optional<Order> getOrderForUser(Long orderId, String loginName) {
        return orderRepository.findByIdAndAccount_LoginName(orderId, loginName);
    }
}
