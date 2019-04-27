package just.cse.mahfuz.quizsolving;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
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

import static just.cse.mahfuz.quizsolving.Utils.checkInternet;
import static just.cse.mahfuz.quizsolving.Utils.fixMobileNumber;
import static just.cse.mahfuz.quizsolving.Utils.showAlertDialogue;


public class Redeem extends AppCompatActivity {

    TextView walletBalanceText, balanceWarning, otherWarning;
    EditText mobileText, amountText;
    RadioGroup withdrawGroup;
    Button withdrawButton;

    String paymentMethod, paymentMobileNumber;
    ProgressDialog dialog;
    int balance = 0, withdrawAmount;
    SharedPreferences preferences;

    AdView bannerTop, bannerBottom;
    private Date internetDate = null;

    String phone;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redeem);

        getSupportActionBar().setTitle("Redeem");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        walletBalanceText = findViewById(R.id.walletBalanceText);
        balanceWarning = findViewById(R.id.balanceWarning);
        otherWarning = findViewById(R.id.otherWarning);
        mobileText = findViewById(R.id.mobileText);
        withdrawGroup = findViewById(R.id.withdrawGroup);
        withdrawButton = findViewById(R.id.withdrawButton);
        amountText = findViewById(R.id.amountText);

        dialog = new ProgressDialog(Redeem.this);

        SharedPrefManager sharedPrefManager= new SharedPrefManager(Redeem.this);
        phone=sharedPrefManager.getPhone();

        loadBalance(phone);
        showBannerAds();
        updateRadioButton();
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

    protected void updateRadioButton() {
        withdrawGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (balance < 100) {
                    balanceWarning.setVisibility(View.VISIBLE);
                } else {
                    switch (checkedId) {
                        case R.id.rechargeOption: {
                            paymentMethod = "Recharge";
                            otherWarning.setVisibility(View.GONE);
                            balanceWarning.setVisibility(View.GONE);
                            break;
                        }
                        case R.id.bkashOption: {
                            paymentMethod = "Bkash";
                            if (balance < 1000) {
                                otherWarning.setVisibility(View.VISIBLE);
                                balanceWarning.setVisibility(View.GONE);
                            }
                            break;
                        }
                        case R.id.rocketOption: {
                            paymentMethod = "Rocket";
                            if (balance < 1000) {
                                otherWarning.setVisibility(View.VISIBLE);
                                balanceWarning.setVisibility(View.GONE);
                            }
                            break;
                        }
                    }
                }
            }
        });
    }

    private void updateUI() {
        walletBalanceText.setText("Current Point " + balance);
        paymentMethod = "Recharge";
        withdrawGroup.check(R.id.rechargeOption);
        mobileText.setText("");
        amountText.setText("");

        if (balance >= 100) {
            withdrawButton.setBackgroundResource(R.drawable.withdraw_green);
            activateWithdrawButton();
        } else {
            withdrawButton.setBackgroundResource(R.drawable.withdraw_ash);
            balanceWarning.setVisibility(View.VISIBLE);
            deactivateWithdrawButton();
        }
        dialog.dismiss();
    }

    private void activateWithdrawButton() {
        withdrawButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (amountText.length() < 1) {
                    showAlertDialogue("Error!", "Enter Any Valid Amount", Redeem.this);
                    return;
                }

                withdrawAmount = Integer.parseInt(amountText.getText().toString().trim());
                paymentMobileNumber = fixMobileNumber(mobileText.getText().toString().trim());

                Boolean isInternetAvailable = checkInternet(Redeem.this);
                if (!isInternetAvailable) {
                    showAlertDialogue("Error!", "Enable Internet to Continue", Redeem.this);
                    return;
                }

                if (amountText.length() == 0) {
                    showAlertDialogue("Warning!", "Please enter a valid amount", Redeem.this);

                } else if (paymentMobileNumber.length() != 14) {
                    showAlertDialogue("Warning!", "Please enter a valid mobile number", Redeem.this);

                } else if (withdrawAmount > balance) {
                    showAlertDialogue("Warning!", "You don't have enough points", Redeem.this);

                } else if (balance < 1000 && !paymentMethod.equals("Recharge")) {
                    showAlertDialogue("Warning!", "Insufficient points for Bkash or Rocket withdrawal", Redeem.this);

                } else if (withdrawAmount > 1000 || withdrawAmount < 100) {
                    showAlertDialogue("Warning!", "Enter Value within 100 to 1000", Redeem.this);

                } else {
                    dialog.setMessage("Please Wait ...");
                    dialog.setCancelable(false);
                    dialog.show();

                    redeemPointFromUser(phone,String.valueOf(paymentMobileNumber),String.valueOf(withdrawAmount));
                    //new getTimeThenUpdate().execute((Void[]) null);
                }
            }
        });
    }

    private void redeemPointFromUser(final String phone, final String payment_number, final String amount) {
        AndroidNetworking.post(Constants.getROOT_URL()+"redeemPointFromUser.php")
                .addBodyParameter("phone", phone)
                .addBodyParameter("payment_number", payment_number)
                .addBodyParameter("payment_type", paymentMethod)
                .addBodyParameter("amount", amount)
                .setTag("test")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        finish();
                        startActivity(new Intent(Redeem.this,RedeemHistory.class));
                        Log.d("ERRO",phone+payment_number+paymentMethod+amount);
                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                    }
                });
    }

    private void deactivateWithdrawButton() {
        withdrawButton.setOnClickListener(null);
    }

    /*

    private void requestWithdrawUpdateFirebase() {

    }

    private void updateSharedPreference() {

    }

    private void updateBalanceFirebase() {

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
                if (dialog.isShowing())
                    dialog.dismiss();
                showAlertDialogue("Time Check Failed!", "Please try again later!", Redeem.this);
                return;
            }

            Date lastTime = new Date(500);
            String lastWithdraw = preferences.getString(sharedLastWithdraw, "");
            try {
                lastTime = getLastTimeToday(lastWithdraw);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (internetDate.compareTo(lastTime) > 0) {
                requestWithdrawUpdateFirebase();
            } else {
                if (dialog.isShowing()) dialog.dismiss();
                showAlertDialogue("Wait!", "Maximum One Request per day!", Redeem.this);
            }
        }
    }



    */

    private void showBannerAds() {

        bannerTop = findViewById(R.id.adViewTop);
        bannerBottom = findViewById(R.id.adViewBottom);

        AdRequest adRequest1 = new AdRequest.Builder().build();
        AdRequest adRequest2 = new AdRequest.Builder().build();

        bannerTop.loadAd(adRequest1);
        bannerBottom.loadAd(adRequest2);
    }



    public void loadBalance(String phone) {
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
        dialog.show();

        AndroidNetworking.post(Constants.getROOT_URL()+"getUserDetails.php")
                .addBodyParameter("phone", phone)
                .setTag("test")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            balance= Integer.parseInt(response.getString("point"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        updateUI();

                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                    }
                });
    }
}
