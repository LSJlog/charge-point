package io.dev.tdd.point.controller;

import io.dev.tdd.point.PointHistory;
import io.dev.tdd.point.TransactionType;
import io.dev.tdd.point.UserPoint;
import io.dev.tdd.point.repository.PointHistoryRepository;
import io.dev.tdd.point.repository.UserPointRepository;
import io.dev.tdd.point.service.PointService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class PointControllerTest {

    private PointService pointService;
    private UserPointRepository userPointRepository;
    private PointHistoryRepository pointHistoryRepository;

    @BeforeEach
    void setUp() {
        userPointRepository = mock(UserPointRepository.class);
        pointHistoryRepository = mock(PointHistoryRepository.class);
        pointService = new PointService(userPointRepository, pointHistoryRepository);
    }

    /**
     * 포인트 조회
     * @throws Exception
     */
    @Test
    void getPoint() throws Exception {
        long userId = 1L;
        UserPoint userPoint = new UserPoint(userId, 0, System.currentTimeMillis());

        when(userPointRepository.selectById(userId)).thenReturn(userPoint);

        UserPoint result = pointService.getPoint(userId);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(userId);
        assertThat(result.point()).isEqualTo(0);
    }

    /**
     * 포인트 충전
     * @throws Exception
     */
    @Test
    void chargePoint() throws Exception {
        long userId = 1L;
        long amount = 500L;
        UserPoint current = new UserPoint(userId, 0L, System.currentTimeMillis());
        UserPoint updated = new UserPoint(userId, 500L, System.currentTimeMillis());

        when(userPointRepository.selectById(userId)).thenReturn(current);
        when(userPointRepository.insertOrUpdate(userId, 500L)).thenReturn(updated);

        UserPoint result = pointService.charge(userId, amount);

        assertThat(result.point()).isEqualTo(500L);
        verify(pointHistoryRepository).insert(eq(userId), eq(amount), eq(TransactionType.CHARGE), anyLong());
    }

    /**
     * 포인트 사용
     * @throws Exception
     */
    @Test
    void usePoint_success() throws Exception {
        long userId = 1L;
        long amount = 300L;
        UserPoint current = new UserPoint(userId, 1000L, System.currentTimeMillis());
        UserPoint updated = new UserPoint(userId, 700L, System.currentTimeMillis());

        when(userPointRepository.selectById(userId)).thenReturn(current);
        when(userPointRepository.insertOrUpdate(userId, 700L)).thenReturn(updated);

        UserPoint result = pointService.use(userId, amount);

        assertThat(result.point()).isEqualTo(700L);
        verify(pointHistoryRepository).insert(eq(userId), eq(amount), eq(TransactionType.USE), anyLong());
    }

    /**
     * 포인트 사용 실패
     * @throws Exception
     */
    @Test
    void usePoint_fail() throws Exception {
        long userId = 1L;
        long amount = 1500L;
        UserPoint current = new UserPoint(userId, 1000L, System.currentTimeMillis());

        when(userPointRepository.selectById(userId)).thenReturn(current);

        assertThatThrownBy(() -> pointService.use(userId, amount))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("잔고가 부족합니다.");
    }

    /**
     * 포인트 내역 조회
     * @throws Exception
     */
    @Test
    void getPointHistory() {
        long userId = 1L;
        List<PointHistory> histories = List.of(
                new PointHistory(1L, userId, 500L, TransactionType.CHARGE, System.currentTimeMillis()),
                new PointHistory(2L, userId, 300L, TransactionType.USE, System.currentTimeMillis())
        );

        when(pointHistoryRepository.selectAllByUserId(userId)).thenReturn(histories);

        List<PointHistory> result = pointService.getHistories(userId);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).type()).isEqualTo(TransactionType.CHARGE);
        assertThat(result.get(1).type()).isEqualTo(TransactionType.USE);
    }

}