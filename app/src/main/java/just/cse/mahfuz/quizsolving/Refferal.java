package just.cse.mahfuz.quizsolving;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class Refferal extends AppCompatActivity {

    TextView referralText;

    String referral;


    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refferal);


        getSupportActionBar().setTitle("Refferal");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        referralText = findViewById(R.id.referralText);

        SharedPrefManager sharedPrefManager = new SharedPrefManager(Refferal.this);

        referralText.setText("Referral Code: " + sharedPrefManager.getPhone());

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
