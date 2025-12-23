package com.softwaresecured.burp;

public final class VERSION {
    public static final int VERSION_MAJOR = 1;
    public static final int VERSION_MINOR = 0;
    public static final int VERSION_PATCH = 0;
    public static String getVersionStrPlain() {
        return String.format("%d.%d.%d", VERSION_MAJOR, VERSION_MINOR, VERSION_PATCH);
    }
}
