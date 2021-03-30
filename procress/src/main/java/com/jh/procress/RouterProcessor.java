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
import javax.tools.Diagnostic;

import static javax.lang.model.element.Modifier.PUBLIC;

/**
 * create by jh on 2021/3/25.
 */
@AutoService(Processor.class)
public class RouterProcessor extends AbstractProcessor {

    private Filer filer;
    private Map<String,ClassName> routes=new HashMap<>();
    private String moduleName;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        filer=processingEnvironment.getFiler();
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,"22212sdasd");

        Map<String, String> options = processingEnv.getOptions();
        if (null!=options && !options.isEmpty()) {
            moduleName = options.get(Routes.ROUTES_MODULE_NAME);
        }

    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> set=new LinkedHashSet<>();
        set.add(Route.class.getCanonicalName());
        return set;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (!set.isEmpty()) {

            Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(Route.class);
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

            ParameterizedTypeName parameterizedTypeName = ParameterizedTypeName.get(ClassName.get(Map.class)
                    , ClassName.get(String.class), ParameterizedTypeName.get(
                            ClassName.get(Class.class),
                            WildcardTypeName.subtypeOf(ClassName.get(Object.class))));

            ParameterSpec parameterSpec = ParameterSpec.builder(parameterizedTypeName, "routes").build();

            MethodSpec.Builder methodBuild = MethodSpec.methodBuilder("loadInto")
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class)
                    .returns(void.class)
                    .addParameter(parameterSpec);

            if (routes != null && !routes.isEmpty()) {
                for (String key : routes.keySet()) {
                    methodBuild.addStatement("routes.put($S,$T.class)", key, routes.get(key));
                }
            }

            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "11111AAAAAA");

            TypeSpec typeSpec = TypeSpec.classBuilder(Routes.ROUTES_GROUP_CLASS_NAME + moduleName)
                    .addSuperinterface(ClassName.bestGuess(Routes.ROUTE_GROUP_PARENTS_NAME))
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
        Route route=element.getAnnotation(Route.class);
        String path=route.path();
//        String name=typeElement.getQualifiedName().toString();
        ClassName className=ClassName.get(typeElement);
//        groupName=path.substring(1,path.lastIndexOf("/"));
        routes.put(path,className);


    }
}
