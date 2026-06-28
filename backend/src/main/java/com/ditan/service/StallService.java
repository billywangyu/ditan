package com.ditan.service;

import com.ditan.dto.StallRequest;
import com.ditan.dto.StallResponse;
import com.ditan.entity.Stall;
import com.ditan.repository.StallRepository;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StallService {

    private final StallRepository stallRepository;
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    public StallService(StallRepository stallRepository) {
        this.stallRepository = stallRepository;
    }

    @Transactional
    public StallResponse createStall(StallRequest request) {
        Stall stall = new Stall();
        stall.setTitle(request.getTitle());
        stall.setCategory(request.getCategory());
        stall.setLocation(geometryFactory.createPoint(new Coordinate(request.getLongitude(), request.getLatitude())));
        stall.setAddress(request.getAddress());
        stall.setOwnerId(request.getOwnerId()); // 把前端传来的用户ID存进去
        stall = stallRepository.save(stall);
        return StallResponse.fromEntity(stall);
    }

    public List<StallResponse> getNearbyStalls(double lng, double lat, double radius) {
        return stallRepository.findNearby(lng, lat, radius).stream()
                .map(StallResponse::fromEntity)
                .collect(Collectors.toList());
    }
}