package org.example;

import java.lang.reflect.Proxy;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Service service = new ServiceImpl();
        Service cachedService = (Service) Proxy.newProxyInstance(
                Service.class.getClassLoader(),
                new Class[]{Service.class},
                new CacheProxy(service));

        // Первый вызов выполнит расчет
        long startTime = System.currentTimeMillis();
        List<String> result1 = cachedService.work("item");
        long endTime = System.currentTimeMillis();
        System.out.println("Первый вызов занял: " + (endTime - startTime) + " ms");
        System.out.println(result1);

        // Второй вызов возьмет данные из кэша
        startTime = System.currentTimeMillis();
        List<String> result2 = cachedService.work("item");
        endTime = System.currentTimeMillis();
        System.out.println("Второй вызов занял: " + (endTime - startTime) + " ms");
        System.out.println(result2);

        // Проверка, что результаты одинаковы
        if (result1.equals(result2)) {
            System.out.println("Результаты одинаковы");
        } else {
            System.out.println("Разные результаты");
        }
    }
}