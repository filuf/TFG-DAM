package com.slotify.backend.spring.reserve.components;

import com.slotify.backend.spring.exceptions.ReservationConflictException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class RedisLockReserveExecutor {

    private final RedisLockReserveManager redisManager;

    public void userLock(String userId, int ttl, Runnable task) {
        boolean locked = false;

        try {
            locked = redisManager.acquireUserLock(userId, ttl);
            if (!locked) {
                throw new ReservationConflictException("No se puede realizar más de una reserva de forma concurrente");
            }

            task.run();

        } finally {
            if (locked) {
                redisManager.releaseUserLock(userId);
            }
        }
    }

    public <T> T userLock(String userId, int ttl, Supplier<T> task) {
        boolean locked = false;

        try {
            locked = redisManager.acquireUserLock(userId, ttl);
            if (!locked) {
                throw new ReservationConflictException("No se puede realizar más de una reserva de forma concurrente");
            }

            return task.get();

        } finally {
            if (locked) {
                redisManager.releaseUserLock(userId);
            }
        }
    }

    public void companyLock(String companyId, int ttl, Runnable task) {
        boolean locked = false;

        try {
            locked = redisManager.acquireCompanyLock(companyId, ttl);

            task.run();

        } finally {
            if (locked) {
                redisManager.releaseCompanyLock(companyId);
            }
        }
    }

    public <T> T companyLock(String companyId, int ttl, Supplier<T> task) {
        boolean locked = false;

        try {
            locked = redisManager.acquireCompanyLock(companyId, ttl);

            return task.get();

        } finally {
            if (locked) {
                redisManager.releaseCompanyLock(companyId);
            }
        }
    }
}