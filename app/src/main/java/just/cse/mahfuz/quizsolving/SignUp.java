package just.cse.mahfuz.quizsolving;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {

    EditText name,phone,password,confirm_password,refferal_code;
    String sName,sPhone,sPassword,sConfirm_password,sRefferal_code;

    Button signUp;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        name=findViewById(R.id.name);
        phone=findViewById(R.id.phone);
        password=findViewById(R.id.password);
        confirm_password=findViewById(R.id.confirm_password);
        refferal_code=findViewById(R.id.refferal_code);

        signUp=findViewById(R.id.signUp);
        progressDialog= new ProgressDialog(SignUp.this);


        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog.setMessage("Signing Up....");
                progressDialog.show();

                sName=name.getText().toString().trim();
                sPhone=phone.getText().toString().trim();
                sPassword=password.getText().toString().trim();
                sConfirm_password=confirm_password.getText().toString().trim();
                sRefferal_code=refferal_code.getText().toString().trim();


                if (!TextUtils.isEmpty(sName) &&!TextUtils.isEmpty(sPhone) &&!TextUtils.isEmpty(sPassword) &&!TextUtils.isEmpty(sConfirm_password)) {
                    if (sPassword.equals(sConfirm_password)) {

                        if (phone.length()==11) {
                            registerUser();
                        }
                        else {
                            progressDialog.dismiss();
                            Toast.makeText(SignUp.this,"Please enter a valid phone number",Toast.LENGTH_SHORT).show();
                        }

                    }
                    else {
                        progressDialog.dismiss();
                        Toast.makeText(SignUp.this,"Password doesn't match",Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    progressDialog.dismiss();
                    Toast.makeText(SignUp.this,"Please fill up all the required field",Toast.LENGTH_SHORT).show();
                }

            }
        });


    }


    private void registerUser() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.getROOT_URL()+"registerUser.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                            if (!TextUtils.isEmpty(sRefferal_code) && sRefferal_code!=null) {
                                addPointToReffer(sRefferal_code);

                            }
                            else {
                                finish();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.hide();
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("name", sName);
                params.put("phone", sPhone);
                params.put("password", sPassword);
                return params;
            }
        };


        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);


    }

    private void addPointToReffer(String phone) {
        AndroidNetworking.post(Constants.getROOT_URL()+"addPointToReffer.php")
                .addBodyParameter("phone", phone)
                .setTag("test")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        finish();
                        Log.d("ERRO","reffer done");
                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                    }
                });
    }
}
