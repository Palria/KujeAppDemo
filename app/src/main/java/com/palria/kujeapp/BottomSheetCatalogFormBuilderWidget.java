package com.palria.kujeapp;

import android.content.Context;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.palria.kujeapp.widgets.BottomSheetFormBuilderWidget;

public class BottomSheetCatalogFormBuilderWidget extends BottomSheetFormBuilderWidget {
    Context context;
    public BottomSheetCatalogFormBuilderWidget(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    public ImageView addImage(ImageView imageView){
        formLayout.addView(imageView);
        return imageView;
    }

}
