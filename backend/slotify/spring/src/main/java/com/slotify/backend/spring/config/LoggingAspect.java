package com.slotify.backend.spring.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Around("execution(* com.slotify.backend.spring..*.*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        // Obtenemos el nombre de la clase y el método
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        log.info("==> [AOP] Ejecutando: {}.{} | Params: {}", className, methodName, Arrays.toString(args));

        try {
            Object proceed = joinPoint.proceed();

            long executionTime = System.currentTimeMillis() - start;
            log.info("<== [AOP] Finalizado: {}.{} | Tiempo: {}ms", className, methodName, executionTime);

            return proceed;
        } catch (Throwable throwable) {
            log.error("!!! [AOP] Error en {}.{}: {}", className, methodName, throwable.getMessage());
            throw throwable;
        }
    }
}