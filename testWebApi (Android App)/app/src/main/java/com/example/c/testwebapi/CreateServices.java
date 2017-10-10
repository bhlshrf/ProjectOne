package com.example.c.testwebapi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.List;

public class CreateServices extends ActionBarActivity
{
    Button b, b2, b3, deleteService_butn,creatServiceshowmap;

    EditText tname, tlong, tlant, tphone, twebsite, tdesc;
    Spinner ttypes;

    ListView listOffers;

    List<Data.Offer> offers;

    AlertWindow alerter;
    public static Data.Service service;


    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        service = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_services);

        if (alerter == null)
            alerter = new AlertWindow(this);

        tname = (EditText) findViewById(R.id.crtsrv_name);
        tlong = (EditText) findViewById(R.id.crtsrv_long);
        tlant = (EditText) findViewById(R.id.crtsrv_lati);
        tphone = (EditText) findViewById(R.id.crtsrv_phone);
        twebsite = (EditText) findViewById(R.id.crtsrv_website);
        tdesc = (EditText) findViewById(R.id.crtsrv_desc);
        ttypes = (Spinner) findViewById(R.id.crtsrv_types);

        tlant.setText(ServerSDK._myLatitude + "");
        tlong.setText(ServerSDK._myLongitude + "");

        ttypes.setAdapter(new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item_layout, R.id.spinner_item_layout_textView, MainActivity.types));

        listOffers = (ListView) findViewById(R.id.createservice_listoffers);
        listOffers.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                alerter.AlertOffersHandler(offers.get(position), false);
            }
        });
        listOffers.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
            {
                alerter.AlertOffersHandler(offers.get(position), true);
                return true;
            }
        });

        deleteService_butn = (Button) findViewById(R.id.deleteService_butn);
        deleteService_butn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                MainActivity._ServerCaller.ServiceAction(service.id, ServerSDK.ServiceActions.delete, new OnOprationDoing()
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

        if (service != null)
        {
            deleteService_butn.setVisibility(View.VISIBLE);
            tname.setText(service.name);
            tlong.setText(service.longitude + "");
            tlant.setText(service.laititude + "");
            tphone.setText(service.phone);
            twebsite.setText(service.website);
            tdesc.setText(service.desc);

            for (int i = 0; i < MainActivity.types.length; i++)
                if (service.type.equalsIgnoreCase(MainActivity.types[i]))
                {
                    ttypes.setSelection(i);
                    break;
                }

            fetchOffersOfthisServices();
        }


        b3 = (Button) findViewById(R.id.imgsService_create_butn);

        b2 = (Button) findViewById(R.id.offer_create_butn);
        b2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                CreateOffers.service = service;
                startActivity(new Intent(CreateServices.this, CreateOffers.class));
            }
        });


        b = (Button) findViewById(R.id.save_butn);
        b.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    b.setEnabled(false);
                    OnOprationDoing d = new OnOprationDoing()
                    {
                        @Override
                        public void OnSuccess(String result)
                        {
                            print("success");
                            finish();
                        }

                        @Override
                        public void OnFail(int code, String result)
                        {
                            print(result);
                        }

                        @Override
                        public void afterFinish()
                        {
                            runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    b.setEnabled(true);
                                }
                            });
                        }
                    };

                    if (service != null)
                        MainActivity._ServerCaller.EditService(
                                service.id,
                                tname.getText().toString(),
                                Double.parseDouble(tlong.getText().toString()),
                                Double.parseDouble(tlant.getText().toString()),
                                tphone.getText().toString(),
                                tdesc.getText().toString(),
                                twebsite.getText().toString(),
                                ttypes.getSelectedItem().toString(), d);
                    else
                        MainActivity._ServerCaller.CreateService(
                                tname.getText().toString(),
                                Double.parseDouble(tlong.getText().toString()),
                                Double.parseDouble(tlant.getText().toString()),
                                tphone.getText().toString(),
                                tdesc.getText().toString(),
                                twebsite.getText().toString(),
                                ttypes.getSelectedItem().toString(), d);
                } catch (Exception e)
                {
                    print(e.getMessage());
                }
            }
        });

        creatServiceshowmap = (Button) findViewById(R.id.creatServiceshowmap);
        creatServiceshowmap.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                MapsActivity.isForCreateService = true;
                startActivityForResult(new Intent(CreateServices.this, MapsActivity.class), 1);
            }
        });

        setVisibility();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1)
            if(resultCode == RESULT_OK)
            {
                double lat = data.getDoubleExtra("lat", ServerSDK._myLatitude);
                double lng = data.getDoubleExtra("lng", ServerSDK._myLongitude);

                tlant.setText(lat + "");
                tlong.setText(lng + "");

                print("new coordinates selected");
            }
    }


    /**
     * * Method for Setting the Height of the ListView dynamically.
     * *** Hack to fix the issue of not showing all the items of the ListView
     * *** when placed inside a ScrollView  ***
     */
    public static void setListViewHeightBasedOnChildren(ListView listView)
    {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++)
        {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();


    }

    public void print(String str)
    {
        Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
    }


    private void fetchOffersOfthisServices()
    {
        if (service == null)
            return;

        MainActivity._ServerCaller.GetOffersOfServices(service.id, new OnOprationDoing()
        {
            @Override
            public void OnSuccess(String result)
            {
                try
                {
                    offers = ExtracterDataFromJson.getOffers(result);
                    listOffers.setAdapter(new OffersArrayAdapter(getApplicationContext(), offers));
                    setListViewHeightBasedOnChildren(listOffers);
                } catch (Exception ex)
                {
                    OnFail(0, ex.getMessage());
                }
            }

            @Override
            public void OnFail(int code, String result)
            {
                print(result);
            }
        });
    }

    private void setVisibility()
    {
        if (service == null)
        {
            b2.setVisibility(View.GONE);
            b3.setVisibility(View.GONE);
            listOffers.setVisibility(View.GONE);
            deleteService_butn.setVisibility(View.GONE);
        } else
        {
            b2.setVisibility(View.VISIBLE);
            b3.setVisibility(View.VISIBLE);
            listOffers.setVisibility(View.VISIBLE);
            deleteService_butn.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_refresh, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.action_refresh)
        {
            fetchOffersOfthisServices();
            setVisibility();
        }
        return super.onOptionsItemSelected(item);
    }

}