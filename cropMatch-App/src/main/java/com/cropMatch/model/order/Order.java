package com.cropMatch.model.order;

import com.cropMatch.enums.CropUnit;
import com.cropMatch.enums.OrderStatus;
import com.cropMatch.model.farmer.Crop;
import com.cropMatch.model.user.UserDetail;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "orders")
public class Order {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer id;

        @ManyToOne
        @JoinColumn(name = "buyer_id", nullable = false)
        private UserDetail buyer;

        @ManyToOne
        @JoinColumn(name = "crop_id", nullable = false)
        private Crop crop;

        @Column(nullable = false)
        private Integer quantity;

        @Column(name = "unit_price", nullable = false)
        private BigDecimal unitPrice;

        @Column(name = "total_price", nullable = false)
        private BigDecimal totalPrice;

        @Column(nullable = false, length = 20)
        private CropUnit unit;

        @Column(name = "order_date", nullable = false)
        private LocalDateTime orderDate;

        @Column(nullable = false, length = 20)
        private OrderStatus status;

}
