package io.dev.tdd.point.controller;

import io.dev.tdd.point.PointHistory;
import io.dev.tdd.point.UserPoint;
import io.dev.tdd.point.service.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/point")
public class PointController {

    private final PointService pointService;
//    public PointController(PointService pointService) {
//        this.pointService = pointService;
//    }

    /**
     * TODO - 포인트 조회
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public UserPoint getPoint(@PathVariable long id){
        UserPoint userPoint = pointService.getPoint(id);

        return userPoint; // // ResponseEntity.ok ->> 200 OK와 함께 객체 반환
    }

    /**
     * TODO - 포인트 내역 조회
     * @param id
     * @return
     */
    @GetMapping("{id}/histories")
    public List<PointHistory> getHistories(@PathVariable long id){
        List<PointHistory> pointHistories = pointService.getHistories(id);

        return pointHistories;
    }

    /**
     * TODO - 포인트 충전
     * @param id
     * @param amount
     * @return
     */
    @PatchMapping("{id}/charge")
    public UserPoint charge(@PathVariable long id, @RequestBody long amount){
        UserPoint resultUserPoint = pointService.charge(id, amount);
        return resultUserPoint;
    }

    /**
     * TODO - 포인트 사용
     * @param id
     * @param amount
     * @return
     */
    @PatchMapping("{id}/use")
    public UserPoint use(@PathVariable long id, @RequestBody long amount) {
        UserPoint resultUserPoint = pointService.use(id, amount);
        return resultUserPoint;
    }


}
