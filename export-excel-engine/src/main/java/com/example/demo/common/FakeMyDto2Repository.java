package com.example.demo.common;

import com.example.demo.dto.MyDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class FakeMyDto2Repository {

    private static final List<MyDto> DB = new ArrayList<>();

    static {
        Random random = new Random();

        for (long i = 1; i <= 10_000; i++) {
            DB.add(new MyDto(
                    i,
                    "user_" + i,
                    random.nextInt(60) + 18,
                    random.nextLong(1_000_000),
                    BigDecimal.valueOf(random.nextDouble() * 10000),
                    random.nextBoolean(),
                    LocalDate.now().minusDays(random.nextInt(10000)),
                    LocalDateTime.now().minusMinutes(random.nextInt(100000)),
                    random.nextDouble() * 100,
                    "note_" + i,
                    "09" + String.format("%08d", random.nextInt(100_000_000))
            ));
        }
    }

    public List<MyDto> fetchBatch(long lastId, int size) {

        int startIndex = (int) lastId; // vì id = index giả lập

        if (startIndex >= DB.size())
            return Collections.emptyList();

        int endIndex = Math.min(startIndex + size, DB.size());

        return DB.subList(startIndex, endIndex);
    }
}
