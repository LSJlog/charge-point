package io.dev.tdd.point;

import org.springframework.stereotype.Component;

@Component
public class PointValidator {
    long MAX_BALANCE = 10000; // 최대 보유 포인트
    long MAX_AMOUNT = 5000; // 최대 사용/충전 포인트

    /**
     * 충전금액검증
     */
    public long verifyChargeAmount(long amount) {
        if(amount <= 0) {
            throw new IllegalArgumentException("0보다 큰 포인트을 충전해야 합니다.");
        }

        if(amount > MAX_AMOUNT) {
            throw new IllegalArgumentException("1회 최대 충전 한도는 5000포인트 입니다.");
        }

        return amount;
    }

    /**
     * 사용금액검증
     */
    public long verifyUseAmount(long amount) {
        if(amount <= 0 ) {
            throw new IllegalArgumentException("0보다 큰 포인트을 사용해야 합니다.");
        }

        if(amount > MAX_AMOUNT) {
            throw new IllegalArgumentException("1회 최대 사용 한도는 5000포인트 입니다.");
        }

        return amount;
    }

    /**
     * 잔고검증
     */
    public long verifyUseBalance(long amount, long balance) {
        if(amount > balance) {
            throw new IllegalArgumentException("보유 포인트가 부족합니다.");
        }
        return balance - amount;
    }

    /**
     * 보유가능한 최대/최소 포인트 검증
     */
    public long verifyChangePoint(long amount) {
        if(amount > MAX_BALANCE) {
            throw new IllegalArgumentException("최대 1만 포인트까지만 보유할 수 있습니다.");
        }
        if (amount < 0) {
            throw new IllegalArgumentException("보유 포인트보다 많이 사용할 수 없습니다.");
        }
        return amount;
    }
}
