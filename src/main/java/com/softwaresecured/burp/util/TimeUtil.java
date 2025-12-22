package com.softwaresecured.burp.util;

public class TimeUtil {
    public static void delay( int ms ) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {
            ;
        }
    }
}
