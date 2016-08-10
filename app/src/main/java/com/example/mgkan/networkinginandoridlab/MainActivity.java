package com.example.mgkan.networkinginandoridlab;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
  List<String> listString;
  Button chocolateButton, teaButton, cerealButton;
  ListView listView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    chocolateButton = (Button) findViewById(R.id.chocolateButton);
    teaButton = (Button) findViewById(R.id.teaButton);
    cerealButton = (Button) findViewById(R.id.cerealButton);
    listView = (ListView) findViewById(R.id.listView);

    ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

    if (networkInfo != null && networkInfo.isConnected()) {
      chocolateButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          new runTask().execute("http://api.walmartlabs.com/v1/search?query=chocolate&format=json&apiKey=e63kyqcgnsba7as3ud7zd5hm");
        }
      });
      cerealButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          new runTask().execute("http://api.walmartlabs.com/v1/search?query=cereal&format=json&apiKey=e63kyqcgnsba7as3ud7zd5hm");
        }
      });
      teaButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          new runTask().execute("http://api.walmartlabs.com/v1/search?query=tea&format=json&apiKey=e63kyqcgnsba7as3ud7zd5hm");
        }
      });

      // the connection is available
    } else {
      Toast.makeText(MainActivity.this, "The Connection Is NOT Available", Toast.LENGTH_SHORT).show();
    }

  }

  private List<String> downloadUrl(String myUrl) throws IOException, JSONException {
    InputStream is = null;
    try {
      URL url = new URL(myUrl);
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("GET");
      conn.setDoInput(true);

      // Starts the query
      conn.connect();
      is = conn.getInputStream();

      // Converts the InputStream into a string
      String contentAsString = readIt(is);
      listString = parseJson(contentAsString);
      return listString;

      // Makes sure that the InputStream is closed after the app is
      // finished using it.
    } finally {
      if (is != null) {
        is.close();
      }
    }
  }

  private class runTask extends AsyncTask<String, Void, List<String>> {

    @Override
    protected List<String> doInBackground(String... params) {
      listString= new ArrayList<>();
      try {
        return downloadUrl(params[0]);
      } catch (JSONException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
      return listString;
    }

    @Override
    protected void onPostExecute(List<String> listString) {
      ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1,listString);
      listView.setAdapter(arrayAdapter);
    }
  }

  public String readIt(InputStream stream) throws IOException {
    StringBuilder sb = new StringBuilder();
    BufferedReader br = new BufferedReader(new InputStreamReader(stream));
    String read;

    while((read = br.readLine()) != null) {
      sb.append(read);
    }
    return sb.toString();
  }

  private List<String> parseJson(String contentAsString) throws JSONException {
    JSONObject object = new JSONObject(contentAsString);

    JSONArray arrayItems = object.getJSONArray("items");

    for (int i = 0; i < arrayItems.length(); i++)
    {
      JSONObject item = arrayItems.getJSONObject(i);
      String name = item.getString("name");
      listString.add(name);
    }
    return listString;
  }

}



