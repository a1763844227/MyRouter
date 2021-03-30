package com.jh.other;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.jh.annotation.Route;
import com.jh.router.MyRouter;


@Route(path = "/other/text1")
public class OtherActivity extends AppCompatActivity {
//    @CreateView(R.id.text)
//    TextView textView;
//    @CreateView(R.id.text2)
//    TextView textView2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other);

        TextView textView=findViewById(R.id.tv01);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyRouter.getInstance().build("/other/text1").navigation(OtherActivity.this);
            }
        });

    }

//     for (Element ele : roundEnv.getElementsAnnotatedWith(InjectView.class)) {
//        // 打印element信息
//        processingEnv.getMessager().printMessage(Kind.NOTE, "ele = " + ele);
//        // 如果解析的是类的注解
//        if (ele.getKind() == ElementKind.CLASS) {
//            // 类的Element
//            TypeElement classElement = (TypeElement) ele;
//            // 包的Element，也就是类的封装Element
//            PackageElement packageElement = (PackageElement) ele.getEnclosingElement();
//            // 类的全名：com.example.util.MainActivity
//            fullClassName= classElement.getQualifiedName().toString();
//            // 类名：MainActivity
//            className = classElement.getSimpleName().toString();
//            // 包名：com.example.util
//            packageName = packageElement.getQualifiedName().toString();
//            // 获取类的注解值
//            int layoutId = classElement.getAnnotation(InjectView.class).value();
//            // 打印信息
//            processingEnv.getMessager().printMessage(Kind.NOTE, "annatated class : packageName = " + packageName + " , className = " + className + " , fqClassName = " + fqClassName);
//        } else if (ele.getKind() == ElementKind.FIELD) {
//            // 属性的Element
//            VariableElement varElement = (VariableElement) ele;
//            // 类的Element，就是属性的封装Element
//            TypeElement classElement = (TypeElement) ele.getEnclosingElement();
//            // 类的全名：com.example.util.MainActivity
//            fullClassName= classElement.getQualifiedName().toString();
//            // 包的Element
//            PackageElement packageElement = elementUtils.getPackageOf(classElement);
//            // 包名：com.example.util
//            packageName = packageElement.getQualifiedName().toString();
//            // 类名 ：MainActivity
//            className = classElement.getSimpleName().toString();
//            // 获取属性的注解
//            int id = varElement.getAnnotation(InjectView.class).value();
//            String fieldName = varElement.getSimpleName().toString();
//            String fieldType = varElement.asType().toString();
//            // 打印信息
//            processingEnv.getMessager().printMessage(Kind.NOTE, "annatated field : fieldName = " + varElement.getSimpleName().toString() + " , id = " + id + " , fileType = " + fieldType);
//        }
//        /**
//         * 下面可以用JavaFileObject jfo = processingEnv.getFiler().createSourceFile方法生成JavaFileObject对象，然后Writer writer = jfo.openWriter()方法得到一个writer对象，最后java代码文字，动态生成的java文件
//         **/
//        return true;
//    }
//}
}
