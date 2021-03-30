package com.jh.router;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.text.TextUtils;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * create by jh on 2021/3/26.
 */
public class PostCard {

    private String path;
    private Bundle bundle;
    private Intent intent;
    private int flag = -1;
    private int addFlag=-1;
    private int enterAnim = -1;
    private int exitAnim = -1;

    private String action;


    private Bundle optionsCompat;    // The transition animation of activity

    public PostCard(String path) {
        this.path = path;
        intent=new Intent();
    }

    public PostCard(String path,boolean initIntent) {
        this.path = path;
        intent=initIntent? new Intent():null;
    }


    public Intent getIntent() {
        return intent;
    }

    public PostCard setIntent(Intent intent) {
        this.intent = intent;
        return this;

    }

    public int getFlag() {
        return flag;
    }

    public PostCard setFlag(int flag) {
        this.flag = flag;
        if (flag!=-1) {
            intent.setFlags(flag);
        }
        return this;
    }

    public int getAddFlag() {
        return addFlag;
    }

    public PostCard setAddFlag(int addFlag) {
        this.addFlag = addFlag;
        if (addFlag!=-1) {
            intent.addFlags(addFlag);
        }
        return this;
    }

    public PostCard addAndSetFlag(int flag,int addFlag) {
        this.addFlag = addFlag;
        this.flag=flag;
        if (flag!=-1) {
            intent.setFlags(flag);
        }
        if (addFlag!=-1) {
            intent.addFlags(addFlag);
        }

        return this;
    }

    public int getEnterAnim() {
        return enterAnim;
    }


    public int getExitAnim() {
        return exitAnim;
    }


    public PostCard withTransition(int enterAnim, int exitAnim) {
        this.enterAnim = enterAnim;
        this.exitAnim = exitAnim;
        return this;
    }

    @RequiresApi(16)
    public PostCard withOptionsCompat(ActivityOptionsCompat compat) {
        if (null != compat) {
            this.optionsCompat = compat.toBundle();
        }
        return this;
    }


    public PostCard withAction(String action) {
        this.action = action;
        if (!TextUtils.isEmpty(action)) {
            intent.setAction(action);
        }
        return this;
    }

    public PostCard withSerializable(@Nullable String key, @Nullable Serializable value) {
        intent.putExtra(key, value);
        return this;
    }

    public PostCard withStringArrayList(@Nullable String key, @Nullable ArrayList<String> value) {
        intent.putExtra(key, value);
        return this;
    }

    public PostCard withParcelableArrayList(@Nullable String key, @Nullable ArrayList<? extends Parcelable> value) {
        intent.putExtra(key, value);
        return this;
    }

    public PostCard withParcelable(@Nullable String key, @Nullable Parcelable value) {
        intent.putExtra(key, value);
        return this;
    }

    public PostCard withFloat(@Nullable String key, float value) {
        intent.putExtra(key, value);
        return this;
    }

    public PostCard withString(@Nullable String key, String value) {
        intent.putExtra(key, value);
        return this;
    }
    public PostCard withBoolean(@Nullable String key, boolean value) {
        intent.putExtra(key, value);
        return this;
    }

    public PostCard withInt(@Nullable String key, int value) {
        intent.putExtra(key, value);
        return this;
    }


    public void navigation(Context context,int requestCode){
        Class<?> aClass = MyRouter.getInstance().routes.get(path);
        if (aClass!=null) {
            intent.setClass(context,aClass);

            if (requestCode>=0) {
                if (context instanceof Activity) {
                    ActivityCompat.startActivityForResult((Activity) context, intent, requestCode, optionsCompat);
                }
            }else {
                ActivityCompat.startActivity(context, intent, optionsCompat);

            }

            if ((-1 != enterAnim && -1 != exitAnim) && context instanceof Activity) {    // Old version.
                ((Activity) context).overridePendingTransition(enterAnim, exitAnim);
            }

        }


    }

    public void navigation(Context context){
        navigation(context,-1);
    }


    public  IRouterProvider navigation(Class<? extends IRouterProvider> providerClass,Context context){
        IRouterProvider iRouterProvider=MyRouter.getInstance().providers.get(providerClass);

        try {
            if (null == iRouterProvider) {
                Class<? extends IRouterProvider>  aClass = MyRouter.getInstance().providersGroup.get(path);
                if (aClass!=null) {
                    iRouterProvider= aClass.getConstructor().newInstance();
                    iRouterProvider.init(context);
                    MyRouter.getInstance().providers.put(aClass,iRouterProvider);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return iRouterProvider;

    }

}
