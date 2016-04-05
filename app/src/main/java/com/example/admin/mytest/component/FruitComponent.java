package com.example.admin.mytest.component;

import com.example.admin.mytest.bean.Container;
import com.example.admin.mytest.module.FruitModule;

import dagger.Component;

/**
 * Created by admin on 2016-03-26.
 */
@Component(modules = {FruitModule.class})
public interface FruitComponent {
    void inject(Container container);
}
