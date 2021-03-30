package com.jh.procress;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;

import javax.lang.model.element.Modifier;

/**
 * create by jh on 2021/3/24.
 */
public class JavaPoetText {

//    public final class HelloWorld {
//        public static void main(String[] args) {
//            System.out.println("Hello, JavaPoet!");
//        }
//    }

    public void text01(){
        MethodSpec methodSpec= MethodSpec.methodBuilder("text")
                .addModifiers(Modifier.PUBLIC,Modifier.STATIC)
                .returns(void.class)
                .addParameter(String[].class,"args")
                .addStatement("$T.out.println($S)",System.class,"11111").build();

        TypeSpec typeSpec=TypeSpec.classBuilder("Text")
                .addMethod(methodSpec)
                .addModifiers(Modifier.PUBLIC,Modifier.FINAL)
                .build();

        JavaFile javaFile=JavaFile.builder("com.jh.procress",typeSpec)
                .build();
        try {
            javaFile.writeTo(System.out);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}



