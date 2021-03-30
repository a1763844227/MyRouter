package com.jh.router;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * create by jh on 2021/3/26.
 */
public class MyRouter {

    private static final String TAG = "MyRouter";
    public Map<String,Class<?>> routes=new HashMap<>();
    public Map<String,Class<? extends IRouterProvider>> providersGroup =new HashMap<>();

    public Map<Class<? extends IRouterProvider>,IRouterProvider> providers=new HashMap<>();

    private static Application application;

    private volatile static boolean hasInit;

    public volatile static MyRouter myRouter;

//    private Map<String,Class<?>>


    private static final String ROUTES_PACKAGE_NAME="com.jh.router.routes";

    private MyRouter() {

    }

    public void init(Application context){
        if (!hasInit) {
            application =context;
            register(context);
        }
        hasInit=true;
    }

    public static MyRouter getInstance(){
        if (myRouter==null) {
            synchronized (MyRouter.class){
                if (myRouter==null) {
                    myRouter=new MyRouter();
                }
            }
        }

        return myRouter;
    }

//    public void navigation(Activity context, int requestCode) {
//        Intent intent = new Intent();
//        intent.setComponent(new ComponentName(context.getPackageName(), activityName));
//        intent.putExtras(mBundle);
//        context.startActivityForResult(intent, requestCode);
//    }

    private  void register(Context context){

        try {
            Set<String> setClass=ClassUtils.getFileNameByPackageName(context,ROUTES_PACKAGE_NAME);

            for(String className  :setClass){
                if (className.startsWith(ROUTES_PACKAGE_NAME)) {
                    Object object = Class.forName(className).getConstructor().newInstance();
                    if (object instanceof IRouterGroup) {
                        ((IRouterGroup) object).loadInto(routes);
                    }else if(object instanceof  IProviderGroup){
                        ((IProviderGroup) object).loadInto(providersGroup);
                    }
                }

            }

            Log.i(TAG, "register: "+routes.toString() );
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    /**
     * 界面跳转
     */
    public PostCard build(String path){
        if (!hasInit) {
            throw new RuntimeException("MyRouter has not init");
        }
        if (TextUtils.isEmpty(path)) {
            throw new RuntimeException("router path is empty");
        }
        return new PostCard(path);
    }

    public PostCard buildProvider(String path){
        if (!hasInit) {
            throw new RuntimeException("MyRouter has not init");
        }
        if (TextUtils.isEmpty(path)) {
            throw new RuntimeException("router path is empty");
        }
        return new PostCard(path,false);
    }



}
