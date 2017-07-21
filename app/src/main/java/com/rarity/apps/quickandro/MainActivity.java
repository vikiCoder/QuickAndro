package com.rarity.apps.quickandro;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.rarity.apps.quickandro.Modules.Call;
import com.rarity.apps.quickandro.Modules.SpeakText;
import com.rarity.apps.quickandro.Modules.Temp_SpeechToText;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements RunBot, NavigationView.OnNavigationItemSelectedListener {

    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    private PrefManager prefManager;
    private ArrayList<String> conversation = new ArrayList<String>();
    private boolean isAppReady= false;
    protected ImageButton btn;
    protected EditText command;
    private RecyclerView r_view;

    private Temp_SpeechToText stt;
    public SpeakText tts;
    private Adapter adp;
    private RunCommands runCommands;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //getWindow().setBackgroundDrawableResource(R.drawable.bg_main);

        prefManager = new PrefManager(this);
        if (prefManager.isFirstTimeLaunch()) {
            startActivity(new Intent(MainActivity.this, WelcomeActivity.class));
            finish();
        }

        prefManager = new PrefManager(this);
        if (prefManager.isFirstTimeLaunch()) {
            startActivity(new Intent(MainActivity.this, WelcomeActivity.class));
            finish();
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

        Thread init = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    runCommands = new RunCommands(MainActivity.this);
                }catch (Exception e){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Please give the permissions...", Toast.LENGTH_SHORT).show();
                        }
                    });

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS )!= PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.READ_CONTACTS,
                                                        Manifest.permission.CALL_PHONE,
                                                        Manifest.permission.SEND_SMS}, PERMISSIONS_REQUEST_READ_CONTACTS);
                    }

                }

                if(prefManager.getMessage()!=null) {
                    String[] msg = prefManager.getMessage().split(";,;");
                    for (int i = 0; i < msg.length; i++) {
                        conversation.add(msg[i]);
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateListView(null);
                    }
                });

                isAppReady = true;
            }
        });
        init.start();

        command.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == 6) {
                    btn.callOnClick();
                    handled = true;
                }
                return handled;
            }
        });

        adp = new Adapter(conversation, this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setStackFromEnd(true);
        r_view.setLayoutManager(mLayoutManager);
        r_view.setItemAnimator(new DefaultItemAnimator());
        r_view.setAdapter(adp);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isAppReady){
                    Toast.makeText(MainActivity.this, "Please wait for the app to be ready...", Toast.LENGTH_LONG).show();
                    return;
                }

                if(command.getText().toString().equals("")) {
                    run();
                } else{
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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        tts.stopSpeaking();
        tts.shutdown();
    }

    @Override
    protected void onStop() {
        super.onStop();
        StringBuilder sb=new StringBuilder();
        for(int i=Math.max(0, conversation.size()-100);i<conversation.size();i++){
            sb.append(conversation.get(i)).append(";,;");
        }
        prefManager.setMessage(sb.toString());
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
        int id = item.getItemId();
        Intent intent = new Intent();

        switch(id){
            case R.id.share:intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_link));
                intent.putExtra(Intent.EXTRA_SUBJECT, "QuickAndro app");
                startActivity(Intent.createChooser(intent, null));
                break;
            case R.id.about:intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
                break;
            case R.id.rate:intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=com.rarity.apps.quickandro"));
                startActivity(intent);
                break;
            case R.id.exit:
                System.exit(0);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        int[] mainIds = new int[]{R.id.nav_call, R.id.nav_calculate, R.id.nav_open, R.id.nav_message, R.id.nav_profile, R.id.nav_search, R.id.nav_turn, R.id.nav_alarm};
        String item_descriptions[] = getResources().getStringArray(R.array.drawer_description);
        final String item_examples[] = getResources().getStringArray(R.array.drawer_examples);

        switch (id){
            case R.id.share:
            case R.id.about:
            case R.id.rate:
            case R.id.exit:
                onOptionsItemSelected(item);
                break;

            default:
                int i;
                for(i=0; i<mainIds.length; i++)
                    if(id == mainIds[i])
                        break;
                final int index = i;

                Dialog dialog = new Dialog(MainActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.help_dialog, null, false);
                TextView title = (TextView) dialogView.findViewById(R.id.main_title);
                TextView description = (TextView) dialogView.findViewById(R.id.title);
                TextView example = (TextView) dialogView.findViewById(R.id.dialog_example);
                ImageButton btn_speak = (ImageButton) dialogView.findViewById(R.id.dialog_btn_speak);

                title.setText(item.getTitle());
                description.setText(item_descriptions[index]);
                example.setText("eg: " + item_examples[index]);
                btn_speak.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tts.speak(item_examples[index]);
                    }
                });


                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(dialogView);
                dialog.setCancelable(true);
                dialog.show();
        }

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
                Toast.makeText(this, R.string.no_permission, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    public String updateListView(String message){
        if(message != null)
            conversation.add(message);

        adp.notifyDataSetChanged();
        r_view.smoothScrollToPosition(Math.max(0, r_view.getAdapter().getItemCount()-1));

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

    public void showSuggestDialog(final ArrayList<String> names, final ArrayList<String> numbers){
        Dialog dialog = new Dialog(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.suggest_dialog, null, false);
        ListView listView = (ListView) dialogView.findViewById(R.id.suggestionList);
        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.soggest_dialog_row, names);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Call call = new Call(getApplicationContext());
                call.call(numbers.get(i));
            }
        });

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialogView);
        dialog.setCancelable(true);
        dialog.show();
    }
}
