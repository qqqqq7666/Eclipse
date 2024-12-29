package com.example.shop_project.point.service;

import com.example.shop_project.member.entity.Member;
import com.example.shop_project.member.repository.MemberRepository;
import com.example.shop_project.order.entity.Order;
import com.example.shop_project.order.repository.OrderRepository;
import com.example.shop_project.point.dto.*;
import com.example.shop_project.point.entity.Point;
import com.example.shop_project.point.entity.SavedPoint;
import com.example.shop_project.point.entity.UsedPoint;
import com.example.shop_project.point.mapper.PointMapper;
import com.example.shop_project.point.repository.PointRepository;
import com.example.shop_project.point.repository.SavedPointRepository;
import com.example.shop_project.point.repository.UsedPointRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
public class PointService {
    @Autowired
    private PointRepository pointRepository;
    @Autowired
    private UsedPointRepository usedPointRepository;
    @Autowired
    private SavedPointRepository savedPointRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PointMapper pointMapper;
    @Autowired
    private OrderRepository orderRepository;

    // 회원가입할 때 같이 생성
    public void createPoint(Long memberId) {
        Point point = Point.builder()
                .member(memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다.")))
                .build();
        pointRepository.save(point);
    }

    public PointDto getPointByMember(String email) {
        Point point = findPointByEmail(email);
        return pointMapper.toDto(point);
    }

    public PointDto createSavedPoint(SavedPointRequestDto requestDto) {
        Point point = findPointByEmail(requestDto.getEmail());

        point.savePoint(pointMapper.toEntity(requestDto));
        return pointMapper.toDto(pointRepository.save(point));
    }

    public PointDto createUsedPoint(UsedPointRequestDto requestDto) {
        Point point = findPointByEmail(requestDto.getEmail());
        point.usePoint(pointMapper.toEntity(requestDto, orderRepository));
        return pointMapper.toDto(pointRepository.save(point));
    }

    public Point findPointByEmail(String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        return pointRepository.findByMember(member).orElseThrow(() -> new IllegalArgumentException("포인트가 존재하지 않습니다."));
    }

    public UsedPointResponseDto getPointByOrderNo(Long orderNo) {
        Order order = orderRepository.findByOrderNo(orderNo).orElseThrow(() -> new IllegalArgumentException("주문이 존재하지 않습니다."));
        UsedPoint usedPoint = usedPointRepository.findByOrder(order).orElseThrow(() -> new IllegalArgumentException("사용된 포인트 내역이 존재하지 않습니다."));

        return pointMapper.toResponseDto(usedPoint);
    }

    public List<PointHistoryDto> getPointList(String email, String category) {
        Point point = findPointByEmail(email);
        List<PointHistoryDto> savedPointHistoryDtoList = new ArrayList<>();
        List<PointHistoryDto> usedPointHistoryDtoList = new ArrayList<>();
        List<SavedPoint> savedPointList = savedPointRepository.findAllByPoint(point);
        List<UsedPoint> usedPointList = usedPointRepository.findAllByPoint(point);
        savedPointList.forEach(savedPoint -> {
            savedPointHistoryDtoList.add(PointHistoryDto.builder()
                    .createdDate(savedPoint.getCreatedDate())
                    .amount(savedPoint.getSavedPoint())
                    .reason(savedPoint.getSaveReason())
                    .isUsed(false)
                    .build());
        });
        usedPointList.forEach(usedPoint -> {
            usedPointHistoryDtoList.add(PointHistoryDto.builder()
                    .amount(usedPoint.getAmount())
                    .createdDate(usedPoint.getCreatedDate())
                    .order(usedPoint.getOrder())
                            .reason(usedPoint.getOrder().getOrderDetailList().getFirst().getProduct().getProductName())
                    .isUsed(true)
                    .build());
        });
        if(category.equals("all")) {
            List<PointHistoryDto> pointHistoryDtoList = new ArrayList<>();
            pointHistoryDtoList.addAll(savedPointHistoryDtoList);
            pointHistoryDtoList.addAll(usedPointHistoryDtoList);
            pointHistoryDtoList.sort(Comparator.comparing(PointHistoryDto::getCreatedDate).reversed());
            return pointHistoryDtoList;
        }

        if(category.equals("save"))
            return savedPointHistoryDtoList.reversed();
        if(category.equals("use"))
            return usedPointHistoryDtoList.reversed();

        return null;
    }
}