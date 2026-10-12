
package com.rahul.genmillenauts.logging.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
public class RequestLoggingFilter implements Filter {

    private static final String CORRELATION_ID = "correlationId";
    private static final String HEADER_NAME = "X-Correlation-ID";

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Generate a server-controlled ID for each request.
        String correlationId = UUID.randomUUID().toString();

        long startTime = System.currentTimeMillis();

        try {
            MDC.put(CORRELATION_ID, correlationId);
            httpResponse.setHeader(HEADER_NAME, correlationId);

            log.info(
                    "Incoming request: {} {}",
                    httpRequest.getMethod(),
                    httpRequest.getRequestURI()
            );

            chain.doFilter(request, response);

        } finally {
            long executionTime =
                    System.currentTimeMillis() - startTime;

            log.info(
                    "Completed request: {} {} | Status: {} | Duration: {} ms",
                    httpRequest.getMethod(),
                    httpRequest.getRequestURI(),
                    httpResponse.getStatus(),
                    executionTime
            );

            MDC.remove(CORRELATION_ID);
        }
    }
}
