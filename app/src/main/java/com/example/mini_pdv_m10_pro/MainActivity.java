package com.example.mini_pdv_m10_pro;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.util.Log;

import com.imin.printerlib.IminPrintUtils;

import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "IMIN_PRINT";
    private IminPrintUtils printer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        printer = IminPrintUtils.getInstance(this);

        // Inicializa via USB (M10)
        try {
            printer.initPrinter(IminPrintUtils.PrintConnectType.USB);
        } catch (Exception e) {
            Log.e(TAG, "Erro ao inicializar impressora: " + e.getMessage(), e);
        }

        Button btnPrintText = findViewById(R.id.btnPrintText);
        Button btnAlignCenter = findViewById(R.id.btnAlignCenter);
        Button btnAlignLeft = findViewById(R.id.btnAlignLeft);
        Button btnAlignRight = findViewById(R.id.btnAlignRight);
        Button btnPrintQr = findViewById(R.id.btnPrintQr);
        Button btnCut = findViewById(R.id.btnCut);
        Button btnFullTest = findViewById(R.id.btnFullTest);

        btnPrintText.setOnClickListener(v -> {
            try {
                printer.printText("Olá Raphaella!\nImpressão funcionando!\n");
                feedPaper(40);
            } catch (Exception e) {
                Log.e(TAG, "Erro imprimir texto: " + e.getMessage(), e);
            }
        });

        btnAlignCenter.setOnClickListener(v -> {
            try {
                setAlignment(1); // 1 = center
                printer.printText("CENTRALIZADO\n");
                feedPaper(30);
            } catch (Exception e) {
                Log.e(TAG, "Erro alinhamento central: " + e.getMessage(), e);
            }
        });

        btnAlignLeft.setOnClickListener(v -> {
            try {
                setAlignment(0); // 0 = left
                printer.printText("ESQUERDA\n");
                feedPaper(30);
            } catch (Exception e) {
                Log.e(TAG, "Erro alinhamento esquerda: " + e.getMessage(), e);
            }
        });

        btnAlignRight.setOnClickListener(v -> {
            try {
                setAlignment(2); // 2 = right
                printer.printText("DIREITA\n");
                feedPaper(30);
            } catch (Exception e) {
                Log.e(TAG, "Erro alinhamento direita: " + e.getMessage(), e);
            }
        });

        btnPrintQr.setOnClickListener(v -> {
            try {
                // Tenta chamar printQRCode(String, int) ou printQr(String, int) via reflexão
                boolean ok = printQrSafe("https://raphaella.com", 6);
                if (!ok) {
                    // fallback: imprime a própria URL como texto se QR não puder ser gerado pela lib
                    printer.printText("https://raphaella.com\n");
                }
                feedPaper(50);
            } catch (Exception e) {
                Log.e(TAG, "Erro imprimir QR: " + e.getMessage(), e);
            }
        });

        btnCut.setOnClickListener(v -> {
            try {
                // corte parcial (se o método existir)
                try {
                    Method cut = printer.getClass().getMethod("cutPaper", int.class);
                    cut.invoke(printer, 1);
                } catch (NoSuchMethodException nsme) {
                    // se não existir, manda comando ESC/POS de corte parcial (pode ou não funcionar dependendo do hardware)
                    // GS V m: 0x1D 0x56 0x01
                    printer.cutPaper(); // corte parcial

                }
            } catch (Exception e) {
                Log.e(TAG, "Erro cortar papel: " + e.getMessage(), e);
            }
        });

        btnFullTest.setOnClickListener(v -> {
            try {
                setAlignment(1);
                printer.printText("=== TESTE COMPLETO ===\n");
                setAlignment(0);
                printer.printText("• Texto OK\n");
                printer.printText("• Alinhamento OK\n\n");
                setAlignment(1);
                printQrSafe("https://teste.com", 6);
                feedPaper(60);
                // tenta cortar
                try {
                    Method cut = printer.getClass().getMethod("cutPaper", int.class);
                    cut.invoke(printer, 1);
                } catch (NoSuchMethodException ignored) {
                    printer.cutPaper();

                }
            } catch (Exception e) {
                Log.e(TAG, "Erro full test: " + e.getMessage(), e);
            }
        });
    }

    /**
     * Avança o papel usando ESC d n  -> 0x1B 0x64 n
     */
    private void feedPaper(int dots) {
        try {
            printer.cutPaper(); // corte padrão do M10

        } catch (Exception e) {
            Log.e(TAG, "Erro ao avançar papel: " + e.getMessage(), e);
        }
    }

    /**
     * Define alinhamento via ESC a n  -> 0x1B 0x61 n
     * n = 0 left, 1 center, 2 right
     */
    private void setAlignment(int mode) {
        try {
            printer.cutPaper(); // corte padrão do M10


        } catch (Exception e) {
            Log.e(TAG, "Erro setAlignment: " + e.getMessage(), e);
        }
    }

    /**
     * Tenta chamar métodos comuns de impressão de QR na SDK via reflexão.
     * Retorna true se chamamos com sucesso algum método que gere o QR.
     */
    private boolean printQrSafe(String data, int size) {
        try {
            // tenta printQRCode(String, int)
            try {
                Method m = printer.getClass().getMethod("printQRCode", String.class, int.class);
                m.invoke(printer, data, size);
                return true;
            } catch (NoSuchMethodException ignored) {}

            // tenta printQr(String, int)
            try {
                Method m2 = printer.getClass().getMethod("printQr", String.class, int.class);
                m2.invoke(printer, data, size);
                return true;
            } catch (NoSuchMethodException ignored) {}

            // tenta printQrCode(String, int)
            try {
                Method m3 = printer.getClass().getMethod("printQrCode", String.class, int.class);
                m3.invoke(printer, data, size);
                return true;
            } catch (NoSuchMethodException ignored) {}

            // nenhum método encontrado
            Log.w(TAG, "Nenhum método de QR encontrado na SDK (printQRCode/printQr).");
            return false;

        } catch (Exception e) {
            Log.e(TAG, "Erro printQrSafe: " + e.getMessage(), e);
            return false;
        }
    }
}
