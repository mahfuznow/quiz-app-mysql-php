package just.cse.mahfuz.quizsolving;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import static just.cse.mahfuz.quizsolving.Utils.showAlertDialogue;


public class QuizResultActivity extends AppCompatActivity {

    TextView resultText, pointText;
    Button okButton;
    Date internetDate = null;

    int result,totalPoint;

    int pointPerAd=0, pointPerQuestion =0;

    String point;
    SharedPrefManager sharedPrefManager;

    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_result);


        getSupportActionBar().setTitle("Result");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sharedPrefManager= new SharedPrefManager(QuizResultActivity.this);

        resultText = findViewById(R.id.scoreText);
        pointText = findViewById(R.id.pointText);
        okButton = findViewById(R.id.okButton);
        dialog = new ProgressDialog(QuizResultActivity.this);

        getCurrentPointValueFromServer();

    }





    public  void getCurrentPointValueFromServer() {

        dialog.setMessage("Please Wait ...");
        dialog.setCancelable(false);
        dialog.show();

        AndroidNetworking.get(Constants.getROOT_URL()+"getCurrentPointValue.php")
                .setTag("test")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            pointPerAd= Integer.parseInt(response.getString("PointPerAd"));
                            pointPerQuestion = Integer.parseInt(response.getString("PointPerQuestion"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        setContents();
                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                    }
                });
    }


    private void  setContents() {
        result = getIntent().getIntExtra("result", 0);
        point=getIntent().getExtras().getString("point","false");



        if ("true".equals(point)){
            totalPoint=pointPerAd+(result* pointPerQuestion);
        }
        else {
            totalPoint=(result* pointPerQuestion);
            pointPerAd=0;
        }

        resultText.setText("Congratulations! You have answered ");
        resultText.append("" + result);
        resultText.append(" questions correctly.");

        pointText.setText("You have earned ");
        pointText.append("" + result* pointPerQuestion);
        pointText.append(" points for correct answer!");
        pointText.append(" "+"and"+" "+ pointPerAd);
        pointText.append(" points for ad clicking!");


        updateBalance(sharedPrefManager.getPhone(),String.valueOf(totalPoint));
    }

    private void updateBalance(String phone,String totalPoint) {

            AndroidNetworking.post(Constants.getROOT_URL()+"addPointToUser.php")
                    .addBodyParameter("phone", phone)
                    .addBodyParameter("totalPoint", totalPoint)
                    .setTag("test")
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            //enabling ok button after response
                            dialog.dismiss();
                                        okButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                finish();
                                            }
                                        });
                        }
                        @Override
                        public void onError(ANError error) {
                            // handle error
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



        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }














    private void updateSharedPreference() {

    }

    private class getTimeThenUpdate extends AsyncTask<Void, Void, Void> {

        protected Void doInBackground(Void... param) {
            long nowAsPerDeviceTimeZone = 0;
            SntpClient sntpClient = new SntpClient();
            if (sntpClient.requestTime("0.africa.pool.ntp.org", 30000)) {
                nowAsPerDeviceTimeZone = sntpClient.getNtpTime();
            }
            internetDate = new Date(nowAsPerDeviceTimeZone);
            return null;
        }

        protected void onPostExecute(Void param) {
            if (internetDate == null) {
                if (dialog.isShowing()) dialog.dismiss();
                showAlertDialogue("Time Check Failed!", "Please try again later!", QuizResultActivity.this);
                return;
            }

        }
    }
}
