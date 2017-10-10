package com.example.c.testwebapi;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * Created by 01 on 23/5/2015.
 */
public class CreateOffers extends ActionBarActivity
{
    public static Data.Service service;
    public static Data.Offer offer;

    EditText name,min,max,qunt,desc;
    CheckBox requstable;
    Button save,delete;

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        service = null;
        offer = null;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_offers);

        name = (EditText)findViewById(R.id.createoffer_name);
        min = (EditText)findViewById(R.id.createoffer_min);
        max = (EditText)findViewById(R.id.createoffer_max);
        qunt = (EditText)findViewById(R.id.createoffer_quantity);
        desc = (EditText)findViewById(R.id.createoffer_desc);

        requstable = (CheckBox)findViewById(R.id.createoffer_requestable);

        save = (Button)findViewById(R.id.createoffer_save);
        delete = (Button)findViewById(R.id.createoffer_delete);
        delete.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                MainActivity._ServerCaller.DeleteOffer(offer.id,new OnOprationDoing()
                {
                    @Override
                    public void OnSuccess(String result)
                    {
                        print("deleted");
                        finish();
                    }

                    @Override
                    public void OnFail(int code, String result)
                    {
                        print(result);
                    }
                });
            }
        });

        if(offer == null) // Create new offer
        {
            if(service == null)
            {
                print("something wrong");
                finish();
            }

            delete.setVisibility(View.GONE);
            save.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    try
                    {
                        if(Integer.parseInt(min.getText().toString()) > Integer.parseInt(max.getText().toString()))
                        {
                            print("min most be smaller than max");
                            return;
                        }
                        MainActivity._ServerCaller.CreateOffer(
                                service.id,
                                name.getText().toString(),
                                Integer.parseInt(min.getText().toString()),
                                Integer.parseInt(max.getText().toString()),
                                Integer.parseInt(qunt.getText().toString()),
                                requstable.isChecked(),
                                desc.getText().toString(),
                                new OnOprationDoing()
                                {
                                    @Override
                                    public void OnSuccess(String result)
                                    {
                                        print("created");
                                        finish();
                                    }

                                    @Override
                                    public void OnFail(int code, String result)
                                    {
                                        print(result);
                                    }
                                });
                    }
                    catch (Exception ex)
                    {
                        print("incorrect data");
                    }
                }
            });
        }
        else             // edit exist offer
        {
            delete.setVisibility(View.VISIBLE);

            name.setText(offer.name);
            min.setText(offer.mincost + "");
            max.setText(offer.maxcost+"");
            qunt.setText(offer.quantity+"");
            requstable.setChecked(offer.requestable);
            desc.setText(offer.desc);


            save.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    try
                    {
                        if(Integer.parseInt(min.getText().toString()) > Integer.parseInt(max.getText().toString()))
                        {
                            print("min most be smaller than max");
                            return;
                        }
                    MainActivity._ServerCaller.EditOffer(
                            offer.id,
                            name.getText().toString(),
                            Integer.parseInt(min.getText().toString()),
                            Integer.parseInt(max.getText().toString()),
                            Integer.parseInt(qunt.getText().toString()),
                            requstable.isChecked(),
                            desc.getText().toString(),
                            new OnOprationDoing()
                            {
                                @Override
                                public void OnSuccess(String result)
                                {
                                    print("done");
                                    finish();
                                }

                                @Override
                                public void OnFail(int code, String result)
                                {
                                    print(result);
                                }
                            });
                    }
                    catch (Exception ex)
                    {
                        print("incorrect data");
                    }
                }
            });
        }
    }

    public void print(String str)
    {
        Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
    }
}
