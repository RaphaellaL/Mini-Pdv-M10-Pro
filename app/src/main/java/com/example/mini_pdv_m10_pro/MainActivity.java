package com.example.mini_pdv_m10_pro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.display.DisplayManager;
import android.os.Bundle;
import android.view.Display;
import android.widget.Button;
import android.util.Log;

import com.imin.image.ILcdManager;
import com.imin.printerlib.IminPrintUtils;

import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {

    static {
        try {
            System.loadLibrary("free_image");
            Log.d("LCD", "libfree_image carregada!");
        } catch (Exception e) {
            Log.e("LCD", "Erro ao carregar lib: " + e.getMessage());
        }
    }

    private static final String TAG = "IMIN_PRINT";
    private IminPrintUtils printer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        printer = IminPrintUtils.getInstance(this);

        DisplayManager displayManager =
                (DisplayManager) getSystemService(Context.DISPLAY_SERVICE);

        Display[] displays = displayManager.getDisplays(
                DisplayManager.DISPLAY_CATEGORY_PRESENTATION
        );

        if (displays.length > 0) {
            Display display = displays[0];

            CustomerPresentation presentation =
                    new CustomerPresentation(this, display);

            presentation.show();
        } else {
            Log.e("DISPLAY", "Nenhuma tela secundária encontrada");
        }

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
        Button btnDisplay = findViewById(R.id.btnDisplay);

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
                setAlignment(1);
                printer.printText("CENTRALIZADO\n");
                feedPaper(30);
            } catch (Exception e) {
                Log.e(TAG, "Erro alinhamento central: " + e.getMessage(), e);
            }
        });

        btnAlignLeft.setOnClickListener(v -> {
            try {
                setAlignment(0);
                printer.printText("ESQUERDA\n");
                feedPaper(30);
            } catch (Exception e) {
                Log.e(TAG, "Erro alinhamento esquerda: " + e.getMessage(), e);
            }
        });

        btnAlignRight.setOnClickListener(v -> {
            try {
                setAlignment(2);
                printer.printText("DIREITA\n");
                feedPaper(30);
            } catch (Exception e) {
                Log.e(TAG, "Erro alinhamento direita: " + e.getMessage(), e);
            }
        });

        btnPrintQr.setOnClickListener(v -> {
            try {
                boolean ok = printQrSafe("https://raphaella.com", 6);
                if (!ok) {
                    printer.printText("https://raphaella.com\n");
                }
                feedPaper(50);
            } catch (Exception e) {
                Log.e(TAG, "Erro imprimir QR: " + e.getMessage(), e);
            }
        });

        btnCut.setOnClickListener(v -> {
            try {
                try {
                    Method cut = printer.getClass().getMethod("cutPaper", int.class);
                    cut.invoke(printer, 1);
                } catch (NoSuchMethodException nsme) {
                    printer.cutPaper();
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


            btnDisplay.setOnClickListener(v -> {
                try {

                    ILcdManager lcd = ILcdManager.getInstance(this);

                    lcd.sendLCDCommand(1);

                    Bitmap bitmap = Bitmap.createBitmap(240, 320, Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(bitmap);

                    // FUNDO PRETO
                    canvas.drawColor(Color.BLACK);

                    Paint paint = new Paint();
                    paint.setColor(Color.WHITE);
                    paint.setTextSize(50);
                    paint.setAntiAlias(true);

                    canvas.drawText("TESTE", 40, 140, paint);
                    canvas.drawText("RAPHAELLA", 10, 200, paint);

                    // 🔥 ESSA LINHA MUDA TUDO
                    lcd.sendLCDMinBitmap(bitmap, 1);

                    Log.d("LCD", "Bitmap enviado!");

                } catch (Exception e) {
                    Log.e(TAG, "Erro display: " + e.getMessage(), e);
                }
            });
    }

    private void feedPaper(int dots) {
        try {
            printer.cutPaper();
        } catch (Exception e) {
            Log.e(TAG, "Erro ao avançar papel: " + e.getMessage(), e);
        }
    }

    private void setAlignment(int mode) {
        try {
            // (placeholder - depois te ensino o comando correto)
        } catch (Exception e) {
            Log.e(TAG, "Erro setAlignment: " + e.getMessage(), e);
        }
    }

    private boolean printQrSafe(String data, int size) {
        try {
            try {
                Method m = printer.getClass().getMethod("printQRCode", String.class, int.class);
                m.invoke(printer, data, size);
                return true;
            } catch (NoSuchMethodException ignored) {}

            try {
                Method m2 = printer.getClass().getMethod("printQr", String.class, int.class);
                m2.invoke(printer, data, size);
                return true;
            } catch (NoSuchMethodException ignored) {}

            try {
                Method m3 = printer.getClass().getMethod("printQrCode", String.class, int.class);
                m3.invoke(printer, data, size);
                return true;
            } catch (NoSuchMethodException ignored) {}

            Log.w(TAG, "Nenhum método de QR encontrado.");
            return false;

        } catch (Exception e) {
            Log.e(TAG, "Erro printQrSafe: " + e.getMessage(), e);
            return false;
        }
    }
}