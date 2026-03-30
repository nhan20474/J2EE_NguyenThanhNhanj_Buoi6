package nguyenthanhnhan.repository;

import nguyenthanhnhan.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    void deleteByProduct_Id(Long productId);
}
