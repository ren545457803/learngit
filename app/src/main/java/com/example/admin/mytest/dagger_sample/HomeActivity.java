/*
 * Copyright (C) 2013 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.admin.mytest.dagger_sample;

import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.admin.mytest.R;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HomeActivity extends DemoActivity {

    static String TAG = "HomeActivity";

    @Inject
    LocationManager locationManager;
    @Bind(R.id.picasso)
    ImageView imageView;
    //    @Bind(R.id.facebook)
    SimpleDraweeView faceImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((DemoApplication) getApplication()).component().inject(this);

        // TODO do something with the injected dependencies here!
        Log.d("HomeActivity", locationManager.toString());
        setContentView(R.layout.home);

        ButterKnife.bind(this);

        Picasso picasso = Picasso.with(this);
        picasso.setIndicatorsEnabled(true);
        picasso.setLoggingEnabled(true);

        String urlStr = "http://f.hiphotos.baidu.com/image/pic/item/48540923dd54564e67a67603b4de9c82d0584fcd.jpg";
        /*picasso.load(urlStr)
                .placeholder(R.drawable.hehe)
                .error(R.drawable.ic_default_adimage)
                .into(imageView);*/


        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(Uri.parse(urlStr))
                .setAutoPlayAnimations(true)
                .build();
        faceImageView = new SimpleDraweeView(this);
        faceImageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//        faceImageView.setController(controller);
        faceImageView.setImageURI(Uri.parse(urlStr), this);

        ((ViewGroup) findViewById(R.id.parent)).addView(faceImageView);

        GenericDraweeHierarchy h = GenericDraweeHierarchyBuilder.newInstance(getResources())
                .build();


/*//        Uri uri = Uri.parse("res://com.example.admin.mytest/" + R.drawable.ic_default_adimage);
        Uri uri = Uri.parse("http://i.imgur.com/DvpvklR.png");
        Log.e(TAG, uri.toString());

        faceImageView.setImageURI(uri, this);*/
    }
}
