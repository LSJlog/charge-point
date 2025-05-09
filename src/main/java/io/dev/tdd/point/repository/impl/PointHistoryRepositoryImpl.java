package io.dev.tdd.point.repository.impl;

import io.dev.tdd.database.PointHistoryTable;
import io.dev.tdd.point.PointHistory;
import io.dev.tdd.point.TransactionType;
import io.dev.tdd.point.repository.PointHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class PointHistoryRepositoryImpl implements PointHistoryRepository {
    private final PointHistoryTable pointHistoryTable;

//    public PointHistoryRepositoryImpl(PointHistoryTable pointHistoryTable) {
//        this.pointHistoryTable = pointHistoryTable;
//    }


    @Override
    public PointHistory insert(long userId, long amount, TransactionType type, long updateMillis) {
        return pointHistoryTable.insert(userId, amount, type, updateMillis);
    }

    @Override
    public List<PointHistory> selectAllByUserId(long userId) {
        return pointHistoryTable.selectAllByUserId(userId);
    }
}


