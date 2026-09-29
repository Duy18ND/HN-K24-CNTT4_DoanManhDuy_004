package org.example.reservationservice.models.services.impl;

import lombok.RequiredArgsConstructor;
import org.example.reservationservice.models.constants.ReservationStatus;
import org.example.reservationservice.models.dto.requests.CreateReservationDetailRequest;
import org.example.reservationservice.models.dto.requests.CreateReservationRequest;
import org.example.reservationservice.models.dto.responses.ReservationDetailResponse;
import org.example.reservationservice.models.dto.responses.ReservationResponse;
import org.example.reservationservice.models.dto.responses.RoomResponse;
import org.example.reservationservice.models.entities.Reservation;
import org.example.reservationservice.models.entities.ReservationDetail;
import org.example.reservationservice.models.repositories.ReservationDetailRepository;
import org.example.reservationservice.models.repositories.ReservationRepository;
import org.example.reservationservice.models.services.ReservationService;
import org.jspecify.annotations.NonNull;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;
import java.util.ArrayList;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.example.reservationservice.models.entities.*;
import org.example.reservationservice.models.dto.responses.*;
import org.example.reservationservice.models.constants.*;

import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationDetailRepository reservationDetailRepository;
    private final RoomGatewayService roomGatewayService;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Override
    @Transactional
    public ReservationResponse createReservation(CreateReservationRequest request) {
        Reservation tx = Reservation.builder().customerName(request.customerName()).status(ReservationStatus.PENDING).totalAmount(0.0).build();

        tx = reservationRepository.save(tx);

        double total = 0.0;
        List<ReservationDetail> details = new ArrayList<>();
        List<ReservationDetailResponse> detailResponses = new ArrayList<>();

        for (var item : request.items()) {
            RoomResponse res = roomGatewayService.getRoomById(item.roomId());

            double subtotal = res.pricePerNight() * item.nights();
            total += subtotal;

            ReservationDetail detail = ReservationDetail.builder().reservation(tx).roomId(res.roomId()).nights(item.nights()).pricePerNight(res.pricePerNight()).build();
            details.add(detail);

            detailResponses.add(new ReservationDetailResponse(null, res.roomId(), res.roomNumber(), res.roomType(), item.nights(), res.pricePerNight(), subtotal));
        }

        List<ReservationDetail> savedDetails = reservationDetailRepository.saveAll(details);

        for (int i = 0; i < savedDetails.size(); i++) {
            ReservationDetail saved = savedDetails.get(i);
            ReservationDetailResponse resp = detailResponses.get(i);
            detailResponses.set(i, new ReservationDetailResponse(saved.getId(), resp.roomId(), resp.roomNumber(), resp.roomType(), resp.nights(), resp.pricePerNight(), resp.lineTotal()));
        }

        tx.setTotalAmount(total);
        tx = reservationRepository.save(tx);

        kafkaTemplate.send("reservation-created", request.customerEmail());

        return new ReservationResponse(tx.getId(), tx.getCustomerName(), tx.getCustomerEmail(), tx.getTotalAmount(), tx.getStatus(), detailResponses);
    }
}
