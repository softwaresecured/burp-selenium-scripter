package com.softwaresecured.burp.util;



import java.io.IOException;
import java.io.InputStream;

public final class ResourceLoader {

    private static ResourceLoader INSTANCE;
    public ResourceLoader() {
    }

    public static ResourceLoader getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new ResourceLoader();
        }

        return INSTANCE;
    }

    public static String loadContent(String resourceName) {
        try {
            InputStream in = ResourceLoader.class.getClassLoader().getResourceAsStream((resourceName));
            return new String(in.readAllBytes());
        } catch (IOException e) {
            Logger.log("ERROR", String.format("Exception loading resource [%s]: %s", resourceName, e.getMessage()));
        }
        return null;
    }
}
