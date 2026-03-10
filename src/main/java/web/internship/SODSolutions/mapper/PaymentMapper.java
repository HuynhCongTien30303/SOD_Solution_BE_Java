package web.internship.SODSolutions.mapper;

import org.mapstruct.*;
import web.internship.SODSolutions.dto.request.ReqPaymentDTO;
import web.internship.SODSolutions.dto.response.ResPaymentDTO;
import web.internship.SODSolutions.model.Payment;
import web.internship.SODSolutions.model.common.PaymentStatus;
import web.internship.SODSolutions.util.error.AppException;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PaymentMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "projectPhase", ignore = true)
    @Mapping(target = "paymentStatus", source = "paymentStatus", qualifiedByName = "stringToPaymentStatus")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Payment toPayment(ReqPaymentDTO reqPaymentDTO);

    @Mapping(target = "paymentStatus", source = "paymentStatus", qualifiedByName = "paymentStatusToString")
    @Mapping(target = "projectPhaseId", source = "projectPhase.id")
    ResPaymentDTO toResPaymentDTO(Payment payment);

    List<ResPaymentDTO> toResPaymentDTO(List<Payment> payments);

    @Named("stringToPaymentStatus")
    default PaymentStatus stringToPaymentStatus(String paymentStatus) {
        if (paymentStatus == null) {
            return null;
        }
        try {
            return PaymentStatus.valueOf(paymentStatus.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new AppException("Invalid payment status: " + paymentStatus);
        }
    }

    @Named("paymentStatusToString")
    default String paymentStatusToString(PaymentStatus paymentStatus) {
        return paymentStatus != null ? paymentStatus.name() : null;
    }
}