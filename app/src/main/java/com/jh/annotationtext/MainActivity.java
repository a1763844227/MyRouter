package com.jh.annotationtext;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jh.annotation.BindingView;
import com.jh.annotation.Route;
import com.jh.router.MyRouter;
import com.jh.router.ViewInject;


@ContentView(R.layout.activity_main)
@Route(path = "/app/text")
public class MainActivity extends AppCompatActivity {
//    @CreateView(R.id.text)
//    TextView textView;
//    @CreateView(R.id.text2)
//    TextView textView2;

    @BindingView(R.id.text)
    TextView textView5;
    @BindingView(R.id.text2)
    TextView textView3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewBind.init(this);
        ViewInject.inject(this);
        textView5.setText("111");
        textView3.setText("2222");


        textView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPer();
                MyRouter.getInstance().init(getApplication());


                MyRouter.getInstance().build("/home/text1").navigation(MainActivity.this);

            }
        });


    }
    @NeedPerMission(value = Manifest.permission.WRITE_EXTERNAL_STORAGE,code = 1)
    public void requestPer(){
        if (PerMissionUtil.requestPerMission(this)) {
            Log.i("tag","asdgf");
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode ==1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i("tag","asdgf22222");
            } else {
                PerMissionUtil.showDialog(this);
            }

        }
    }
    @ClickView({R.id.text,R.id.text2})
    public void clickView(View v){
        switch (v.getId()) {
            case R.id.text:
                Log.i("tag","点击了text1");
                Toast.makeText(this,"sdfas",Toast.LENGTH_SHORT).show();
                Toast.makeText(this,"sdfas2222",Toast.LENGTH_SHORT).show();


                break;
            case R.id.text2:
                Log.i("tag","点击了text2");

                break;

            default:
                break;
        }
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
