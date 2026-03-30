package nguyenthanhnhan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem implements Serializable {

    private Long productId;
    private String productName;
    private Double price;
    private int quantity;
}
