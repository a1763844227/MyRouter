package com.jh.procress;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;

import static javax.lang.model.element.Modifier.PUBLIC;

/**
 * create by jh on 2021/3/23.
 */
public class ClassProxyCreate {

    private String className;
    private String packageName;
    private TypeElement typeElement;
    private Map<Integer, VariableElement> variableElementMap=new HashMap<>();

    public ClassProxyCreate(Elements elements,TypeElement typeElement) {
        this.typeElement = typeElement;
        PackageElement packageElement=elements.getPackageOf(typeElement);
        packageName=packageElement.getQualifiedName().toString();
        className=typeElement.getSimpleName().toString();

    }

    public void putElement(int id,VariableElement variableElement){
        variableElementMap.put(id,variableElement);
    }

    public TypeSpec createClass(){
        TypeSpec buildClass=TypeSpec.classBuilder(className+"_binding")
                .addModifiers(PUBLIC)
                .addMethod(createMethod())
                .build();
        return buildClass;


    }

    public MethodSpec createMethod(){
        ClassName className =ClassName.bestGuess(typeElement.getQualifiedName().toString());
        MethodSpec.Builder builder=MethodSpec.methodBuilder("bindView")
                .addModifiers(PUBLIC)
                .returns(void.class)
                .addParameter(className,"viewClass");
        for(int id  :variableElementMap.keySet()){
            VariableElement variableElement=variableElementMap.get(id);
            String name=variableElement.getSimpleName().toString();
            builder.addCode("viewClass."+name+"="+"((android.app.Activity)viewClass).findViewById( " + id + ");");

        }
        return builder.build();
    }

    public String getFullName(){
        return packageName;
    }
}
