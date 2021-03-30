//
//        ┌─┐       ┌─┐
//        ┌──┘ ┴───────┘ ┴──┐
//        │                 │
//        │       ───       │
//        │  ─┬┘       └┬─  │
//        │                 │
//        │       ─┴─       │
//        │                 │
//        └───┐         ┌───┘
//            │         │
//            │         │
//            │         │
//            │         └──────────────┐
//            │                        │
//            │                        ├─┐
//            │                        ┌─┘
//            │                        │
//            └─┐  ┐  ┌───────┬──┐  ┌──┘
//              │ ─┤ ─┤       │ ─┤ ─┤
//              └──┴──┘       └──┴──┘
//
//            神兽保佑       永无bug
package com.jh.annotationtext;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.TypeVariable;

public class PerMissionUtil {


    static boolean requestPerMission(Activity activity){
        if (Build.VERSION.SDK_INT>=23) {
            Class<? extends Activity> aClass=activity.getClass();
            Method[] methods=aClass.getDeclaredMethods();
            for(Method method :methods){
                if (method.isAnnotationPresent(NeedPerMission.class)) {
                    NeedPerMission needPerMission=method.getAnnotation(NeedPerMission.class);
                    String permission=needPerMission.value();
                    int code=needPerMission.code();
//                for (Annotation[] annotations:method.getParameterAnnotations()){
//                    for (Annotation annotation:annotations){
//                        if (annotation instanceof BindView) {
//
//                        }
//                    }
//                }
                    if (ActivityCompat.checkSelfPermission(activity,permission)!=PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(activity,permission)) {
                            showDialog(activity);
                        }else{
                            ActivityCompat.requestPermissions(activity,new String[]{permission},code);
                        }
                        return false;
                    }
                    return true;

                }
            }
        }

        return true;

    }

    public static void showDialog( final Activity activity){
        AlertDialog alertDialog=new AlertDialog.Builder(activity)
                .setTitle("提示")
                .setMessage("检测到您已拒绝过权限申请，" +
                        "是否跳转到应用详情界面进行权限开通？")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(activity,"你拒绝了权限申请",Toast.LENGTH_SHORT).show();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.fromParts("package", activity.getPackageName(), null));
                        activity.startActivity(intent);

                    }
                })
                .create();
        alertDialog.show();
    }
}
