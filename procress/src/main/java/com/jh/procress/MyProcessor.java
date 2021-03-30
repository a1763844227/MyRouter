package com.jh.procress;


import com.google.auto.service.AutoService;
import com.jh.annotation.BindingView;
import com.squareup.javapoet.JavaFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
@SupportedAnnotationTypes({"com.jh.annotation.BindingView"})
public class MyProcessor extends AbstractProcessor {

    private Filer filer;
    private Messager messager;
    private Elements elementsUtil;
    private Types types;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> set=new LinkedHashSet<>();
        set.add(BindingView.class.getCanonicalName());
        return set;
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        filer=processingEnv.getFiler();
        elementsUtil=processingEnv.getElementUtils();
        messager=processingEnv.getMessager();
        types=processingEnv.getTypeUtils();
        Map<String,ClassProxyCreate> proxyCreateMap=new HashMap<>();


        Set<? extends Element> elements =roundEnvironment.getElementsAnnotatedWith(BindingView.class);
        for(Element element  :elements){
            VariableElement variableElement= (VariableElement) element;
            TypeElement classElement= (TypeElement) element.getEnclosingElement();
            String fullName=classElement.getQualifiedName().toString();
            ClassProxyCreate classProxyCreate=proxyCreateMap.get(fullName);
            if (classProxyCreate==null) {
                classProxyCreate=new ClassProxyCreate(elementsUtil,classElement);
                proxyCreateMap.put(fullName,classProxyCreate);
            }
            BindingView bindView = element.getAnnotation(BindingView.class);
            int id=bindView.value();
            classProxyCreate.putElement(id,variableElement);

        }

        for (String key :proxyCreateMap.keySet()) {
            ClassProxyCreate classProxyCreate=proxyCreateMap.get(key);
            JavaFile javaFile= JavaFile.builder(classProxyCreate.getFullName(),classProxyCreate.createClass()).build();
            try {
                javaFile.writeTo(processingEnv.getFiler());
            } catch (IOException e) {
                e.printStackTrace();
            }

            messager.printMessage(Diagnostic.Kind.NOTE,"11111111111111");

        }

        return true;
    }

}
