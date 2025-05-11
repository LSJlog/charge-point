package io.dev.tdd.point.service;

import io.dev.tdd.point.PointHistory;
import io.dev.tdd.point.PointValidator;
import io.dev.tdd.point.TransactionType;
import io.dev.tdd.point.UserPoint;
import io.dev.tdd.point.repository.PointHistoryRepository;
import io.dev.tdd.point.repository.UserPointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@RequiredArgsConstructor
@Service
public class PointService {

    private final UserPointRepository UserPointRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final ConcurrentHashMap<Long, ReentrantLock> userMap = new ConcurrentHashMap<>();
    private final PointValidator pointValidator;

//    public PointService(UserPointRository userPointRository, PointHistoryRepository pointHistoryRepository) {
//        this.userPointRository = userPointRository;
//        this.pointHistoryRepository = pointHistoryRepository;
//    }

    /**
     * 포인트 조회
     * @param id
     * @return
     */
    public UserPoint getPoint(long id) {
        return UserPointRepository.selectById(id);
    }

    /**
     * 포인트 내역 조회
     * @param userId
     * @return
     */
    public List<PointHistory> getHistories(long userId) {
        return pointHistoryRepository.selectAllByUserId(userId);
    }

    /**
     * 포인트 충전
     * @param id
     * @param amount
     * @return
     */
    public UserPoint charge(long id, long amount) {
        // 동시성제어
        ReentrantLock lock = userMap.computeIfAbsent(id, k -> new ReentrantLock());
        lock.lock();

        try {
            // 충전금액검증
            long chargeAmount = pointValidator.verifyChargeAmount(amount);

            // 현재 포인트
            UserPoint balance = UserPointRepository.selectById(id);

            // 포인트 수정
            long updateAmount = balance.point() + chargeAmount;

            // 보유가능한 최대/최소 포인트 검증
            long verifiedChangePoint = pointValidator.verifyChangePoint(updateAmount);

            // 포인트 update
            UserPoint resultUser = UserPointRepository.insertOrUpdate(id, verifiedChangePoint);

            // 포인트 내역 insert
            pointHistoryRepository.insert(id, verifiedChangePoint, TransactionType.CHARGE, System.currentTimeMillis());
            return resultUser;

        } finally {
            lock.unlock();
        }
    }

    /**
     * 포인트 사용
     * @param userId
     * @param amount
     * @return
     */
    public UserPoint use(long userId, long amount) {
        // 동시성제어
        ReentrantLock lock = userMap.computeIfAbsent(userId, k -> new ReentrantLock());
        lock.lock();

        try {
            // 사용금액검증
            long verifiedUseAmount = pointValidator.verifyUseAmount(amount);

            // 현재 포인트 조회
            UserPoint current = UserPointRepository.selectById(userId);
            long balance = current.point();

            // 잔고검증
            long verifiedUseBalance = pointValidator.verifyUseBalance(verifiedUseAmount, balance);

            // 보유가능한 최대/최소 포인트 검증
            long verifiedChangePoint = pointValidator.verifyChangePoint(verifiedUseBalance);

            // 포인트 update
            UserPoint resultUser = UserPointRepository.insertOrUpdate(userId, verifiedChangePoint);

            // 포인트 내역 insert
            pointHistoryRepository.insert(userId, amount, TransactionType.USE, System.currentTimeMillis());

            return resultUser;

        } finally {
            lock.unlock();
        }
    }


}
