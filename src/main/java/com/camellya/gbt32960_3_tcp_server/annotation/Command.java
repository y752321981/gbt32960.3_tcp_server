package com.camellya.gbt32960_3_tcp_server.annotation;

import com.camellya.gbt32960_3_tcp_server.enums.CommandEnum;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Command {
    CommandEnum value();
}
