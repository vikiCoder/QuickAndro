package com.rarity.apps.quickandro;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.rarity.apps.quickandro.Modules.SpeakText;
import com.rarity.apps.quickandro.Modules.Temp_SpeechToText;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements RunBot, NavigationView.OnNavigationItemSelectedListener {

    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    private PrefManager prefManager;
    private ArrayList<String> conversation = new ArrayList<String>();
    private ImageButton btn;
    private EditText command;
    private RecyclerView r_view;

    private Temp_SpeechToText stt;
    public SpeakText tts;
    private Adapter adp;
    private RunCommands runCommands;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefManager = new PrefManager(this);
        if (prefManager.isFirstTimeLaunch()) {
            startActivity(new Intent(MainActivity.this, WelcomeActivity.class));
            finish();
        }

        if(prefManager.getMessage()!=null) {
            String[] msg = prefManager.getMessage().split(";,;");
            for (int i = 0; i < msg.length; i++) {
                conversation.add(msg[i]);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if(savedInstanceState != null)
            conversation = savedInstanceState.getStringArrayList("conversation");

        btn = (ImageButton) findViewById(R.id.btn_run);
        command = (EditText) findViewById(R.id.editText_command);
        r_view = (RecyclerView) findViewById(R.id.recycler_view);
        tts = new SpeakText(this);
        stt = new Temp_SpeechToText(this);

        adp = new Adapter(conversation);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setStackFromEnd(true);
        r_view.setLayoutManager(mLayoutManager);
        r_view.setItemAnimator(new DefaultItemAnimator());
        r_view.setAdapter(adp);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(command.getText().toString().equals("")) {
                    run();
                }
                else{
                    tts.stopSpeaking();
                    updateLayout(" "+command.getText().toString() );
                    runCommands.callModule(command.getText().toString() );
                    command.setText("");
                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("conversation", conversation);
    }

    @Override
    protected void onPause() {
        super.onPause();
        StringBuilder sb=new StringBuilder();
        for(int i=Math.max(0, conversation.size()-100);i<conversation.size();i++){
            sb.append(conversation.get(i)).append(";,;");
        }
        prefManager.setMessage(sb.toString());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        tts.stopSpeaking();
        tts.shutdown();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

//        if (id == R.id.nav_camera) {
//            // Handle the camera action
//        } else if (id == R.id.nav_gallery) {
//
//        } else if (id == R.id.nav_slideshow) {
//
//        } else if (id == R.id.nav_manage) {
//
//        } else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {
//
//        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // retrieves data from the VoiceRecognizer
        if (requestCode == 1010 && resultCode == RESULT_OK) {
            String result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0);
            updateLayout(" " + result);
            runCommands.callModule(result);
        }
    }








    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                runCommands = new RunCommands(this);
            } else {
                Toast.makeText(this, "Can't use the app without permission...", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    public String updateListView(String message){
        conversation.add(message);
        //adp = new Adapter(conversation);
        //r_view.setAdapter(adp);
        adp.notifyDataSetChanged();
        r_view.smoothScrollToPosition(r_view.getAdapter().getItemCount()-1);

        return message;
    }

    @Override
    public void updateLayout(String message) {
        updateListView(message);
        if(message.charAt(0)!=' ')    //this will speak the message aloud if that is written by the bot.
            tts.speak(message);
    }

    public void run(){
        tts.stopSpeaking();
        stt.listen();
    }
}
