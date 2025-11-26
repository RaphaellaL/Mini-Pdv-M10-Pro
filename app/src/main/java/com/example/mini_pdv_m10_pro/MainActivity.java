package com.example.mini_pdv_m10_pro;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.imin.printerlib.IminPrintUtils;

public class MainActivity extends AppCompatActivity {

    IminPrintUtils printer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        printer = IminPrintUtils.getInstance(this);

        // Inicializa impressora interna do M10 Pro
        printer.initPrinter(IminPrintUtils.PrintConnectType.INNER);

        // Teste
        printer.printText("Teste de impressão\nMini PDV M10 Pro\n");
        printer.printAndFeedPaper(30);
    }
}