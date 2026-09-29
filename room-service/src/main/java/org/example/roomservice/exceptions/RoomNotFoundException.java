package org.example.roomservice.exceptions;

public class RoomNotFoundException extends RuntimeException {
    public RoomNotFoundException(Long id) {
        super("Khong tim thay phong: " + id);
    }
}
