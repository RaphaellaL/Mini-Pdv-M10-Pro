package com.example.mini_pdv_m10_pro;

import android.app.Presentation;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.widget.TextView;

public class CustomerPresentation extends Presentation {

    public CustomerPresentation(Context context, Display display) {
        super(context, display);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_layout);

        TextView txt = findViewById(R.id.txtDisplay);
        txt.setText("R$ 0,00");
    }
}
