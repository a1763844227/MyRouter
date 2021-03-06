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
                Log.i("tag","?????????text1");
                Toast.makeText(this,"sdfas",Toast.LENGTH_SHORT).show();
                Toast.makeText(this,"sdfas2222",Toast.LENGTH_SHORT).show();


                break;
            case R.id.text2:
                Log.i("tag","?????????text2");

                break;

            default:
                break;
        }
    }

//     for (Element ele : roundEnv.getElementsAnnotatedWith(InjectView.class)) {
//        // ??????element??????
//        processingEnv.getMessager().printMessage(Kind.NOTE, "ele = " + ele);
//        // ??????????????????????????????
//        if (ele.getKind() == ElementKind.CLASS) {
//            // ??????Element
//            TypeElement classElement = (TypeElement) ele;
//            // ??????Element????????????????????????Element
//            PackageElement packageElement = (PackageElement) ele.getEnclosingElement();
//            // ???????????????com.example.util.MainActivity
//            fullClassName= classElement.getQualifiedName().toString();
//            // ?????????MainActivity
//            className = classElement.getSimpleName().toString();
//            // ?????????com.example.util
//            packageName = packageElement.getQualifiedName().toString();
//            // ?????????????????????
//            int layoutId = classElement.getAnnotation(InjectView.class).value();
//            // ????????????
//            processingEnv.getMessager().printMessage(Kind.NOTE, "annatated class : packageName = " + packageName + " , className = " + className + " , fqClassName = " + fqClassName);
//        } else if (ele.getKind() == ElementKind.FIELD) {
//            // ?????????Element
//            VariableElement varElement = (VariableElement) ele;
//            // ??????Element????????????????????????Element
//            TypeElement classElement = (TypeElement) ele.getEnclosingElement();
//            // ???????????????com.example.util.MainActivity
//            fullClassName= classElement.getQualifiedName().toString();
//            // ??????Element
//            PackageElement packageElement = elementUtils.getPackageOf(classElement);
//            // ?????????com.example.util
//            packageName = packageElement.getQualifiedName().toString();
//            // ?????? ???MainActivity
//            className = classElement.getSimpleName().toString();
//            // ?????????????????????
//            int id = varElement.getAnnotation(InjectView.class).value();
//            String fieldName = varElement.getSimpleName().toString();
//            String fieldType = varElement.asType().toString();
//            // ????????????
//            processingEnv.getMessager().printMessage(Kind.NOTE, "annatated field : fieldName = " + varElement.getSimpleName().toString() + " , id = " + id + " , fileType = " + fieldType);
//        }
//        /**
//         * ???????????????JavaFileObject jfo = processingEnv.getFiler().createSourceFile????????????JavaFileObject???????????????Writer writer = jfo.openWriter()??????????????????writer???????????????java??????????????????????????????java??????
//         **/
//        return true;
//    }
//}
}
