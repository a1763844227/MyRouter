package com.jh.plugin.router

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type

public  class RouterClassVisitor extends ClassVisitor{

    String className;

    RouterClassVisitor(ClassVisitor cv) {
        super(Opcodes.ASM6, cv)
    }

    @Override
    void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        className=name
        super.visit(version, access, name, signature, superName, interfaces)
    }

    @Override
    MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor methodVisitor=super.visitMethod(access, name, desc, signature, exceptions)
        print("calssname ========"+className)
        if (RouterUtils.PLUGIN_ACTION_CLASS_FILE_NAME.equals(className) ){
            if (RouterUtils.PLUGIN_ACTION_CLASS_METHOD.equals(name)){
                print("LifecycleClassVisitor visit method "+name)
                methodVisitor=new RouterMethodVisitor(methodVisitor)
            }
        }
        return methodVisitor
    }

    private static class RouterMethodVisitor extends MethodVisitor{

        RouterMethodVisitor(MethodVisitor mv) {
            super(Opcodes.ASM6, mv)
        }


        @Override
        void visitInsn(int opcode) {
            if ((opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN)) {
//                String filePackage = RouterUtils.ROUTER_PACKAGE.replaceAll("/", ".")
                println("zhu ru start =====")
                RouterTransform.listRouterPath.each { fileName ->
                    fileName=fileName.replaceAll("/",".")
//                    fileName = filePackage + fileName
                    println("zhu ru start =====" + fileName)
//                mv.visitLdcInsn(Type.getType("L"+fileName+";"))
//                mv.visitLdcInsn(fileName)
//                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,RouterUtils.PLUGIN_ACTION_CLASS_FILE_NAME,
//                RouterUtils.PLUGIN_ACTION_CLASS_REGISTER,"(Ljava/lang/Class;)V",false)

//                    mv.visitLdcInsn(fileName)//类名
//                    // generate invoke register method into LogisticsCenter.loadRouterMap()
//                    mv.visitMethodInsn(Opcodes.INVOKESTATIC
//                            , RouterUtils.PLUGIN_ACTION_CLASS_FILE_NAME
//                            , RouterUtils.PLUGIN_ACTION_CLASS_REGISTER
//                            , "(Ljava/lang/String;)V"
//                            , false)
                    mv.visitLdcInsn(fileName)
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, "com/jh/router/MyRouter", "registerByPlugin", "(Ljava/lang/String;)V", false);
                }
            }

            super.visitInsn(opcode)


        }

        @Override
        void visitEnd() {
            super.visitEnd()
            println("zhuru end")
        }
    }
}
