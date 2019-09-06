package just.cse.mahfuz.quizsolving;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    Button b4,b5,b6,b7,b8;

    ProgressDialog dialog;
    NavigationView navigationView;
    TextView name,phone;


    @Override
    protected void onStart() {
        super.onStart();
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setTitle("QuizSolving");

        b4=findViewById(R.id.b4);
        b5=findViewById(R.id.b5);
        b6=findViewById(R.id.b6);
        b7=findViewById(R.id.b7);
        b8=findViewById(R.id.b8);

        dialog= new ProgressDialog(Home.this);
//nav Drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View hView = navigationView.getHeaderView(0);

        name = hView.findViewById(R.id.name);
        phone = hView.findViewById(R.id.phone);
        setNavContents();

        dialog.setMessage("Please Wait...");
        final Intent intent=new Intent(Home.this,Quiz.class);
        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                intent.putExtra("id","4");
                startActivity(intent);
            }
        });
        b5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                intent.putExtra("id","5");
                startActivity(intent);
            }
        });
        b6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                intent.putExtra("id","6");
                startActivity(intent);
            }
        });
        b7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                intent.putExtra("id","7");
                startActivity(intent);
            }
        });
        b8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                intent.putExtra("id","8");
                startActivity(intent);
            }
        });





    }

    public void setNavContents() {

       SharedPrefManager sharedPrefManager = new SharedPrefManager(Home.this);
       name.setText(sharedPrefManager.getName());
       phone.setText(sharedPrefManager.getPhone());

    }

    /********************************************************/
//navigation panel listener
    @SuppressWarnings("StatementWithEmptyBody")

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        dialog.setMessage("Loading...");
        dialog.show();
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            dialog.dismiss();

        } else if (id == R.id.nav_profile) {
            Intent intent = new Intent(Home.this, ProfileActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_notification) {
            startActivity(new Intent(Home.this,Notification.class));


        } else if (id == R.id.nav_logout) {
            dialog.setMessage("Logging out....");
            dialog.show();
            SharedPrefManager sharedPrefManager=new SharedPrefManager(Home.this);
            sharedPrefManager.logout();
            finish();
            Intent intent = new Intent(Home.this, Login.class);
            startActivity(intent);


        }else if (id == R.id.nav_contact) {
            Intent emailIntent = new Intent();
            emailIntent.setAction(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:mahfuz.cse.just@gmail.com"));
            startActivity(emailIntent);

        } else if (id == R.id.nav_share) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "http://play.google.com/store/apps/details?id=");
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, "Share play store link via"));

        }else if (id == R.id.nav_redeem) {
            startActivity(new Intent(Home.this,Redeem.class));

        }else if (id == R.id.nav_redeem_his) {
            startActivity(new Intent(Home.this,RedeemHistory.class));

        }else if (id == R.id.nav_reff) {
            startActivity(new Intent(Home.this,Refferal.class));

        }
        else if (id == R.id.nav_privacy) {
            startActivity(new Intent(Home.this,Privacy.class));

        }

        else if (id == R.id.nav_rate) {
            String appPackageName = getPackageName();
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=")));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id="+ appPackageName)));
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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


    @Override
    protected void onResume() {

        navigationView.setCheckedItem(R.id.nav_home);
        super.onResume();
    }

}
