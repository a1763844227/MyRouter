package com.jh.plugin

import com.android.build.gradle.AppExtension
import com.jh.plugin.router.RouterTransform
import org.gradle.api.Plugin
import org.gradle.api.Project

import javax.sound.midi.Track


public class MyPlugin implements Plugin<Project>{

    @Override
    void apply(Project target) {

        def  android =target.extensions.getByType(AppExtension)
        android.registerTransform(new RouterTransform(target))
        print("+++++++++++++++++++++++")
        print("ceshi plugin yixia")
        print("=======================")

    }
}