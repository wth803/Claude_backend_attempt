package com.example.customerapi.util;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerCodeGeneratorTest {

    @Test
    void singletonReturnsSameInstance() {
        CustomerCodeGenerator a = CustomerCodeGenerator.INSTANCE;
        CustomerCodeGenerator b = CustomerCodeGenerator.INSTANCE;
        assertThat(a).isSameAs(b);
    }

    @Test
    void generateCode_formatMatchesPattern() {
        String code = CustomerCodeGenerator.INSTANCE.generateCode(5);
        // format: yyyyMMddHHmmss-N  e.g. 20240601123045-5
        assertThat(code).matches("\\d{14}-5");
    }

    @Test
    void generateCode_differentCounts_produceDifferentCodes() {
        String code1 = CustomerCodeGenerator.INSTANCE.generateCode(1);
        String code2 = CustomerCodeGenerator.INSTANCE.generateCode(2);
        assertThat(code1).isNotEqualTo(code2);
    }

    @Test
    void threadSafety_uniqueCodes() throws InterruptedException {
        int threadCount = 50;
        Set<String> codes = Collections.synchronizedSet(new HashSet<>());
        CountDownLatch latch = new CountDownLatch(threadCount);
        ExecutorService executor = Executors.newFixedThreadPool(10);

        for (int i = 0; i < threadCount; i++) {
            final int count = i + 1;
            executor.submit(() -> {
                try {
                    codes.add(CustomerCodeGenerator.INSTANCE.generateCode(count));
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        executor.shutdown();

        assertThat(codes).hasSize(threadCount);
    }
}
