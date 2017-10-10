package com.example.c.testwebapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Search extends Activity
{
    AlertWindow alert;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);

        if(alert == null)
            alert = new AlertWindow(this);

        users = (RadioButton) findViewById(R.id.searchRBTNusers);
        services = (RadioButton) findViewById(R.id.searchRBTNServices);
        offers = (RadioButton) findViewById(R.id.searchRBTNOffers);

        query = (EditText) findViewById(R.id.searchTxtQuery);

        searchPrice = (EditText) findViewById(R.id.searchPrice);
        Button sesrchFilterOffers = (Button) findViewById(R.id.sesrchFilterOffers);
        sesrchFilterOffers.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    fillOffersByPrice(Integer.parseInt(searchPrice.getText().toString()));
                }
                catch (Exception ex)
                {
                    fillOffersByPrice(0);
                }
            }
        });

        searchDistance = (EditText) findViewById(R.id.searchDistance);
        Button sesrchFilterServices = (Button) findViewById(R.id.sesrchFilterServices);
        sesrchFilterServices.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    fillServicesByDistance(Integer.parseInt(searchDistance.getText().toString()));
                } catch (Exception ex)
                {
                    fillServicesByDistance(-1);
                }
            }
        });

        users.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (!users.isChecked())
                    return;

                searchFilterLayout1.setVisibility(View.GONE);
                searchFilterLayout2.setVisibility(View.GONE);
            }
        });

        services.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (!services.isChecked())
                    return;

                searchFilterLayout1.setVisibility(View.VISIBLE);
                searchFilterLayout2.setVisibility(View.GONE);
            }
        });
        offers.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (!offers.isChecked())
                    return;

                searchFilterLayout1.setVisibility(View.GONE);
                searchFilterLayout2.setVisibility(View.VISIBLE);
            }
        });


        searchFilterLayout1 = (LinearLayout) findViewById(R.id.searchFilterLayout1);
        searchFilterLayout1.setVisibility(View.GONE);
        searchFilterLayout2 = (LinearLayout) findViewById(R.id.searchFilterLayout2);
        searchFilterLayout2.setVisibility(View.GONE);

        searchSpinnerServiceTypes = (Spinner) findViewById(R.id.searchSpinnerServiceTypes);
        searchSpinnerServiceTypes.setAdapter(new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item_layout, R.id.spinner_item_layout_textView, MainActivity.types));

        searchSpinnerServiceTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                fillServicesByTypes();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        searchListView = (ListView) findViewById(R.id.searchListView);
        searchListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
            {
                if (lastType == 1)
                    alert.AlertFriendHandler(LastList1.get(position), false);
                else if (lastType == 2)
                    alert.AlertServicesHandler(LastList2.get(position), false);
                else if (lastType == 3)
                    alert.AlertOffersHandler(LastList3.get(position), false);
                else if (lastType == 4)
                    alert.AlertServicesHandler(filteredServices.get(position), false);
                else if (lastType == 5)
                    alert.AlertOffersHandler(filteredOffers.get(position), false);
                return true;
            }
        });
        searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                if (lastType == 1)
                    alert.AlertFriendHandler(LastList1.get(position), true);
                else if (lastType == 2)
                    alert.AlertServicesHandler(LastList2.get(position), true);
                else if (lastType == 3)
                    alert.AlertOffersHandler(LastList3.get(position), true);
                else if (lastType == 4)
                    alert.AlertServicesHandler(filteredServices.get(position), true);
                else if (lastType == 5)
                    alert.AlertOffersHandler(filteredOffers.get(position), true);
            }
        });


        Button sesrchShowMapsServices = (Button)findViewById(R.id.sesrchShowMapsServices);
        sesrchShowMapsServices.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (LastList2 != null)
                    MapsActivity.PassedDatas = LastList2.toArray(new Data.Service[LastList2.size()]);
                startActivity(new Intent(Search.this, MapsActivity.class));
            }
        });
    }

    List<Data.Friend> LastList1;    // 1
    List<Data.Service> LastList2;   // 2
    List<Data.Offer> LastList3;     // 3

    List<Data.Service> filteredServices; // 4
    List<Data.Offer> filteredOffers;     // 5

    int lastType = 0;

    RadioButton users, services, offers;
    EditText query, searchPrice,searchDistance;
    ListView searchListView;
    Spinner searchSpinnerServiceTypes;
    LinearLayout searchFilterLayout1, searchFilterLayout2;

    public void onClickDo(View v)
    {
        if (users.isChecked())
            MainActivity._ServerCaller.SearchUsers(query.getText().toString(), new OnOprationDoing()
            {
                @Override
                public void OnSuccess(String result)
                {
                    try
                    {
                        LastList1 = ExtracterDataFromJson.getFriends(result);
                        searchListView.setAdapter(new FriendsArrayAdapter(getApplicationContext(), LastList1));
                        lastType = 1; // order very important here

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

        else if (services.isChecked())
            MainActivity._ServerCaller.SearchServices(query.getText().toString(), new OnOprationDoing()
            {
                @Override
                public void OnSuccess(String result)
                {
                    try
                    {
                        LastList2 = ExtracterDataFromJson.getServices(result);
                        fillServicesByTypes();
                        //searchListView.setAdapter(new ServicesArrayAdapter(getApplicationContext(), LastList2));
                        lastType = 2; // order very important here
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
        else
            MainActivity._ServerCaller.SearchOffers(query.getText().toString(), new OnOprationDoing()
            {
                @Override
                public void OnSuccess(String result)
                {
                    try
                    {
                        LastList3 = ExtracterDataFromJson.getOffers(result);
                        searchListView.setAdapter(new OffersArrayAdapter(getApplicationContext(), LastList3));
                        lastType = 3; // order very important here
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


    public void print(String str)
    {
        Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
    }

    private void fillServicesByTypes()
    {
        if (LastList2 == null)
            return;

        String type = MainActivity.types[searchSpinnerServiceTypes.getSelectedItemPosition()];
        if (type.equalsIgnoreCase("any"))
        {
            searchListView.setAdapter(new ServicesArrayAdapter(getApplicationContext(), LastList2));
            lastType = 2;
            return;
        }

        filteredServices = new ArrayList<>();
        for (Data.Service s : LastList2)
            if (s.type.equalsIgnoreCase(type))
                filteredServices.add(s);
        print(filteredServices.size() + " found");
        searchListView.setAdapter(new ServicesArrayAdapter(getApplicationContext(), filteredServices));
        lastType = 4;
    }

    private void fillServicesByDistance(int distance)
    {
        if (LastList2 == null)
            return;

        if (distance == -1)
        {
            searchListView.setAdapter(new ServicesArrayAdapter(getApplicationContext(), LastList2));
            lastType = 2;
            return;
        }

        filteredServices = new ArrayList<>();
        for (Data.Service srvc : LastList2)
            if (ServerSDK.GetDistanceFrom(srvc) <= distance)
                filteredServices.add(srvc);
        print(filteredServices.size() + " found");
        searchListView.setAdapter(new ServicesArrayAdapter(getApplicationContext(), filteredServices));
        lastType = 4;
    }

    private void fillOffersByPrice(int price)
    {
        if (LastList3 == null)
            return;

        if (price == 0)
        {
            searchListView.setAdapter(new OffersArrayAdapter(getApplicationContext(), LastList3));
            lastType = 3;
            return;
        }

        filteredOffers = new ArrayList<>();
        for (Data.Offer ofr : LastList3)
            if (ofr.mincost < price && price < ofr.maxcost)
                filteredOffers.add(ofr);
        print(filteredOffers.size() + " found");
        searchListView.setAdapter(new OffersArrayAdapter(getApplicationContext(), filteredOffers));
        lastType = 5;
    }
}