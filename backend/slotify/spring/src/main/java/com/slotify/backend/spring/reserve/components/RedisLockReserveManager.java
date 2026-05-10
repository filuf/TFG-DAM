package com.slotify.backend.spring.reserve.components;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RedisLockReserveManager {
    private static final String COMPANY_LOCK_PREFIX = "lock:company:";
    private static final String USER_LOCK_PREFIX = "lock:user:reserving:";


    private final StringRedisTemplate redisTemplate;

    public boolean acquireCompanyLock(String companyId, int ttl) {
        return Boolean.TRUE.equals(redisTemplate.opsForValue()
                .setIfAbsent(COMPANY_LOCK_PREFIX + companyId, "locked", Duration.ofSeconds(ttl)));
    }

    public void releaseCompanyLock(String companyId) {
        redisTemplate.delete(COMPANY_LOCK_PREFIX + companyId);
    }

    /**
     * Intenta adquirir el bloqueo de la empresa esperando si es necesario.
     * @param companyId ID de la empresa
     * @param lockTtl Segundos que durará el candado una vez obtenido
     * @param waitTimeoutSeconds Tiempo máximo que el hilo esperará intentando obtener el lock
     * @return true si se obtuvo, false si se acabó el tiempo de espera
     */
    public boolean acquireCompanyLockWithWait(String companyId, int lockTtl, long waitTimeoutSeconds) {
        long endTime = System.currentTimeMillis() + (waitTimeoutSeconds * 1000);

        while (System.currentTimeMillis() < endTime) {
            if (acquireCompanyLock(companyId, lockTtl)) {
                return true;
            }

            try {
                Thread.sleep(40);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return false;
    }

    public boolean acquireUserLock(String userId, int ttl) {
        return Boolean.TRUE.equals(redisTemplate.opsForValue()
                .setIfAbsent(USER_LOCK_PREFIX + userId, "locked", Duration.ofSeconds(ttl)));
    }

    public void releaseUserLock(String userId) {
        redisTemplate.delete(USER_LOCK_PREFIX + userId);
    }


}
