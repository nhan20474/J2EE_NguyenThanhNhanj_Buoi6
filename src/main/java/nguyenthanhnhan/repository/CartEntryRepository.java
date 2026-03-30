package nguyenthanhnhan.repository;

import nguyenthanhnhan.entity.CartEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartEntryRepository extends JpaRepository<CartEntry, Long> {
    void deleteByProduct_Id(Long productId);
}
