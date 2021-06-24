package com.jh.plugin.router

import com.android.build.api.transform.Context
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
import org.gradle.api.tasks.Input
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes

import java.util.jar.JarEntry
import java.util.jar.JarFile

public class RouterTransform extends Transform {

    Project project

    static List<String> listRouterPath=new ArrayList<>()

    static File file_contains_init_jars

    RouterTransform(Project project) {
        this.project = project
    }
//  该Transform的名称，自定义即可，只是一个标识
    @Override
    String getName() {
        return RouterUtils.PLUGIN_NAME
    }


    //该Transform支持扫描的文件类型，分为class文件和资源文件，我们这里只处理class文件的扫描
    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    //扫描范围，这里扫描整个工程，包括当前module以及其他jar包、aar文件等所有的class
    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
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
        print(RouterUtils.PLUGIN_NAME+"start -------------")
        def startTime=System.currentTimeMillis()
        Collection<TransformInput> inputs=transformInvocation.getInputs()
        TransformOutputProvider outputProvider=transformInvocation.getOutputProvider()
        inputs.each { TransformInput transformInput ->
            transformInput.directoryInputs.each { DirectoryInput directoryInput ->
                handleDirectory(directoryInput,outputProvider)

            }

            transformInput.jarInputs.each { JarInput jarInput ->
                handleJar(jarInput,outputProvider)
            }
        }

        println("RouterTransform scan end cost"+System.currentTimeMillis()-startTime+"ms")
        listRouterPath.each {path->
            println("file name -----"+path)
        }
        println("包含MyRouter的jar包")
        println(file_contains_init_jars.getAbsolutePath())

        RouterUtils.handleJarInputs()

    }

    static void handleDirectory(DirectoryInput directoryInput,TransformOutputProvider outputProvider){
        File dest=outputProvider.getContentLocation(directoryInput.name,directoryInput.getContentTypes(),directoryInput.scopes, Format.DIRECTORY)
        String root = directoryInput.file.absolutePath
        print("rootpath"+root)

        directoryInput.file.eachFileRecurse {File file ->

            def path = file.absolutePath.replace(root, '')
            if (File.separator != '/'){
                path = path.replaceAll("\\\\", "/")
            }
            print("path replace==="+path)
            print("path replace+++"+file.name)
            print("path isFile+++"+file.isFile())
            print("path isRouterClass+++"+RouterUtils.isRouterClass(path))

            if (file.isFile() && RouterUtils.isRouterClass(path)){
//                if (!listRouterPath.contains(file.name))
//                    listRouterPath.add(file.name)
                scanClass(file)
            }

        }

        FileUtils.copyDirectory(directoryInput.file,dest)

    }

    static void handleJar(JarInput jarInput,TransformOutputProvider outputProvider){
        String destName = jarInput.name
        // rename jar files
        def hexName = DigestUtils.md5Hex(jarInput.file.absolutePath)
        if (destName.endsWith(".jar")) {
            destName = destName.substring(0, destName.length() - 4)
        }
        // input file
        File jarFile = jarInput.file
        // output file
        File dest = outputProvider.getContentLocation(destName + "_" + hexName, jarInput.contentTypes, jarInput.scopes, Format.JAR)

        //scan jar file to find classes

        if (jarInput.file.getAbsolutePath().endsWith(".jar")) {

            if (RouterUtils.shouldProcessPreDexJar(jarFile.absolutePath)){
                def file = new JarFile(jarFile)
                Enumeration enumeration = file.entries()
                while (enumeration.hasMoreElements()) {
                    //遍历这个jar包里的所有class文件项
                    JarEntry jarEntry = (JarEntry) enumeration.nextElement()
                    //class文件的名称，这里是全路径类名，包名之间以"/"分隔
                    String entryName = jarEntry.getName()
                    if (entryName.startsWith(RouterUtils.ROUTER_PACKAGE)) {

                        print("entryName===="+entryName)
//                        String name=entryName.substring(entryName.lastIndexOf("/")+1)
//                        if (!listRouterPath.contains(name)){
//                            listRouterPath.add(name)
//                        }
                        InputStream inputStream = file.getInputStream(jarEntry)
                        scanClass(inputStream)
                    } else if (RouterUtils.PLUGIN_ACTION_CLASS == entryName) {
                        // mark this jar file contains LogisticsCenter.class
                        // After the scan is complete, we will generate register code into this file
                        file_contains_init_jars = dest
                    }
                }
                file.close()
            }
        }

        FileUtils.copyFile(jarFile, dest)
    }

    static void scanClass(File file){
        scanClass(new FileInputStream(file))
    }

    static void scanClass(InputStream inputStream){
        ClassReader classReader = new ClassReader(inputStream)
        ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
        ClassVisitor cv = new ScanClassVisit(classWriter)
        classReader.accept(cv,ClassReader.EXPAND_FRAMES)
        inputStream.close()
    }


    private static class ScanClassVisit extends ClassVisitor{

        ScanClassVisit(ClassVisitor cv) {
            super(Opcodes.ASM6, cv)
        }

        @Override
        void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            super.visit(version, access, name, signature, superName, interfaces)
            println("name is"+name)
            if (!listRouterPath.contains(name)){
                listRouterPath.add(name)
            }
        }
    }
}