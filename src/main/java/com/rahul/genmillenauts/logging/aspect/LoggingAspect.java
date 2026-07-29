package com.rahul.genmillenauts.logging.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    /**
     * Intercepts all methods inside classes annotated with @Service.
     */
    @Pointcut("@within(org.springframework.stereotype.Service)")
    public void serviceLayer() {
        // Pointcut method
    }

    /**
     * Logs service method entry, exit, execution time, and exceptions.
     */
    @Around("serviceLayer()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {

        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        long startTime = System.currentTimeMillis();

        log.info("Entering {}.{}()", className, methodName);

        try {

            Object result = joinPoint.proceed();

            log.info("Successfully executed {}.{}()", className, methodName);

            return result;

        } catch (Throwable ex) {

            log.error(
                    "Exception in {}.{}() : {}",
                    className,
                    methodName,
                    ex.getMessage(),
                    ex
            );

            throw ex;

        } finally {

            long executionTime = System.currentTimeMillis() - startTime;

            log.info(
                    "Exiting {}.{}() | Execution Time: {} ms",
                    className,
                    methodName,
                    executionTime
            );
        }
    }
}