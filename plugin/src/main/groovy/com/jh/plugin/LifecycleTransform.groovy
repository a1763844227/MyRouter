package com.jh.plugin

import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.Format
import com.android.build.api.transform.JarInput
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInput
import com.android.build.api.transform.TransformInvocation
import com.android.build.api.transform.TransformOutputProvider
import com.android.build.gradle.internal.pipeline.TransformManager
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

import static org.objectweb.asm.ClassReader.EXPAND_FRAMES
import static org.objectweb.asm.Opcodes.LSTORE

public class LifecycleTransform extends Transform {

    Project project

    LifecycleTransform(Project project) {
        this.project = project
    }
//  该Transform的名称，自定义即可，只是一个标识
    @Override
    String getName() {
        return "LifecycleTransform"
    }


    //该Transform支持扫描的文件类型，分为class文件和资源文件，我们这里只处理class文件的扫描
    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    //扫描范围，这里扫描整个工程，包括当前module以及其他jar包、aar文件等所有的class
    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
//        org.objectweb.asm
//        ClassReader
//        DigestUtils
//        FileUtils
        return TransformManager.SCOPE_FULL_PROJECT
    }

    //是否增量扫描
    @Override
    boolean isIncremental() {
        return true
    }


    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation)
        print('=====LifecycleTransform start')
        def startTime=System.currentTimeMillis()
        Collection<TransformInput> inputs=transformInvocation.getInputs()
        TransformOutputProvider outputProvider=transformInvocation.getOutputProvider()
        if (outputProvider!=null) outputProvider.deleteAll()
        //遍历inputs
        inputs.each {TransformInput input ->
            //遍历directoryInputs
            input.directoryInputs.each { DirectoryInput directoryInput ->
                handleDirectory(directoryInput,outputProvider)
            }

            //遍历jarInputs
            input.jarInputs.each { JarInput jarInputs ->
                handleJarInputs(jarInputs,outputProvider)
            }

        }

        def cost=System.currentTimeMillis()-startTime

        print('LifecycleTransform end')

        print('LifecycleTransform haoshi $cost s'+cost)


    }

    static void handleDirectory(DirectoryInput directoryInput,TransformOutputProvider outputProvider){
        print('=====handleDirectory start')
        if (directoryInput.file.isDirectory()){
            directoryInput.file.eachFileRecurse {File file ->

                def name=file.name
                print('=====eachFileRecurse start'+file.absolutePath)
                print('=====contains Activity'+name.contains("Activity.class"))

                if (checkClassFile(name)){
                    print('----------- deal with "class" file <' + name + '> -----------')
                    ClassReader classReader=new ClassReader(file.bytes)
                    ClassWriter classWriter=new ClassWriter(classReader,ClassWriter.COMPUTE_MAXS)
                    ClassVisitor cv=new LifecycleClassVisitor(classWriter)
                    classReader.accept(cv,ClassReader.EXPAND_FRAMES)
                    byte[] code=classWriter.toByteArray()
                    FileOutputStream fileOutputStream=new FileOutputStream(file.parentFile.getAbsolutePath()+File.separator+name)
                    fileOutputStream.write(code)
                    fileOutputStream.close()
                }
            }
            //Transform扫描的class文件是输入文件(input)，有输入必然会有输出(output)，处理完成后需要将输入文件拷贝到一个输出目录下去，
            //后面打包将class文件转换成dex文件时，直接采用的就是输出目录下的class文件了。
            //必须这样获取输出路径的目录名称
            def dest= outputProvider.getContentLocation(directoryInput.name,directoryInput.getContentTypes(),directoryInput.scopes,
                    Format.DIRECTORY)
            FileUtils.copyDirectory(directoryInput.file,dest)

        }
    }

    /**
     * 处理Jar中的class文件
     */
    static void handleJarInputs(JarInput jarInput, TransformOutputProvider outputProvider) {
        if (jarInput.file.getAbsolutePath().endsWith(".jar")) {
            //重名名输出文件,因为可能同名,会覆盖
            def jarName = jarInput.name
            def md5Name = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())
            if (jarName.endsWith(".jar")) {
                jarName = jarName.substring(0, jarName.length() - 4)
            }
            JarFile jarFile = new JarFile(jarInput.file)
            Enumeration enumeration = jarFile.entries()
            File tmpFile = new File(jarInput.file.getParent() + File.separator + "classes_temp.jar")
            //避免上次的缓存被重复插入
            if (tmpFile.exists()) {
                tmpFile.delete()
            }
            JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(tmpFile))
            //用于保存
            while (enumeration.hasMoreElements()) {
                JarEntry jarEntry = (JarEntry) enumeration.nextElement()
                String entryName = jarEntry.getName()
                ZipEntry zipEntry = new ZipEntry(entryName)
                InputStream inputStream = jarFile.getInputStream(jarEntry)
                //插桩class
                if (checkClassFile(entryName)) {
                    //class文件处理
                    println '----------- deal with "jar" class file <' + entryName + '> -----------'
                    jarOutputStream.putNextEntry(zipEntry)
                    ClassReader classReader = new ClassReader(IOUtils.toByteArray(inputStream))
                    ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
                    ClassVisitor cv = new LifecycleClassVisitor(classWriter)
                    classReader.accept(cv, EXPAND_FRAMES)
                    byte[] code = classWriter.toByteArray()
                    jarOutputStream.write(code)
                } else {
                    jarOutputStream.putNextEntry(zipEntry)
                    jarOutputStream.write(IOUtils.toByteArray(inputStream))
                }
                jarOutputStream.closeEntry()
            }
            //结束
            jarOutputStream.close()
            jarFile.close()
            def dest = outputProvider.getContentLocation(jarName + md5Name,
                    jarInput.contentTypes, jarInput.scopes, Format.JAR)
            FileUtils.copyFile(tmpFile, dest)
            tmpFile.delete()
        }
    }

    static boolean checkClassFile(String name) {
        //只处理需要的class文件
        return (name.endsWith(".class") && !name.startsWith("R\$")
                && !"R.class".equals(name) && !"BuildConfig.class".equals(name)
                && "android/support/v4/app/FragmentActivity.class".equals(name))
    }


    private static class LifecycleClassVisitor extends ClassVisitor{

        String className;

        LifecycleClassVisitor(ClassVisitor cv) {
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
            if ("android/support/v4/app/FragmentActivity" .equals(className) ){

                if ("onCreate".equals(name)){
                    print("LifecycleClassVisitor visit method "+name)
                    methodVisitor=new CreateMethodVisit(methodVisitor)
                }else if ("onDestroy".equals(name)){
                    print("LifecycleClassVisitor visit method "+name)
                    methodVisitor=new DestroyMethodVisit(methodVisitor)
                }
            }
            return methodVisitor
        }
    }

    private static class CreateMethodVisit extends MethodVisitor{

        CreateMethodVisit(MethodVisitor mv) {
            super(Opcodes.ASM6, mv)
        }

        @Override
        void visitCode() {
            super.visitCode()
            mv.visitMethodInsn(Opcodes.INVOKESTATIC,"com/example/asm/text/TimeCalculate","start","()V",false)
            mv.visitMethodInsn(Opcodes.INVOKESTATIC,"java/lang/System","currentTimeMillis","()J",false)
            mv.visitVarInsn(LSTORE,1);
            mv.visitFieldInsn(Opcodes.GETSTATIC,"java/lang/System","out","Ljava/io/PrintStream;")
            mv.visitLdcInsn("AsmText")
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,"java/io/PrintStream","println","(Ljava/lang/String;)V",false)
            mv.visitMethodInsn(Opcodes.INVOKESTATIC,"java/lang/System","currentTimeMillis","()J",false)
            mv.visitVarInsn(LSTORE,3);
            mv.visitFieldInsn(Opcodes.GETSTATIC,"java/lang/System","out","Ljava/io/PrintStream;")
            mv.visitTypeInsn(Opcodes.NEW,"java/lang/StringBuilder")
            mv.visitInsn(Opcodes.DUP)
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL,"java/lang/StringBuilder","<init>","()V",false)
            mv.visitLdcInsn("time")
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            mv.visitVarInsn(Opcodes.LLOAD,3)
            mv.visitVarInsn(Opcodes.LLOAD,1)
            mv.visitInsn(Opcodes.LSUB)
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(J)Ljava/lang/StringBuilder;", false);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC,"com/example/asm/text/TimeCalculate","end","()V",false)


        }
    }


    private static class DestroyMethodVisit extends MethodVisitor{

        DestroyMethodVisit(MethodVisitor mv) {
            super(Opcodes.ASM6, mv)
        }



        @Override
        void visitCode() {
            super.visitCode()

            mv.visitLdcInsn("Tag")
            mv.visitTypeInsn(Opcodes.NEW,"java/lang/StringBuilder")
            mv.visitInsn(Opcodes.DUP)
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL,"java/lang/StringBuilder","<init>","()V",false);
            mv.visitLdcInsn("----- onDestroy: ")
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,"java/lang/StringBuilder","append","(Ljava/lang/String;)Ljava/lang/StringBuilder;",false)
            mv.visitVarInsn(Opcodes.ALOAD,0)
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,"java/lang/Object","getClass","()Ljava/lang/Class;",false)
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,"java/lang/Class","getSimpleName","()Ljava/lang/String;",false)
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,"java/lang/StringBuilder","append","(Ljava/lang/String;)Ljava/lang/StringBuilder;",false)
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,"java/lang/StringBuilder","toString","()Ljava/lang/String;",false)
            mv.visitMethodInsn(Opcodes.INVOKESTATIC,"android/util/Log","i","(Ljava/lang/String;Ljava/lang/String;)I",false)
            mv.visitInsn(Opcodes.POP);



        }
    }
}