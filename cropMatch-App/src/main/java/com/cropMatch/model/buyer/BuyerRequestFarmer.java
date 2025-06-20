package com.cropMatch.model.buyer;

import com.cropMatch.enums.RequestStatus;
import com.cropMatch.enums.ResponseStatus;
import com.cropMatch.model.farmer.Crop;
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

        @Column(name = "is_selected", nullable = false)
        private boolean isSelected = false;

        @Column(name = "responded_on")
        private LocalDateTime respondedOn;

        @Enumerated(EnumType.STRING)
        @Column(name = "buyer_offer_status", nullable = false)
        private ResponseStatus farmerStatus = ResponseStatus.PENDING;

        @Column(name = "sent_on", nullable = false)
        private LocalDateTime sentOn = LocalDateTime.now();

        @ManyToOne
        @JoinColumn(name = "crop_id")
        private Crop crop;

}

