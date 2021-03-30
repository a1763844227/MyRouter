package com.jh.procress;

import com.google.auto.service.AutoService;
import com.jh.annotation.Route;
import com.jh.annotation.RouteProvider;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

import static javax.lang.model.element.Modifier.PUBLIC;

/**
 * create by jh on 2021/3/25.
 */
@AutoService(Processor.class)
public class ProviderProcessor extends AbstractProcessor {

    private Filer filer;
    Elements elementUtils;
    private Map<String,ClassName> routes=new HashMap<>();
    private String moduleName;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        filer=processingEnvironment.getFiler();
        elementUtils=processingEnvironment.getElementUtils();
        Map<String, String> options = processingEnvironment.getOptions();
        processingEnvironment.getMessager().printMessage(Diagnostic.Kind.NOTE,"provider init");
        if (null!=options && !options.isEmpty()) {
            moduleName = options.get(Routes.ROUTES_MODULE_NAME);
        }
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> set=new LinkedHashSet<>();
        set.add(RouteProvider.class.getCanonicalName());
        return set;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (!set.isEmpty()) {

            Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(RouteProvider.class);
            routes.clear();
            for (Element element : elements) {
                addRouter(element);
            }

//        public final class AARouterMap_app implements IRoute {
//            @Override
//            public void loadInto(Map<String, String> routes) {
//                routes.put("/app/main","io.github.iamyours.aarouter.MainActivity");
//            }
//        }
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "parameterizedTypeName");

            ParameterizedTypeName parameterizedTypeName = ParameterizedTypeName.get(ClassName.get(Map.class)
                    , ClassName.get(String.class), ParameterizedTypeName.get(
                            ClassName.get(Class.class),
                            WildcardTypeName.subtypeOf(ClassName.get(elementUtils.getTypeElement(Routes.PROVIDER_GROUP_DETAIL_NAME)))));

            ParameterSpec parameterSpec = ParameterSpec.builder(parameterizedTypeName, "providers").build();

            MethodSpec.Builder methodBuild = MethodSpec.methodBuilder("loadInto")
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class)
                    .returns(void.class)
                    .addParameter(parameterSpec);

            if (routes != null && !routes.isEmpty()) {
                for (String key : routes.keySet()) {
                    methodBuild.addStatement("providers.put($S,$T.class)", key, routes.get(key));
                }
            }


            TypeSpec typeSpec = TypeSpec.classBuilder(Routes.PROVIDER_GROUP_PROVIDER_NAME + moduleName)
                    .addSuperinterface(ClassName.bestGuess(Routes.PROVIDER_GROUP_PARENTS_NAME))
                    .addMethod(methodBuild.build())
                    .addModifiers(PUBLIC)
                    .build();

            JavaFile javaFile = JavaFile.builder(Routes.PACKAGE_NAME_ROUTES, typeSpec).build();
            try {
                javaFile.writeTo(filer);
            } catch (IOException e) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "11111" + e.toString());

                e.printStackTrace();
            }

            return true;

        }
        return false;
    }

    private void addRouter(Element element){
        TypeElement typeElement= (TypeElement) element;
        RouteProvider route=element.getAnnotation(RouteProvider.class);
        String path=route.path();
//        String name=typeElement.getQualifiedName().toString();
        ClassName className=ClassName.get(typeElement);
//        groupName=path.substring(1,path.lastIndexOf("/"));
        routes.put(path,className);


    }
}
