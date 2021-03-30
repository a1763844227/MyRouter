package com.jh.router;


import java.util.Map;

/**
 * create by jh on 2021/3/25.
 *
 */
public interface IProviderGroup {

    void loadInto(Map<String, Class<? extends IRouterProvider>> providers);

}
