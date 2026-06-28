package com.ditan.repository;

import com.ditan.entity.Stall;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface StallRepository extends JpaRepository<Stall, Long> {

    @Query(value = "SELECT s.*, ST_Distance(s.location, CAST(ST_SetSRID(ST_MakePoint(:lng, :lat), 4326) AS geography)) AS dist " +
                   "FROM stall s " +
                   "WHERE ST_DWithin(s.location, CAST(ST_SetSRID(ST_MakePoint(:lng, :lat), 4326) AS geography), :radius) " +
                   "AND s.status = 'OPEN' ORDER BY dist", nativeQuery = true)
    List<Stall> findNearby(@Param("lng") double lng,
                           @Param("lat") double lat,
                           @Param("radius") double radius);
}