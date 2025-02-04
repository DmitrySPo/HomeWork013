package org.example;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CacheProxy implements InvocationHandler {
    private final Object target; // Объект, для которого создается прокси
    private final Map<MethodKey, Lock> locks = new ConcurrentHashMap<>();
    private final Map<MethodKey, Object> cache = new ConcurrentHashMap<>(); // Кэш

    public CacheProxy(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (!method.isAnnotationPresent(Cache.class)) {
            return method.invoke(target, args); // Метод без кэширования
        }

        Cache annotation = method.getAnnotation(Cache.class);
        MethodKey key = new MethodKey(method, args, annotation.identityBy()); // Уникальный ключ для кэша

        Lock lock = getOrCreateLock(key);
        try {
            lock.lock();

            Object result = cache.get(key);
            if (result == null) {
                result = method.invoke(target, args); // Выполнение метода

                // Применение настроек кэширования
                if (result instanceof List && annotation.maxListSize() < ((List<?>) result).size()) {
                    result = ((List<?>) result).stream()
                            .limit(annotation.maxListSize())
                            .collect(Collectors.toList());
                }

                cache.put(key, result); // Сохранение в кэш
            }
            return result;
        } finally {
            lock.unlock();
        }
    }

    private Lock getOrCreateLock(MethodKey key) {
        return locks.computeIfAbsent(key, k -> new ReentrantLock());
    }

    static class MethodKey {
        private final Method method;
        private final Object[] arguments;
        private final Class<?>[] identityClasses;

        public MethodKey(Method method, Object[] arguments, Class<?>[] identityClasses) {
            this.method = method;
            this.arguments = arguments;
            this.identityClasses = identityClasses;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof MethodKey)) return false;
            MethodKey that = (MethodKey) o;
            return Objects.equals(method, that.method) &&
                    Arrays.equals(arguments, that.arguments) &&
                    Arrays.deepEquals(identityClasses, that.identityClasses);
        }

        @Override
        public int hashCode() {
            int result = Objects.hash(method, identityClasses);
            result = 31 * result + Arrays.hashCode(arguments);
            return result;
        }
    }
}