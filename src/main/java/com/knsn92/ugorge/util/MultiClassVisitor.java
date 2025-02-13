package com.knsn92.ugorge.util;

import org.objectweb.asm.*;

public class MultiClassVisitor extends ClassVisitor{

    private final MultiClassVisitorContext ctx;
    private ClassVisitor innerVisitor = null;

    public MultiClassVisitor(int api, ClassVisitor cv, MultiClassVisitorContext ctx) {
        super(api, cv);
        this.ctx = ctx;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.innerVisitor = ctx.create(name, this.cv, this.api);
        this.innerVisitor.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public void visitSource(String source, String debug) {
        this.innerVisitor.visitSource(source, debug);
    }

    @Override
    public void visitOuterClass(String owner, String name, String desc) {
        this.innerVisitor.visitOuterClass(owner, name, desc);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        return this.innerVisitor.visitAnnotation(desc, visible);
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
        return this.innerVisitor.visitTypeAnnotation(typeRef, typePath, desc, visible);
    }

    @Override
    public void visitAttribute(Attribute attr) {
        this.innerVisitor.visitAttribute(attr);
    }

    @Override
    public void visitInnerClass(String name, String outerName, String innerName, int access) {
        this.innerVisitor.visitInnerClass(name, outerName, innerName, access);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        return this.innerVisitor.visitField(access, name, desc, signature, value);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        return this.innerVisitor.visitMethod(access, name, desc, signature, exceptions);
    }

    @Override
    public void visitEnd() {
        this.innerVisitor.visitEnd();
    }
}
