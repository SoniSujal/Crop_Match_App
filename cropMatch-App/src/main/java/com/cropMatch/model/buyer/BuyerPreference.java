package com.cropMatch.model.buyer;

import com.cropMatch.model.admin.Category;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "buyer_preferences", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"buyer_id", "category_id"})
})
@Data
@NoArgsConstructor
public class BuyerPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "buyer_id", nullable = false)
    private Integer buyerId;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    public BuyerPreference(Integer buyerId, Category category) {
        this.buyerId = buyerId;
        this.category = category;
    }

}
