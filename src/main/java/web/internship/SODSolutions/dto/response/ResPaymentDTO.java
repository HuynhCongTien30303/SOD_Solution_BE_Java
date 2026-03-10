package web.internship.SODSolutions.dto.response;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import web.internship.SODSolutions.model.common.PaymentStatus;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class ResPaymentDTO {
    Instant createdAt;
    Instant updatedAt;
    String createdBy;
    String updatedBy;
    Long id;
    LocalDate paymentDate;
    PaymentStatus paymentStatus;
    String transactionId;
    Long projectPhaseId;
}