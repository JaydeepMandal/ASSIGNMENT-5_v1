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

    String countryCode="";      //storing user input from spinner
    boolean permissionGranted=false;    //boolean to check user permitted permission
    static int CALL_REQUEST = 2;    //request code for permission handling

    ArrayList<String> phoneNumber1 = new ArrayList<>(); //list to store number entered in editText

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle(R.string.app_title);   //gives different heading to title of the project

        phoneText1 = (AutoCompleteTextView) findViewById(R.id.PhoneAutoCompleteTextView);
        //different hint based on android version
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            phoneText1.setHint(R.string.hint);
        }
        else{
            phoneText1.setHint(R.string.hint_lollypop);
        }


        spinner = (Spinner) findViewById(R.id.countryCodeSpinner);
        //adapter for use with spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(),
                R.array.code,R.layout.phone_no_drop_down);  //creating adapter using resource from xml
        adapter.setDropDownViewResource(R.layout.phone_no_drop_down);// setting custom layout for dropdown
        //connecting adapter with spinner and connecting a listener with spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        //requesting runtime permission for android version M and above
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
            //checking whether permission granted
            if (ContextCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                permissionGranted=true;
                directCall();
            }
            else {      //permission not granted
                //second time permission request
                if (shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE)) {
                    //Dialog to explain the reason for permission
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Important Permission:")
                            .setCancelable(false)
                            .setMessage("Required for direct call")
                            .setPositiveButton("CONTINUE", new DialogInterface.OnClickListener() {
                                @TargetApi(Build.VERSION_CODES.M)
                                public void onClick(DialogInterface dialog, int which) {
                                    //requesting permission on button click
                                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE},
                                            CALL_REQUEST);
                                }
                            })
                            .setNegativeButton("NOT NOW", null)
                            .create()
                            .show();

                } else {    //first time permission request
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE},
                            CALL_REQUEST);
                }

            }
        }
        else{   //no need for permission request for version lesser than android M
            directCall();
        }

        if(!permissionGranted){     //if permission not granted during startup in android M and above
            directCall();
        }

    }

    //adapter for use with auto complete text view to show phone no. dialed
    public void phoneAdapter(){

        ArrayAdapter<String> phoneNoAdapter = new ArrayAdapter<>(getApplicationContext(),
                R.layout.phone_no_drop_down,phoneNumber1);
        //phoneNoAdapter.setDropDownViewResource(R.layout.phone_no_drop_down);
        phoneText1.setThreshold(1);     //starts searching content from 1st character
        phoneText1.setAdapter(phoneNoAdapter);
        phoneText1.setTextColor(Color.BLACK);   //text color of selected item from dropDown

    }

    //Overridden method to handle permission request in android M and above
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==CALL_REQUEST){
            if (grantResults[0]==PackageManager.PERMISSION_GRANTED){
                permissionGranted=true;
                directCall();
            }
        }
        }

    //method to combine call functionality through button click and virtual keyboard button go
    public void directCall(){

        buttonDirectCall();     //method for calling when image button clicked
        textDirectCall();       //method for calling when virtual Keyboard button go is clicked

    }

    //method to handle permission request on button click and calling dialer
    public void permissionRequestOnCall(){

        //the bellow "if" part works on only for android version M and above
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            //request for permission if permission is not granted initially during app startup
            if(!permissionGranted){
                //Dialog to tell reason for permission and request permission
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
            if (permissionGranted){     //permission is granted initially during app startup
                call();
            }
        }
        //the bellow "else" part works on for android version < android M
        else {
            call();
        }
    }

    //method to call entered phone no. when virtual keyboard button go is clicked
    public void textDirectCall(){

        phoneText1 = (AutoCompleteTextView) findViewById(R.id.PhoneAutoCompleteTextView);
        //method to listen which button is clicked in virtual keyboard
        phoneText1.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_GO){    //stuff to do when go button is clicked

                    permissionRequestOnCall();

                }
                return false;
            }
        });

    }

    //method to call entered phone no. when image button is clicked
    public void buttonDirectCall(){

        callBtn = (ImageButton) findViewById(R.id.CallimageButton);
        //method to listen which button is clicked
        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {   //stuff to do when go button is clicked

                permissionRequestOnCall();

            }
        });

    }

    //method to do the actual calling stuff by using intent
    public void call(){

        StringBuilder phoneNo =new StringBuilder();     //String to hold the complete phone number
        //Dialog to display wrong input in text field
        if(phoneText1.length()!=10){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false)
                    .setTitle("Wrong Input:")
                    .setMessage("Phone no. includes only 10 digits without any special character")
                    .setPositiveButton("OK",null)
                    .create()
                    .show();
        }
        //correct input in text field
        else{
            phoneNo.append(countryCode);    //append country code from spinner
            phoneNo.append(phoneText1.getText().toString());    //append phone no. from text field
            //checking whether entered number is already present in the ArrayList
            if(!phoneNumber1.contains(phoneText1.getText().toString())){
                //if number not present in the list adds it to the list
                phoneNumber1.add(phoneText1.getText().toString());
            }
            phoneText1.setText(null);   //setting the text field to 0th position
            phoneAdapter();//call to autoCompleteTextView adapter
            Uri uri = Uri.parse("tel:"+phoneNo);    //converting the phone number to Uri format

            Intent intent = new Intent(Intent.ACTION_CALL,uri);     //calling the caller header
            if(intent.resolveActivity(getPackageManager())!=null){
                startActivity(intent);  //starts the caller header
            }
        }


    }

    //stuff to do when item is selected in spinner
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        countryCode = parent.getItemAtPosition(position).toString();    //storing the country code selected
        ((TextView) view).setTextColor(Color.BLACK);    //setting the text color of selected item
        ((TextView) view).setTextSize(20);  //setting the text size selected item
    }

    //stuff to do when item is not selected in spinner
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
