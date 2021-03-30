//
//                         _oo8oo_
//                        o8888888o
//                        88" . "88
//                        (| -_- |)
//                        0\  =  /0
//                      ___/'==='\___
//                    .' \\|     |// '.
//                   / \\|||  :  |||// \
//                  / _||||| -:- |||||_ \
//                 |   | \\\  -  /// |   |
//                 | \_|  ''\---/''  |_/ |
//                 \  .-\__  '-'  __/-.  /
//               ___'. .'  /--.--\  '. .'___
//            ."" '<  '.___\_<|>_/___.'  >' "".
//           | | :  `- \`.:`\ _ /`:.`/ -`  : | |
//           \  \ `-.   \_ __\ /__ _/   .-` /  /
//        =====`-.____`.___ \_____/ ___.`____.-`=====
//                         `=---=`
//
//                 佛祖保佑         永无bug
package com.jh.annotationtext;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER,ElementType.LOCAL_VARIABLE})
public @interface Param {
     String value();
}
