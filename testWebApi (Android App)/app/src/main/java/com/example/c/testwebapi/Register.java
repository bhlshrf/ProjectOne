package com.example.c.testwebapi;

import android.app.Activity;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;


public class Register extends Activity
{
    EditText txtusername, txtpassword, txtpassword2, txtFname,txtLname;
    CheckBox chkbxSP;
    TextView lblStatus;
    Button btnOk;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        ScrollView sc = ((ScrollView)findViewById(R.id.rgstscrollview));
        sc.setBackgroundColor(Color.rgb(0, 80, 125));
        sc.requestFocus();

        txtusername=(EditText)findViewById(R.id.txtRgstUsername);
        txtpassword=(EditText)findViewById(R.id.txtRgstPasswrod);
        txtpassword2=(EditText)findViewById(R.id.txtRgstPasswrodConfirm);
        txtFname=(EditText)findViewById(R.id.txtRgstFname);
        txtLname=(EditText)findViewById(R.id.txtRgstLname);

        chkbxSP = (CheckBox)findViewById(R.id.chkbxRgstServiceProv);

        lblStatus = (TextView)findViewById(R.id.lblRgstConnecting);

        btnOk=(Button)findViewById(R.id.btnRgstOK);

        btnOk.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(txtusername.getText().toString().isEmpty())
                {
                    lblStatus.setText("username can't be empty");
                    return;
                }
                if(txtusername.getText().toString().contains(" "))
                {
                    lblStatus.setText("username can't contains Spaces");
                    return;
                }
                if(txtpassword.getText().toString().isEmpty())
                {
                    lblStatus.setText("password can't be empty");
                    return;
                }
                if(!txtpassword.getText().toString().equals(txtpassword2.getText().toString()))
                {
                    lblStatus.setText("passwords not match");
                    return;
                }

                lblStatus.setText("Connecting . . .");
                DisableAllControls();

                MainActivity._ServerCaller.RegisterUser(
                        txtusername.getText().toString(),
                        txtpassword.getText().toString(),
                        txtFname.getText().toString(),
                        txtLname.getText().toString(),
                        chkbxSP.isChecked(),
                        new OnOprationDoing()
                        {
                            @Override
                            public void OnSuccess(String result)
                            {
                                lblStatus.setText("user registered successfully");
                                print("user registered successfully");
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
                                        lblStatus.setText(result);
                                        EnableAllControls();
                                    }
                                });
                            }
                        }
                );
            }
        });
    }

    public void DisableAllControls()
    {
        txtusername.setEnabled(false);
        txtpassword.setEnabled(false);
        txtpassword2.setEnabled(false);
        txtFname.setEnabled(false);
        txtLname.setEnabled(false);
        chkbxSP.setEnabled(false);
        btnOk.setEnabled(false);
    }

    public void EnableAllControls()
    {
        txtusername.setEnabled(true);
        txtpassword.setEnabled(true);
        txtpassword2.setEnabled(true);
        txtFname.setEnabled(true);
        txtLname.setEnabled(true);
        chkbxSP.setEnabled(true);
        btnOk.setEnabled(true);
    }

    public void print(String str)
    {
        Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
    }
}
