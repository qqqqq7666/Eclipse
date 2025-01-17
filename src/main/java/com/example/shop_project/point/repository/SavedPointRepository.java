package com.example.shop_project.point.repository;

import com.example.shop_project.point.entity.Point;
import com.example.shop_project.point.entity.SavedPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SavedPointRepository extends JpaRepository<SavedPoint, Long> {
    List<SavedPoint> findAllByPoint(Point point);
    @Query("select sum(savedPoint) from SavedPoint where point = :point")
    Integer findTotalSavedPoint(Point point);
}
