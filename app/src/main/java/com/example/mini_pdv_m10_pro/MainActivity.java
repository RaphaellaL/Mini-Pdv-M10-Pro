package com.example.mini_pdv_m10_pro;

import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mini_pdv_m10_pro.Controller.PrinterService;
import com.elgin.e1.Impressora.Termica;
import com.imin.printerlib.IminPrintUtils;

public class MainActivity extends AppCompatActivity {

    private PrinterService printer;
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        printer = new PrinterService(this);

        Button btnConnect = findViewById(R.id.btnConnect);
        Button btnPrintText = findViewById(R.id.btnPrintText);
        Button btnPrintQr = findViewById(R.id.btnPrintQr);
        Button btnDrawer = findViewById(R.id.btnDrawer);
        Button btnDisconnect = findViewById(R.id.btnDisconnect);

        btnConnect.setOnClickListener(v -> {

            IminPrintUtils printer = IminPrintUtils.getInstance(context);

            printer.initPrinter();
            printer.printText("Olá mundo\n");
            printer.printAndFeed(50);



//            boolean ok = printer.connect();
//            Toast.makeText(this, ok ? "Conectado!" : "Falha ao conectar!", Toast.LENGTH_SHORT).show();
        });

        btnPrintText.setOnClickListener(v -> {
            boolean ok = printer.printText("Teste de impressão\nMini PDV M10 Pro");
            Toast.makeText(this, ok ? "Impresso!" : "Erro ao imprimir!", Toast.LENGTH_SHORT).show();
        });

        btnPrintQr.setOnClickListener(v -> {
            try {
                Termica.DefinePosicao(0);
                Termica.ImpressaoQRCode("https://google.com", 4, 0);
                Termica.AvancaPapel(10);
                Toast.makeText(this, "QR impresso!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "Erro QR!", Toast.LENGTH_SHORT).show();
            }
        });

        btnDrawer.setOnClickListener(v -> {
            try {
                Termica.AbreGavetaElgin();
                Toast.makeText(this, "Gaveta acionada!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "Erro ao abrir gaveta!", Toast.LENGTH_SHORT).show();
            }
        });

        btnDisconnect.setOnClickListener(v -> {
            printer.disconnect();
            Toast.makeText(this, "Desconectado!", Toast.LENGTH_SHORT).show();
        });
    }
}
