package com.projeta3.user.checklist;


import android.graphics.Bitmap;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PropertyChangeListener, AdapterView.OnItemClickListener, View.OnClickListener

{
    private TextView mWelcome;
    private EditText mListe;
    private Button mValidation;
    private AutoCompleteTextView mRecherche;
    private ImageView mQRCode;
    private ConnectionJson connectionJSon;
    private List<Product> listProducts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectionJSon = new ConnectionJson();
        connectionJSon.execute("http://liste.ega.tf/product/");
        connectionJSon.addPropertyChangeListener(this);
        mWelcome = findViewById(R.id.activity_main_texte_bienvenue); //on fait le lien entre le fichier xml et le Java via les ID
        mListe = findViewById(R.id.activity_main_liste);
        mValidation = findViewById(R.id.activity_main_boutton_validation);
        mQRCode = findViewById(R.id.activity_main_qrCodeImage);
        mRecherche = findViewById(R.id.autoCompleteTextView);

        mValidation.setEnabled(false);

        mListe.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                mValidation.setEnabled(s.toString().length() != 0); //Une fois que l'utilisateur a rentré une lettre dans le champ, le boutton s'active
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mValidation.setOnClickListener(this);

    }
    @Override
    public void propertyChange(PropertyChangeEvent evt) {


        if (evt.getSource().equals(connectionJSon)) {
            Looper.prepare();

            if (evt.getPropertyName().equals("GET-ALL")) {

                List<String> values = new ArrayList<>();
                listProducts = (List<Product>) evt.getNewValue();
                for (int i = 0; i < listProducts.size(); i++)
                {
                    values.add(""+listProducts.get(i).name());
                }
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                        R.layout.liste_article, R.id.text_view_list, values);
                mRecherche.setAdapter(dataAdapter);
                connectionJSon.removePropertyChangeListener(this);

                mRecherche.setOnItemClickListener(this);
                Log.i("test", "Fin Lecture");
                Looper.loop();
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i("test", "Position=" + listProducts.get((int) id).name());
        String monTexte = "";
        if(mListe.getText()!=null){
            monTexte = mListe.getText().toString();
        }
        mListe.setText(monTexte + listProducts.get((int) id).name() +",");
        mRecherche.setText("");
    }

    @Override
    public void onClick(View view) {
        String text = mListe.getText().toString().trim();
        // Identifier les textes aux ID et coder les ID dns le QR code
        // separer les ID par des virgules et tout mettre sur une seule ligne

        if (text != null) {
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            try {
                BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE, 850, 850);
                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                mQRCode.setImageBitmap(bitmap);
            } catch (WriterException e) {
                e.printStackTrace();
            }
        }
    }
}
