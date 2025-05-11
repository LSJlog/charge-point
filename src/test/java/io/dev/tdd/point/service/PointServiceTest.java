package io.dev.tdd.point.service;

import io.dev.tdd.point.PointHistory;
import io.dev.tdd.point.PointValidator;
import io.dev.tdd.point.TransactionType;
import io.dev.tdd.point.UserPoint;
import io.dev.tdd.point.repository.impl.PointHistoryRepositoryImpl;
import io.dev.tdd.point.repository.impl.UserPointRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PointServiceTest {

    @InjectMocks
    private PointService pointService;

    @Mock
    private UserPointRepositoryImpl userPointRepository;
    @Mock
    private PointHistoryRepositoryImpl pointHistoryRepository;
    @Spy
    private PointValidator pointValidator;

    private UserPoint userPoint;

    @BeforeEach
    void setUp() {
        userPoint = new UserPoint(1L, 1000L, System.currentTimeMillis());
    }

    @Test
    @DisplayName("사용자 포인트 조회")
    void getPointTest() throws Exception {
        //given
        when(userPointRepository.selectById(1L)).thenReturn(userPoint);

        //when
        UserPoint result = pointService.getPoint(1L);

        //then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.point()).isEqualTo(1000L);
    }

    @Test
    @DisplayName("사용자 포인트 내역 조회")
    void getPointHistoryTest() {
        //given
        PointHistory pointHistory = new PointHistory(1L, 1L, 1000L, TransactionType.CHARGE, System.currentTimeMillis());
        when(pointHistoryRepository.selectAllByUserId(1L)).thenReturn(Arrays.asList(pointHistory));

        //when
        List<PointHistory> result = pointService.getHistories(1L);

        //then
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("포인트 충전")
    void chargeTest() throws Exception {
        //given
        when(userPointRepository.selectById(1L)).thenReturn(userPoint);
        when(userPointRepository.insertOrUpdate(1L, 1500L)).thenReturn(new UserPoint(1L, 1500L, System.currentTimeMillis()));

        //when
        UserPoint result = pointService.charge(1L, 500L);

        //then
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.point()).isEqualTo(1500L);
    }

    @Test
    @DisplayName("[실패] 포인트 충전")
    public void charge_error_Test(){
        // when & then
        assertThatThrownBy(() -> pointService.charge(1L, 0L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("[성공] 포인트 사용")
    void useTest() throws Exception {
        //given
        when(userPointRepository.selectById(1L)).thenReturn(userPoint);
        when(userPointRepository.insertOrUpdate(1L, 800L)).thenReturn(new UserPoint(1L, 800L, System.currentTimeMillis()));

        //when
        UserPoint result = pointService.use(1L, 200L);

        //then
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.point()).isEqualTo(800L);
    }

    @Test
    @DisplayName("[실패] 포인트 사용")
    void use_error_Test() throws Exception {
        // when & then
        assertThatThrownBy(() -> pointService.use(1L, 10000L))
                .isInstanceOf(IllegalArgumentException.class);
    }

}