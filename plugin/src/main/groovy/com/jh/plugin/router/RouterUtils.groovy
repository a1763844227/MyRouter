package com.jh.plugin.router

import com.android.build.api.transform.Format
import com.android.build.api.transform.JarInput
import com.android.build.api.transform.TransformOutputProvider
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

import static org.objectweb.asm.ClassReader.EXPAND_FRAMES


public class RouterUtils {

    public static final String PLUGIN_NAME="router_plugin";
    public static final String ROUTER_PACKAGE="com/jh/router/routes";
    public static final String PLUGIN_ACTION_CLASS="com/jh/router/MyRouter.class";
    public static final String PLUGIN_ACTION_CLASS_FILE_NAME="com/jh/router/MyRouter";

    public static final String ROUTER_$="\$"

    //要写入的方法名
    public static final String PLUGIN_ACTION_CLASS_METHOD="loadByPlugin";

    //要操作的方法 把其写入loadByPlugin
    public static final String PLUGIN_ACTION_CLASS_REGISTER="registerByPlugin";


    public static boolean isRouterClass(String path){
        return path!=null && path.contains(ROUTER_PACKAGE) && path.contains(ROUTER_$)
    }

    static boolean shouldProcessPreDexJar(String path) {
        return !path.contains("com.android.support") && !path.contains("/android/m2repository")
    }

    /**
     * 处理Jar中的class文件
     */
    static void handleJarInputs() {
        //重名名输出文件,因为可能同名,会覆盖
        File jarFile=RouterTransform.file_contains_init_jars
        //创建一个临时jar文件，要修改注入的字节码会先写入该文件里
        def optJar = new File(jarFile.getParent(), jarFile.name + ".opt")
        //避免上次的缓存被重复插入
        if (optJar.exists()){
            optJar.delete()
        }
        def file=new JarFile(jarFile)
        Enumeration enumeration = file.entries()

        JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(optJar))
        //用于保存
        while (enumeration.hasMoreElements()) {
            JarEntry jarEntry = (JarEntry) enumeration.nextElement()
            String entryName = jarEntry.getName()
            ZipEntry zipEntry = new ZipEntry(entryName)
            InputStream inputStream = file.getInputStream(jarEntry)
            jarOutputStream.putNextEntry(zipEntry)
            //插桩class
            println ("entryNameis"+entryName)
            println ("entryNameis"+PLUGIN_ACTION_CLASS.equals(entryName))

            if (PLUGIN_ACTION_CLASS.equals(entryName)) {
                //class文件处理
                println '----------- deal with "jar" class file <' + entryName + '> -----------'
                ClassReader classReader = new ClassReader(inputStream)
                // 构建一个ClassWriter对象，并设置让系统自动计算栈和本地变量大小
                ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
                ClassVisitor cv = new RouterClassVisitor(classWriter)
                classReader.accept(cv, EXPAND_FRAMES)
                byte[] code = classWriter.toByteArray()
                //将注入过字节码的class，写入临时jar文件里
                jarOutputStream.write(code)
            } else {
                //不需要修改的class，原样写入临时jar文件里
                jarOutputStream.write(IOUtils.toByteArray(inputStream))
            }
            inputStream.close()
            jarOutputStream.closeEntry()
        }
        //结束
        jarOutputStream.close()
        file.close()

        if (jarFile.exists())
            jarFile.delete()
        optJar.renameTo(jarFile)
    }


}