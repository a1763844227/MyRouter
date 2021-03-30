package com.jh.annotationtext;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;

public class ViewBind {

    static void init(Activity activity){
        setView(activity);
//        setViewOnCreate(activity);
        createView(activity);
        clickView(activity);
    }

    /**
     * 设置布局
     */
    public static void setView(Activity activity){

        Class<? extends Activity> aClass=activity.getClass();
        if (aClass.isAnnotationPresent(ContentView.class)) {
            ContentView contentView=aClass.getAnnotation(ContentView.class);
            int layoutId= contentView != null ? contentView.value() : 0;
            if (layoutId!=0) {
                activity.setContentView(layoutId);

            }

        }
    }

    /**
     * 设置布局  在onCreate中设置布局
     */
    private static void setViewOnCreate(Activity activity){

        try {
            //getMethod只能获取public方法
            Method method=activity.getClass().getDeclaredMethod("onCreate", Bundle.class);//getDeclaredMethod可以获取到任何类型的方法，但不包括继承的方法
//            Annotation[] annotations = method.getAnnotations();
//
//            for (Annotation annotation:annotations) {
//                if (annotation instanceof ContentView) {
//                    ContentView contentView= (ContentView) annotation;
//                    int layoutId= contentView.value();
//                    Log.i("tag","sadfAAAQQQQ");
//
//                    if (layoutId!=0) {
//                        activity.setContentView(layoutId);
//
//                    }
//                }
//
//            }

            if (method.isAnnotationPresent(ContentView.class)) {
                ContentView contentView=method.getAnnotation(ContentView.class);
                int layoutId= contentView != null ? contentView.value() : 0;
                if (layoutId!=0) {
                    activity.setContentView(layoutId);

                }

            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

    }

    /**
     * 创建view
     */
    private static void createView(Activity activity){

        Class<? extends Activity> aClass=activity.getClass();
        Field[] fields=aClass.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(CreateView.class)) {
                CreateView createView = field.getAnnotation(CreateView.class);
                int id = createView.value();
                try {
                    Method method = aClass.getMethod("findViewById", int.class);
                    View view = (View) method.invoke(activity, id);
                    field.setAccessible(true);
                    field.set(activity, view);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    /**
     * view点击
     */
     private static void clickView(final Activity activity){
        Class<? extends Activity> aClass=activity.getClass();
        Method[] methods=aClass.getDeclaredMethods();
        for (Method method:methods) {
            if (method.isAnnotationPresent(ClickView.class)) {
                ClickView clickView=method.getAnnotation(ClickView.class);
                int[] ids=clickView.value();
                final HashMap<String,Method> map=new HashMap<>();
                map.put("onClick",method);
                try {
                    for(int id :ids) {
                        View view = activity.findViewById(id);

                        if (view==null) {
                            return;

                        }
                        View.OnClickListener o = (View.OnClickListener) Proxy.newProxyInstance(View.OnClickListener.class.getClassLoader(),
                                new Class[]{View.OnClickListener.class}, new InvocationHandler() {
                                    @Override
                                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                                        Method method1 = map.get(method.getName());
                                        if (method1 != null) {
                                            return method1.invoke(activity, args);
                                        }
                                        return null;
                                    }
                                });


                        Method clickMethodListener = view.getClass().getMethod("setOnClickListener", View.OnClickListener.class);
                        clickMethodListener.setAccessible(true);
                        clickMethodListener.invoke(view,o);
                    }
                }catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}

