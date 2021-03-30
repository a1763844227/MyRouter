package com.jh.router;

import android.app.Activity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * create by jh on 2021/3/23.
 */
public class ViewInject {

    public static void inject(Activity activity){
        Class aClass =activity.getClass();
        try {
            Class bindingClass=Class.forName(aClass.getName()+"_binding");
            Method method =bindingClass.getMethod("bindView",activity.getClass());
            method.invoke(bindingClass.newInstance(),activity);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

    }
}
