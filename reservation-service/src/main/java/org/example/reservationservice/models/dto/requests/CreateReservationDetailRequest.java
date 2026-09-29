package org.example.reservationservice.models.dto.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateReservationDetailRequest(
        @NotNull(message = "Khong duoc trong ID")
        @Min(value = 1, message = "ID bắt đầu từ 1")
        Long roomId,

        @NotNull(message = "Khong duoc de trong Nights")
        @Min(value = 1, message = "Nights phai lon hon 0")
        Integer nights
) {
}
