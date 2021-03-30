package com.jh.router;

import java.util.Map;

/**
 * create by jh on 2021/3/25.
 *
 * 也可以使用Map<String,Sting> 保存类路径
 */
public interface IRouterGroup {

    void loadInto(Map<String,Class<?>> routs);
}
