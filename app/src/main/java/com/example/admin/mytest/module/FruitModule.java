package com.example.admin.mytest.module;

import android.graphics.Color;

import com.example.admin.mytest.bean.Apple;
import com.example.admin.mytest.bean.Fruit;

import dagger.Module;
import dagger.Provides;

/**
 * Created by admin on 2016-03-26.
 */
@Module
public class FruitModule {
    @Provides
    public Fruit provideFruit(){
        return new Apple(Color.RED, 100);
    }
}
