package io.dev.tdd.point.service;

import io.dev.tdd.point.PointHistory;
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

//    public PointService(UserPointRository userPointRository,
//                        PointHistoryRepository pointHistoryRepository) {
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

        ReentrantLock lock = userMap.computeIfAbsent(id, k -> new ReentrantLock());
        lock.lock();

        try {
            // 현재 포인트
            UserPoint current = UserPointRepository.selectById(id);
            long updateAmount = current.point() + amount;
            // 업데이트
            UserPoint resultUser = UserPointRepository.insertOrUpdate(id, updateAmount);
            // 포인트 내역 insert
            pointHistoryRepository.insert(id, amount, TransactionType.CHARGE, System.currentTimeMillis());
            return resultUser;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 포인트 사용
     */
    public UserPoint use(long userId, long amount) {

        ReentrantLock lock = userMap.computeIfAbsent(userId, k -> new ReentrantLock());
        lock.lock();

        try {
            // 현재 포인트 조회
            UserPoint current = UserPointRepository.selectById(userId);
            long currnetPoint = current.point();

            // 잔고부족
            if (currnetPoint < amount) {
                // 포인트 사용 실패
                throw new RuntimeException("잔고가 부족합니다.");
            }
            long updateAmount = currnetPoint - amount;
            // 업데이트
            UserPoint resultUser = UserPointRepository.insertOrUpdate(userId, updateAmount);
            // 포인트 내역 insert
            pointHistoryRepository.insert(userId, amount, TransactionType.USE, System.currentTimeMillis());
            return resultUser;
        } finally {
            lock.unlock();
        }
    }


}
