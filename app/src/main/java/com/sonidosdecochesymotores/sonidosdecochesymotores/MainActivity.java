package com.sonidosdecochesymotores.sonidosdecochesymotores;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdValue;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnPaidEventListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    //final String IDBANNER = "ca-app-pub-3940256099942544/6300978111";  //prueba
    // String IDBANNER = "ca-app-pub-3237439786100647/3882298242";  //real

    ListView lvCoches;
    List<Coche> listaCoches;
    TextView nombreDeLaApp;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toast.makeText(getApplicationContext(), getString(R.string.avisoLongClick), Toast.LENGTH_LONG).show();
        FirebaseCrashlytics.getInstance().setCustomKey("Main Activity", Build.VERSION.SDK_INT);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        pantallaCompleta();
        inicializar();
        lvCoches = (ListView) findViewById(R.id.lvCoches);
        lvCoches.setAdapter(new AdaptadorCoche(getApplicationContext(), R.layout.activity_adaptador_coche, (ArrayList<Coche>) listaCoches));
        lvCoches.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cocheSeleccionado(false, position);
            }
        });
        lvCoches.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                cocheSeleccionado(true, position);
                return true;
            }
        });
        nombreDeLaApp = findViewById(R.id.nombreDeLaApp);
        String appName = nombreDeLaApp.getText().toString();
        Spannable wordtoSpan = new SpannableString(appName);
        wordtoSpan.setSpan(new ForegroundColorSpan(Color.parseColor("#81C4FF")), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        wordtoSpan.setSpan(new ForegroundColorSpan(Color.parseColor("#16588E")), 1, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        wordtoSpan.setSpan(new ForegroundColorSpan(Color.parseColor("#E7222E")), 2, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        nombreDeLaApp.setText(wordtoSpan);
        nombreDeLaApp.setClickable(true);
        nombreDeLaApp.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getApplicationContext(), "¡Gracias a Peio Mena por ayudarme a crear mi primera app!", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    public void cocheSeleccionado(boolean bloqueo, int position) {
        intent = new Intent(getApplicationContext(), MostrarCoche.class);
        Coche coche = listaCoches.get(position);
        intent.putExtra("coche", coche);
        if (bloqueo) intent.putExtra("bloqueo", true);
        startActivity(intent);
    }

    private void anadirBanner() {
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        AdRequest adRequest = new AdRequest.Builder().build();
        AdView mAdView = findViewById(R.id.adViewMain);
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                pantallaCompleta();
            }
        });
    }

    private void pantallaCompleta() {
        getSupportActionBar().hide();
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

    private void inicializar() {
        listaCoches = new ArrayList<>();
        AssetManager am = getApplicationContext().getAssets();
        InputStream is = null;
        Coche c = null;
        List<String> nombresEnOtroIdioma = null;
        int contador = 0;
        if (Locale.getDefault().getLanguage().equals("zh") || Locale.getDefault().getLanguage().equals("hi")) {
            InputStream isOtroIdioma = null;
            try {
                isOtroIdioma = am.open("nombres" + Locale.getDefault().getLanguage() + ".txt");
                nombresEnOtroIdioma = new ArrayList<>();
                InputStreamReader osw = new InputStreamReader(isOtroIdioma);
                BufferedReader br = new BufferedReader(osw);
                while (br.ready()) {
                    nombresEnOtroIdioma.add(br.readLine());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            is = am.open("nombres.txt");
            InputStreamReader osw = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(osw);
            while (br.ready()) {
                listaCoches.add(c = new Coche(br.readLine()));
                if (nombresEnOtroIdioma != null) {
                    c.setNombreEnOtroIdioma(nombresEnOtroIdioma.get(contador++));
                }
                String nombre = c.getNombre().toLowerCase().replaceAll(" ", "")
                        .toLowerCase().replace("-", "").replace("_", "").replace("(", "").replace(")", "");
                c.setImagen(getResources().getIdentifier(nombre.concat("imagen"), "raw", this.getPackageName()));
            }
        } catch (FileNotFoundException ignored) {
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        anadirBanner();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        pantallaCompleta();
    }
}