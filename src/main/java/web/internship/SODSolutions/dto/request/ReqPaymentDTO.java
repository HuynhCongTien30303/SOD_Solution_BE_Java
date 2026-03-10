package web.internship.SODSolutions.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import web.internship.SODSolutions.model.common.PaymentStatus;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class ReqPaymentDTO {
    Long id;
    Long projectPhaseId;
    LocalDate paymentDate;
    PaymentStatus paymentStatus;
    String transactionId;
}
