package io.dev.tdd.point.repository.impl;

import io.dev.tdd.database.UserPointTable;
import io.dev.tdd.point.UserPoint;
import io.dev.tdd.point.repository.UserPointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class UserPointRepositoryImpl implements UserPointRepository {
    private final UserPointTable userPointTable;

//    public UserPointRepositoryImpl(UserPointTable userPointTable) {
//        this.userPointTable = userPointTable;
//    }

    @Override
    public UserPoint selectById(long id) {
        return userPointTable.selectById(id);
    }

    @Override
    public UserPoint insertOrUpdate(long id, long amount) {
        return userPointTable.insertOrUpdate(id, amount);
    }
}
