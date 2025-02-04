package org.example;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Cache {
    /**
     * Тип хранения кэша: IN_MEMORY или FILE.
     */
    CacheType cacheType() default CacheType.IN_MEMORY;

    /**
     * Префикс имени файла для сохранения кэша на диске.
     */
    String fileNamePrefix() default "";

    /**
     * Использовать ли сжатие ZIP для файлового кэша.
     */
    boolean zip() default false;

    /**
     * Максимальное количество элементов в списке, если метод возвращает список.
     */
    int maxListSize() default Integer.MAX_VALUE;

    /**
     * Какие параметры учитывать для определения уникального ключа.
     */
    Class<?>[] identityBy() default {};
}