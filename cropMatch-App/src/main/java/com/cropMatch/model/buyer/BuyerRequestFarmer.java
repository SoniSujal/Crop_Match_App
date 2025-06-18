package com.cropMatch.model.buyer;

import com.cropMatch.enums.RequestStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "buyer_request_farmers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BuyerRequestFarmer {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer id;

        @ManyToOne
        @JoinColumn(name = "buyer_request_id", nullable = false)
        private BuyerRequest buyerRequest;

        @Column(name = "farmer_id", nullable = false)
        private Integer farmerId;

        @Column(name = "is_read", nullable = false)
        private boolean isRead = false;

        @Column(name = "responded_on")
        private LocalDateTime respondedOn;

        @Enumerated(EnumType.STRING)
        @Column(name = "farmer_status", nullable = false)
        private RequestStatus farmerStatus = RequestStatus.PENDING;

        @Column(name = "sent_on", nullable = false)
        private LocalDateTime sentOn = LocalDateTime.now();

}
