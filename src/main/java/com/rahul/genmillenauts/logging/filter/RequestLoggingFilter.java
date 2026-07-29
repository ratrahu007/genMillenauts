package com.rahul.genmillenauts.logging.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class RequestLoggingFilter implements Filter {

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        String method = httpRequest.getMethod();
        String uri = httpRequest.getRequestURI();

        long startTime = System.currentTimeMillis();

        log.info("Incoming Request : {} {}", method, uri);

        try {

            chain.doFilter(request, response);

        } finally {

            long executionTime = System.currentTimeMillis() - startTime;

            log.info(
                    "Completed Request : {} {} | Execution Time : {} ms",
                    method,
                    uri,
                    executionTime
            );
        }
    }
}