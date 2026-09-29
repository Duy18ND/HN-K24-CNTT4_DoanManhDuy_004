package org.example.reservationservice.models.dto.requests;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;

import java.util.List;

public record CreateReservationRequest(
        @NotBlank(message = "Customer name khong trung lap")
        @Size(max = 255, message = "Customer khong lon hon 255")
        String customerName,

        @NotBlank(message = "Customer email khong trung lap")
        @Email(message = "Customer email sai dinh dang")
        String customerEmail,

        @NotEmpty(message = "items khong trong")
        List<@Valid CreateReservationDetailRequest> items
) {
}
