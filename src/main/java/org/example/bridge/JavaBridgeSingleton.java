package org.example.bridge;

public class JavaBridgeSingleton {

    private static final JavaBridge INSTANCE = new JavaBridge();

    public static JavaBridge get() {
        return INSTANCE;
    }
}