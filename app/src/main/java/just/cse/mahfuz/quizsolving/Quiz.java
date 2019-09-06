package just.cse.mahfuz.quizsolving;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static just.cse.mahfuz.quizsolving.StaticNames.sharedTableName;
import static just.cse.mahfuz.quizsolving.StaticNames.sharedTimesPlayed;


public class Quiz extends AppCompatActivity {

    TextView questionText, questionNumberText, quizPlayedText;
    TextView category;
    RadioGroup answerGroup;
    RadioButton answer1, answer2, answer3, answer4;
    Button next;
    boolean change = false;

    ModelQuiz modelQuiz;
    List<ModelQuiz> quizzes;

    CountDownTimer timer;
    int status = 0, questionNumber = 0, correctAnswer = 0;
    String point;
    private String solution, userAnswer;

    AdView bannerTop, bannerBottom;
    InterstitialAd interstitialAd;

    SharedPreferences preferences;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        getSupportActionBar().setTitle("Quiz");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        questionText = findViewById(R.id.questionText);
        answerGroup = findViewById(R.id.answerGroup);
        answer1 = findViewById(R.id.answer1);
        answer2 = findViewById(R.id.answer2);
        answer3 = findViewById(R.id.answer3);
        answer4 = findViewById(R.id.answer4);
        next = findViewById(R.id.nextBtn);
        questionNumberText = findViewById(R.id.questionNumber);

        category=findViewById(R.id.category);

        progressDialog = new ProgressDialog(Quiz.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading Question...");
        progressDialog.show();

        preferences = Quiz.this.getSharedPreferences(sharedTableName, MODE_PRIVATE);


        quizzes = new ArrayList<>();

        MobileAds.initialize(this, Constants.getAppId());


        answerGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.answer1: {
                        userAnswer = answer1.getText().toString();
                        break;
                    }
                    case R.id.answer2: {
                        userAnswer = answer2.getText().toString();
                        break;
                    }
                    case R.id.answer3: {
                        userAnswer = answer3.getText().toString();
                        break;
                    }
                    case R.id.answer4: {
                        userAnswer = answer4.getText().toString();
                        break;
                    }
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                if (!checkInternet(Quiz.this)) {
//                    showNextQuestion();
//
//                } else {
//                    if (interstitialAd.isLoaded()) {
//                        interstitialAd.show();
//                        showNextQuestion();
//
//                    } else {
//                        loadInterstitialAd(true);
//                        //interstitialAd.show();
//                        //showNextQuestion();
//
//                    }
//                }

                showNextQuestion();
            }
        });

        loadInterstitialAd(false);
        showBannerAds();

       String id= getIntent().getExtras().getString("id");

       if (id.equals("4")) {
           LoadQuiz("general");
           category.setText("General Knowledge");
       }
       else if (id.equals("5")) {
           LoadQuiz("science");
           category.setText("Science");
       }
       else if (id.equals("6")) {
           LoadQuiz("english");
           category.setText("English");
       }
       else if (id.equals("7")) {
           LoadQuiz("author");
           category.setText("Authors");
       }
       else if (id.equals("8")) {
           LoadQuiz("math");
           category.setText("Mathematics");
       }

    }



    private void showNextQuestion() {

//        if (interstitialAd.isLoaded())
//            interstitialAd.show();

        if (userAnswer.equals(solution)) {
            ++correctAnswer;
        }

        if (next.getText().toString().equals("Next")) {
            startQuiz();

        } else if (next.getText().toString().equals("Finish")) {

//            if (checkInternet(Quiz.this)) {
//                loadInterstitialAd(true);
//                change = true;
//
//            } else {
//                startActivity(new Intent(Quiz.this, QuizResultActivity.class)
//                        .putExtra("result", correctAnswer)
//                        .putExtra("point", point)
//                );
//                finish();
//            }

            startActivity(new Intent(Quiz.this, QuizResultActivity.class)
                    .putExtra("result", correctAnswer)
                    .putExtra("point", point)
            );
            finish();

        }

    }

    private void startQuiz() {
        questionNumberText.setText("Question " + questionNumber + " / 10");

        if(quizzes!= null && quizzes.size() !=0) {

            progressDialog.dismiss();
            Random random = new Random();
            int rndInt = random.nextInt(quizzes.size());

            questionText.setText(quizzes.get(rndInt).getQuestion());
            answer1.setText(quizzes.get(rndInt).getOptionA());
            answer2.setText(quizzes.get(rndInt).getOptionB());
            answer3.setText(quizzes.get(rndInt).getOptionC());
            answer4.setText(quizzes.get(rndInt).getOptionD());
            solution = quizzes.get(rndInt).getAnswer();

            // Reset Selection
            userAnswer = answer1.getText().toString();
            answerGroup.check(R.id.answer1);

            status=0;
            startCount();
        }
        else {
            Toast.makeText(Quiz.this,"Question List is Empty",Toast.LENGTH_SHORT).show();
        }

    }

    private void LoadQuiz(String catagory) {

        AndroidNetworking.get(Constants.getROOT_URL()+catagory+".php")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        JSONObject jo;
                        try
                        {
                            for(int i=0;i<response.length();i++)
                            {
                                jo=response.getJSONObject(i);

                                String Question=jo.getString("Question");
                                String OptionA=jo.getString("OptionA");
                                String OptionB=jo.getString("OptionB");
                                String OptionC=jo.getString("OptionC");
                                String OptionD=jo.getString("OptionD");
                                String Answer=jo.getString("Answer");

                                modelQuiz=new ModelQuiz(Question,OptionA,OptionB,OptionC,OptionD,Answer);
                                quizzes.add(modelQuiz);
                            }
                            startQuiz();
                        }catch (JSONException e)
                        {
                            Toast.makeText(Quiz.this, "GOOD RESPONSE BUT JAVA CAN'T PARSE JSON IT RECEIEVED. "+e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }

                    //ERROR
                    @Override
                    public void onError(ANError anError) {
                        anError.printStackTrace();
                        Toast.makeText(Quiz.this, "UNSUCCESSFUL :  ERROR IS : "+anError.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }


    private void startCount() {
        next.setBackgroundResource(R.drawable.signup_back);
        timer = new CountDownTimer(1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
//                status += 1;
//                next.setText("Next question in ");
//                next.append("" + (5 - status));
//                next.append(" seconds");
//
//                Log.e("ERRO", "start count"+" "+status+" "+questionNumber);

                next.setText("Next");
            }

            @Override
            public void onFinish() {
                questionNumber++;

                if (questionNumber == 10) { // TODO: Change to 20
                    next.setText("Finish");
                    next.setBackgroundResource(R.drawable.loginbtn_back);

                } else {
                    next.setText("Next");
                    next.setBackgroundResource(R.drawable.loginbtn_back);
                }
            }
        }.start();
    }

    private void showBannerAds() {

        bannerTop = findViewById(R.id.adViewTop);
        bannerBottom = findViewById(R.id.adViewBottom);

        AdRequest adRequest1 = new AdRequest.Builder().build();
        AdRequest adRequest2 = new AdRequest.Builder().build();

        bannerTop.loadAd(adRequest1);
        bannerBottom.loadAd(adRequest2);
    }

    private void loadInterstitialAd(final boolean wait) {

        if (wait) {
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }

        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(Constants.getI1());
        interstitialAd.loadAd(new AdRequest.Builder().build());

        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();


                if (questionNumber == 10) { // TODO: Change value
                    point = "true";
                }
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();

                if (wait && !change) {
                    progressDialog.dismiss();
                    showNextQuestion();
                }
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();

                if (next.getText().toString().equals("Finish") && change) {
                    startActivity(new Intent(Quiz.this, QuizResultActivity.class)
                            .putExtra("point", point) // TODO: change to point
                            .putExtra("result", correctAnswer));
                    finish();
                }

                loadInterstitialAd(false);
            }
        });

    }

    public static boolean checkInternet(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Service.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            return networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED;
        }
        return false;
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
}
