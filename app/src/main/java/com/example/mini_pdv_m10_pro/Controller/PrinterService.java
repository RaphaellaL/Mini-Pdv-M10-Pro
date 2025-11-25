package com.example.mini_pdv_m10_pro.Controller;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.elgin.e1.Impressora.Termica;

public class PrinterService {

    private static final String TAG = "PrinterService";
    private final Context context;
    private boolean connected = false;

    public PrinterService(Context context) {
        this.context = context;
    }

    /** Conecta à impressora interna da M10 Pro */
    public boolean connect() {
        Log.i(TAG, "Abrindo conexão...");

        // evita erro "Access denied finding property ro.serialno"
        try {
            if (context instanceof Activity) {
                //Termica.setActivity((Activity) context);
            } else {
                Log.w(TAG, "Context não é Activity — conexão pode limitar funções.");
            }
        } catch (Throwable t) {
            Log.w(TAG, "Falha ao setar Activity (ignorado)", t);
        }

        // Modelo 5 = impressora interna M10/M10 Pro
        int response = Termica.AbreConexaoImpressora(5, "", "", 0);
       //int response = Termica.AbreConexaoImpressora(1, "USB", "VIDPID=0x28BD,0x0203", 0);

        Log.i(TAG, "AbreConexaoImpressora retorno: " + response);

        if (response != 0) {
            connected = false;
            return false;
        }

        try {
            Termica.InicializaImpressora();
        } catch (Exception e) {
            Log.e(TAG, "Erro ao inicializar impressora", e);
        }

        connected = true;
        return true;
    }

    /** Imprime texto simples */
    public boolean printText(String text) {
        if (!connected) {
            if (!connect()) return false;
        }

        try {
            Termica.DefinePosicao(0);
            Termica.ImpressaoTexto(text, 0, 0, 0);
            Termica.AvancaPapel(10);

            // corte suportado apenas com 1 parâmetro no M10 Pro
            try {
                int cut = Termica.Corte(1);
                Log.i(TAG, "Corte retorno: " + cut);
            } catch (Throwable t) {
                Log.w(TAG, "Corte não suportado nesta impressora", t);
            }

            return true;
        } catch (Exception e) {
            Log.e(TAG, "Erro ao imprimir texto", e);
            return false;
        }
    }

    /** Imprime QR Code */
    public boolean printQRCode(String data, int model, int size) {
        if (!connected) {
            if (!connect()) return false;
        }

        try {
            int qrRet = Termica.ImpressaoQRCode(data, model, size);
            Log.i(TAG, "QR retorno: " + qrRet);

            Termica.AvancaPapel(5);

            return qrRet >= 0;
        } catch (Exception e) {
            Log.e(TAG, "Erro ao imprimir QR", e);
            return false;
        }
    }

    /** Encerra conexão */
    public void disconnect() {
        try {
            int ret = Termica.FechaConexaoImpressora();
            Log.i(TAG, "FechaConexaoImpressora retorno: " + ret);
        } catch (Exception e) {
            Log.e(TAG, "Erro ao fechar conexão", e);
        } finally {
            connected = false;
        }
    }
}
