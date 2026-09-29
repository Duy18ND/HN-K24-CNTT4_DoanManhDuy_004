package org.example.reservationservice.exceptions;

public class RoomNotFoundException extends RuntimeException {

    public RoomNotFoundException(Long roomId) {
        super("Khong tim thay phong id: " + roomId);
    }
}
