package org.example.reservationservice.models.services.impl;

import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.example.reservationservice.clients.RoomClient;
import org.example.reservationservice.exceptions.RoomNotFoundException;
import org.example.reservationservice.exceptions.RoomServiceException;
import org.example.reservationservice.models.dto.responses.RoomResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.example.reservationservice.exceptions.RoomNotFoundException;
import org.example.reservationservice.exceptions.RoomServiceException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoomGatewayService {

    private final RoomClient roomClient;

        @CircuitBreaker(name = "roomService", fallbackMethod = "getRoomByIdFallback")
    public RoomResponse getRoomById(Long roomId) {
        return roomClient.getRoomById(roomId);
    }

    public RoomResponse getRoomByIdFallback(Long roomId, Throwable throwable) {
        if (throwable instanceof feign.FeignException.NotFound) {
            throw new RoomNotFoundException(roomId);
        }
        throw new RoomServiceException("Khong tim thay phong", throwable);
    }
}
