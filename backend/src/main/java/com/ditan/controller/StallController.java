package com.ditan.controller;

import com.ditan.dto.StallRequest;
import com.ditan.dto.StallResponse;
import com.ditan.service.StallService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/stalls")
public class StallController {

    private final StallService stallService;

    public StallController(StallService stallService) {
        this.stallService = stallService;
    }

    @PostMapping
    public ResponseEntity<StallResponse> create(@Valid @RequestBody StallRequest request) {
        return ResponseEntity.ok(stallService.createStall(request));
    }

    @GetMapping("/nearby")
    public ResponseEntity<List<StallResponse>> nearby(
            @RequestParam double lng,
            @RequestParam double lat,
            @RequestParam(defaultValue = "5000") double radius) {
        return ResponseEntity.ok(stallService.getNearbyStalls(lng, lat, radius));
    }

    // 新增：获取全部摊位列表接口
    @GetMapping("/all")
    public ResponseEntity<List<StallResponse>> getAllStalls() {
        return ResponseEntity.ok(stallService.getAllStalls());
    }
}