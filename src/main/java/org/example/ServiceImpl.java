package org.example;


import java.util.Collections;
import java.util.List;

public class ServiceImpl implements Service {
    @Override
    public List<String> work(String item) {
        System.out.println("Вычисление результата для " + item);
        return Collections.singletonList(item);
    }
}