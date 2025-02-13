package com.knsn92.ugorge.util;

import org.objectweb.asm.ClassVisitor;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class MultiClassVisitorContext {

    private final Map<String, BiFunction<Integer, ClassVisitor, ClassVisitor>> visitorCreators = new HashMap<>();
    private BiFunction<Integer, ClassVisitor, ClassVisitor> defaultVisitorCreator = null;

    public void put(String className, BiFunction<Integer, ClassVisitor, ClassVisitor> visitorCreator) {
        this.visitorCreators.put(className, visitorCreator);
    }

    public void setDefault(BiFunction<Integer, ClassVisitor, ClassVisitor> defaultVisitorCreator) {
        this.defaultVisitorCreator = defaultVisitorCreator;
    }

    public void setDefaultIgnore() {
        this.setDefault((api, cv) -> new ClassVisitor(api, cv){});
    }

    public ClassVisitor create(String name, ClassVisitor parent, int api) {
        BiFunction<Integer, ClassVisitor, ClassVisitor> visitorCreator = this.visitorCreators.get(name);
        if (visitorCreator == null) {
            visitorCreator = this.defaultVisitorCreator;
            if (visitorCreator == null)
                throw new RuntimeException("Don't registered class name " + name + " and default visitor don't registered");
        }
        return visitorCreator.apply(api, parent);
    }

}
