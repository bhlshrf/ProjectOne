package com.example.c.testwebapi;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import org.json.JSONException;


public class Login extends Activity
{
    TextView lblConnecting;
    EditText txtusername,txtpassword;
    Button btnLogin,btnRegister;

    boolean loginSuccess=false;

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        if (!loginSuccess)
        {
            System.exit(0);
        }
    }


    @Override
    protected void onStop(){
        super.onStop();

        SharedPreferences settings = getSharedPreferences("X", 0);
        SharedPreferences.Editor editor = settings.edit();

        editor.putString("serverAddress", ServerSDK.serverAdrress);

        editor.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        ScrollView sc = ((ScrollView) findViewById(R.id.loginScrollView));
        sc.setBackgroundColor(Color.rgb(10, 100, 150));
        sc.requestFocus();

        txtusername = (EditText) findViewById(R.id.txtusername);
        txtpassword = (EditText) findViewById(R.id.txtpassword);

        lblConnecting = (TextView) findViewById(R.id.lblConnecting);

        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnRegister = (Button) findViewById(R.id.btnRegister);


        btnLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (txtusername.getText().toString().isEmpty())
                {
                    YoYo.with(Techniques.Tada).playOn(findViewById(R.id.txtusername));
                    lblConnecting.setText("username can't be empty");
                    return;
                }
                if (txtpassword.getText().toString().isEmpty())
                {
                    YoYo.with(Techniques.Tada).playOn(findViewById(R.id.txtpassword));
                    lblConnecting.setText("password can't be empty");
                    return;
                }
                btnLogin.setEnabled(false);
                btnRegister.setEnabled(false);
                txtusername.setEnabled(false);
                txtpassword.setEnabled(false);
                lblConnecting.setText("Connecting . . .");

                MainActivity._ServerCaller.login(txtusername.getText().toString(), txtpassword.getText().toString(),
                        new OnOprationDoing()
                        {
                            @Override
                            public void OnSuccess(String result)
                            {
                                try
                                {
                                    ServerSDK._User = ExtracterDataFromJson.getUser(result);
                                } catch (JSONException e)
                                {
                                    OnFail(0, "Error in read json result\r\n" + result);
                                    return;
                                }

                                loginSuccess = true;
                                setResult(RESULT_OK, new Intent().putExtra("loginSuccess", loginSuccess));
                                finish();
                            }

                            @Override
                            public void OnFail(final int code, final String result)
                            {
                                runOnUiThread(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        lblConnecting.setText(result);
                                        btnLogin.setEnabled(true);
                                        btnRegister.setEnabled(true);
                                        txtusername.setEnabled(true);
                                        txtpassword.setEnabled(true);
                                    }
                                });
                            }
                        }
                );
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(Login.this, Register.class));
            }
        });


         YoYo.with(Techniques.ZoomInDown).playOn(findViewById(R.id.loginAppTitle));

        //YoYo.with(Techniques.values()[new Random().nextInt(Techniques.values().length)]).playOn(findViewById(R.id.loginAppTitle));
    }

    public void print(String str)
    {
        Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.action_setting)
        {
            startActivity(new Intent(Login.this, Settings.class));
        }

        return super.onOptionsItemSelected(item);
    }

}
