package com.jh.plugin

import org.gradle.api.Project


class Logger {
    static org.gradle.api.logging.Logger logger

    static void make(Project project) {
        logger = project.getLogger()
    }

    static void i(String info) {
        if (null != info && null != logger) {
            logger.info("ARouter::Register >>> " + info)
        }

        def iamList=[1,23,1]
        iamList.each {}
        


    }

    static void e(String error) {
        if (null != error && null != logger) {
            logger.error("ARouter::Register >>> " + error)
        }
    }

    static void w(String warning) {
        if (null != warning && null != logger) {
            logger.warn("ARouter::Register >>> " + warning)
        }
    }
}
