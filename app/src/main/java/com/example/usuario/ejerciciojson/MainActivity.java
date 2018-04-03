package com.example.usuario.ejerciciojson;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

public class MainActivity extends AppCompatActivity {

    private ListView lvCiudades;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lvCiudades = findViewById(R.id.lvCiudades);
        findViewById(R.id.boton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ComunicacionTask com = new ComunicacionTask();
                com.execute("http://datos.alcobendas.org/" +
                        "dataset/c3002859-2d57-42b9-8aaf-a69e905e93fb/" +
                        "resource/8cac8866-91c2-43c3-baa0-0908ba2d0328/download/" +
                        "evolucionanualdelapoblacionpordistritos20112015.json");
            }
        });
    }

    private class ComunicacionTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String cadenaJson = "";
            try {
                URL url = new URL(params[0]);
                URLConnection con = url.openConnection();
                //recuperacion de la respuesta JSON
                String s;
                InputStream is = con.getInputStream();
                //utilizamos UTF-8 para que interprete
                //correctamente las ñ y acentos
                BufferedReader bf = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
                while ((s = bf.readLine()) != null) {
                    cadenaJson += s;
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return cadenaJson;
        }

        @Override
        protected void onPostExecute(String result) {
            String[] datosCiudad = null;
            try {
                //creamos un array JSON a partir de la cadena recibida
                JSONArray jarray = new JSONArray(result);
                //creamos el array de String con el tamaño
                //del array JSON
                datosCiudad = new String[jarray.length()];
                for (int i = 0; i < jarray.length(); i++) {
                    JSONObject job = jarray.getJSONObject(i);
                    //por cada objeto JSON, creamos una cadena
                    //con la propiedad año y la suma de
                    //habitantes de cada zona,el resultado lo
                    //añadimos al array
                    int habitantes = job.getInt("Centro") + job.getInt("Norte") + job.getInt("Urbanizaciones");
                    datosCiudad[i] = job.getString("Año") + " - " + habitantes;
                }
                cargarLista(datosCiudad);
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }

        private void cargarLista(String[] datos) {
            //creamos un arrayadapter con los datos del array
            //y lo asignamos al ListView
            ArrayAdapter<String> adp = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, datos);
            lvCiudades.setAdapter(adp);
        }
    }
}


