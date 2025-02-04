package org.example;

import java.util.List;

public interface Service {
    @Cache(cacheType = CacheType.IN_MEMORY, maxListSize = 100_000)
    List<String> work(String item);
}