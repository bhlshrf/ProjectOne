package com.example.c.testwebapi;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;


public class Settings extends ActionBarActivity
{

    RadioButton settingOnlineServer,settingLocalServer,settingCustomServer;
    EditText settingTxtServeraddress;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        settingOnlineServer = (RadioButton) findViewById(R.id.settingOnlineServer);
        settingLocalServer  = (RadioButton) findViewById(R.id.settingLocalServer);
        settingCustomServer = (RadioButton) findViewById(R.id.settingCustomServer);

        settingTxtServeraddress = (EditText) findViewById(R.id.settingTxtServeraddress);

        if(ServerSDK.serverAdrress.equalsIgnoreCase("http://bhlshrf2-001-site1.smarterasp.net/"))
            settingOnlineServer.setChecked(true);
        else if(ServerSDK.serverAdrress.equalsIgnoreCase("http://10.0.2.2/api/"))
            settingLocalServer.setChecked(true);
        else
        {
            settingTxtServeraddress.setText(ServerSDK.serverAdrress);
            settingTxtServeraddress.setVisibility(View.VISIBLE);
            settingCustomServer.setChecked(true);
        }


        settingOnlineServer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if(!isChecked)
                    return;
                ServerSDK.serverAdrress = "http://bhlshrf2-001-site1.smarterasp.net/";
                print(ServerSDK.serverAdrress);
            }
        });

        settingLocalServer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if(!isChecked)
                    return;
                ServerSDK.serverAdrress = "http://10.0.2.2/api/";
                print(ServerSDK.serverAdrress);
            }
        });

        settingCustomServer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if(!isChecked)
                    settingTxtServeraddress.setVisibility(View.INVISIBLE);
                else
                {
                    settingTxtServeraddress.setVisibility(View.VISIBLE);
                    extractAndValidateTextvalueToServerAddress(settingTxtServeraddress.getText().toString());
                }
            }
        });

        settingTxtServeraddress.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                extractAndValidateTextvalueToServerAddress(settingTxtServeraddress.getText().toString());
            }
        });

        settingTxtServeraddress.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                print(ServerSDK.serverAdrress);
                //print(settingTxtServeraddress.getText().toString());
                return true;
            }
        });

    }

    @Override
    public void onStop()
    {
        if(settingCustomServer.isChecked())
            if(ServerSDK.serverAdrress.length()<8)
            {
                print("Incorrect Server address,it will Change to default");
                settingOnlineServer.setChecked(true);
            }
        super.onStop();
    }

    private void extractAndValidateTextvalueToServerAddress(String v)
    {
        ServerSDK.serverAdrress = v;

        if(!ServerSDK.serverAdrress.startsWith("http://"))
            ServerSDK.serverAdrress = "http://" +ServerSDK.serverAdrress;

        if(!ServerSDK.serverAdrress.endsWith("/"))
            ServerSDK.serverAdrress += "/";
    }

    public void print(String str) {
        Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
    }
}
