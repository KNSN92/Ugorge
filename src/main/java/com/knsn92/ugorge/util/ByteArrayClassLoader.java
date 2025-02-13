package com.knsn92.ugorge.util;

import java.net.URL;
import java.security.SecureClassLoader;
import java.util.HashMap;
import java.util.Map;

public class ByteArrayClassLoader extends SecureClassLoader {

    private final Map<String, byte[]> classes = new HashMap<>();
    private final Map<String, URL> resources = new HashMap<>();

    public ByteArrayClassLoader(ClassLoader parent) {
        super(parent);
    }

    public void putClass(String name, byte[] bytes) {
        classes.put(name, bytes);
    }

    public void putResource(String name, URL url) {
        resources.put(name, url);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] bytes = classes.get(name);
        if (bytes == null) {
            throw new ClassNotFoundException("Class " + name + " not found");
        }
        return defineClass(name, bytes, 0, bytes.length);
    }

    @Override
    public URL findResource(String name) {
        return resources.get(name);
    }
}
