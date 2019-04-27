package just.cse.mahfuz.quizsolving;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Notification extends AppCompatActivity {

    ArrayList<ModelNotification> notifications;
    ListView listView;
    ListViewAdapterNotification listViewAdapterNotification;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redeem_history);

        listView=findViewById(R.id.listView);
        progressDialog= new ProgressDialog(Notification.this);

        getSupportActionBar().setTitle("Notification");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        notifications= new ArrayList<>();
        loadContents();
    }

    private void loadContents() {

        progressDialog.setMessage("Loading....");
        progressDialog.show();

        AndroidNetworking.get(Constants.getROOT_URL()+"loadNotifications.php")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        JSONObject jo;
                        ModelNotification modelNotification;
                        try
                        {
                            for(int i=0;i<response.length();i++)
                            {
                                jo=response.getJSONObject(i);

                                String timestamp=jo.getString("timestamp");
                                String msg=jo.getString("message");

                                modelNotification=new ModelNotification(timestamp,msg);
                                notifications.add(modelNotification);
                            }

                            //SET TO SPINNER
                            listViewAdapterNotification =new ListViewAdapterNotification(Notification.this,notifications);
                            listView.setAdapter(listViewAdapterNotification);
                            progressDialog.dismiss();

                        }catch (JSONException e)
                        {
                            progressDialog.dismiss();
                            Toast.makeText(Notification.this, "GOOD RESPONSE BUT JAVA CAN'T PARSE JSON IT RECEIEVED. "+e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }

                    //ERROR
                    @Override
                    public void onError(ANError anError) {
                        anError.printStackTrace();
                        progressDialog.dismiss();
                        Toast.makeText(Notification.this, "UNSUCCESSFUL :  ERROR IS : "+anError.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }


    //setting doted menu item
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
