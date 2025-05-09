package io.dev.tdd.point.repository;

import io.dev.tdd.point.PointHistory;
import io.dev.tdd.point.TransactionType;

import java.util.List;

public interface PointHistoryRepository {
    PointHistory insert(long userId, long amount, TransactionType type, long updateMillis);
    List<PointHistory> selectAllByUserId(long userId);
}
