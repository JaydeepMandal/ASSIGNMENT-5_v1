package com.example.ee.assignment_5;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    ImageButton callBtn;
    Spinner spinner;
    AutoCompleteTextView phoneText1;

    String countryCode="";
    boolean permissionGranted=false;
    static int CALL_REQUEST = 2;

    ArrayList<String> phoneNumber1 = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle(R.string.app_title);

        phoneText1 = (AutoCompleteTextView) findViewById(R.id.PhoneAutoCompleteTextView);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            phoneText1.setHint(R.string.hint);
        }
        else{
            phoneText1.setHint(R.string.hint_lollypop);
        }


        spinner = (Spinner) findViewById(R.id.countryCodeSpinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(),
                R.array.code,R.layout.phone_no_drop_down);
        adapter.setDropDownViewResource(R.layout.phone_no_drop_down);

        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);


        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                permissionGranted=true;
                directCall();
            }
            else {
                if (shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Important Permission:")
                            .setCancelable(false)
                            .setMessage("Required for direct call")
                            .setPositiveButton("CONTINUE", new DialogInterface.OnClickListener() {
                                @TargetApi(Build.VERSION_CODES.M)
                                public void onClick(DialogInterface dialog, int which) {
                                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE},
                                            CALL_REQUEST);
                                }
                            })
                            .setNegativeButton("NOT NOW", null)
                            .create()
                            .show();

                } else {
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE},
                            CALL_REQUEST);
                }

            }
        }
        else{
            directCall();
        }

        if(!permissionGranted){
            directCall();
        }

    }

    public void phoneAdapter(){

        ArrayAdapter<String> phoneNoAdapter = new ArrayAdapter<>(getApplicationContext(),
                R.layout.phone_no_drop_down,phoneNumber1);
       // phoneNoAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        phoneText1.setThreshold(1);
        phoneText1.setAdapter(phoneNoAdapter);
        phoneText1.setTextColor(Color.BLACK);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==CALL_REQUEST){
            if (grantResults[0]==PackageManager.PERMISSION_GRANTED){
                permissionGranted=true;
                directCall();
            }
        }
        }

    public void directCall(){

        buttonDirectCall();
        textDirectCall();

    }

    public void textDirectCall(){

        phoneText1 = (AutoCompleteTextView) findViewById(R.id.PhoneAutoCompleteTextView);

        phoneText1.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_GO){

                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){

                        if(!permissionGranted){

                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setCancelable(false)
                                    .setTitle("Permission Not Granted:")
                                    .setMessage("required for direct call")
                                    .setPositiveButton("CONTINUE", new DialogInterface.OnClickListener() {
                                        @TargetApi(Build.VERSION_CODES.M)
                                        public void onClick(DialogInterface dialog, int which) {
                                            requestPermissions(new String[]{Manifest.permission.CALL_PHONE},
                                                    CALL_REQUEST);

                                        }
                                    })
                                    .setNegativeButton("NOT NOW",null)
                                    .create()
                                    .show();

                        }
                        if (permissionGranted){
                            call();
                        }
                    }
                    else {
                        call();
                    }
                }
                return false;
            }
        });

    }

    public void buttonDirectCall(){

        callBtn = (ImageButton) findViewById(R.id.CallimageButton);

        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){

                    if(!permissionGranted){

                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setCancelable(false)
                                .setTitle("Permission Not Granted:")
                                .setMessage("required for direct call")
                                .setPositiveButton("CONTINUE", new DialogInterface.OnClickListener() {
                                    @TargetApi(Build.VERSION_CODES.M)
                                    public void onClick(DialogInterface dialog, int which) {
                                        requestPermissions(new String[]{Manifest.permission.CALL_PHONE},
                                                CALL_REQUEST);

                                    }
                                })
                                .setNegativeButton("NOT NOW",null)
                                .create()
                                .show();

                    }
                    if (permissionGranted){
                        call();
                    }

                }
                else{
                    call();
                }
            }
        });

    }

    public void call(){

        StringBuilder phoneNo =new StringBuilder();
        if(phoneText1.length()!=10){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false)
                    .setTitle("Wrong Input:")
                    .setMessage("Phone no. includes only 10 digits without any special character")
                    .setPositiveButton("OK",null)
                    .create()
                    .show();
        }
        else{
            phoneNo.append(countryCode);
            phoneNo.append(phoneText1.getText().toString());
            if(!phoneNumber1.contains(phoneText1.getText().toString())){
                phoneNumber1.add(phoneText1.getText().toString());
            }
            phoneText1.setText(null);
            phoneAdapter();
            Uri uri = Uri.parse("tel:"+phoneNo);

            Intent intent = new Intent(Intent.ACTION_CALL,uri);
            if(intent.resolveActivity(getPackageManager())!=null){
                startActivity(intent);
            }
        }


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        countryCode = parent.getItemAtPosition(position).toString();
        ((TextView) view).setTextColor(Color.BLACK);
        ((TextView) view).setTextSize(20);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
