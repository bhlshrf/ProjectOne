package com.example.c.testwebapi;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends ActionBarActivity
{

    public static String[] types = new String[]{"any", "Public","Hotels","Restaurants","Sport","Education","Medical","Security","IT", "Other"};
    public static ServerSDK _ServerCaller;

    Button myServices,newService,myFriends,favoriteServices,favoriteOffers ,requestedOffers,
           myRequestsIMade,myRequestsAsOwner;
    AlertWindow alert;

    TextView statuslabel;

    ListView main_servicetableLayout;
    List<Data.Friend> freinds;
    List<Data.Service> services;
    List<Data.Offer> offers;

    List<Data.Request> requests;

    int lasttype = 0;


    public void RestoreData(){
        SharedPreferences settings = getSharedPreferences("X", 0);
        ServerSDK._User = new Data.User(
                settings.getString("at", "X"),
                settings.getString("username", ""),
                settings.getString("fname", ""),
                settings.getString("lname", ""),
                settings.getBoolean("type", true));

        if (ServerSDK._User.accesstoken.equals("X"))
            ServerSDK._User = null;


        ServerSDK.serverAdrress =  settings.getString("serverAddress", ServerSDK.serverAdrress);
    }

    @Override
    protected void onStop(){
        super.onStop();

        if(ServerSDK._User == null)
        {
            clearStoredData();
            return;
        }

        SharedPreferences settings = getSharedPreferences("X", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("at", ServerSDK._User.accesstoken);
        editor.putString("username", ServerSDK._User.username);
        editor.putString("fname", ServerSDK._User.fname);
        editor.putString("lname", ServerSDK._User.lname);
        editor.putBoolean("type", ServerSDK._User.typeisSP);

        editor.putString("serverAddress", ServerSDK.serverAdrress);

        editor.commit();
    }

    public void clearStoredData(){
        SharedPreferences settings = getSharedPreferences("X", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.remove("at");
        editor.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ServerCaller.context = getBaseContext();

        statuslabel = (TextView)findViewById(R.id.statuslabel);


        if (_ServerCaller == null)
            _ServerCaller = new ServerSDK(this);

        RestoreData();

        if (ServerSDK._User == null)
            startActivityForResult(new Intent(MainActivity.this, Login.class), 1);
        else
            print("welcome " + ServerSDK._User.username);

        if (alert == null)
            alert = new AlertWindow(this);


        main_servicetableLayout = (ListView) findViewById(R.id.main_servicetableLayout);

        newService = (Button) findViewById(R.id.newService);
        myServices = (Button) findViewById(R.id.myServices);
        myFriends = (Button) findViewById(R.id.myFriends);


        favoriteServices = (Button) findViewById(R.id.favoriteServices);
        favoriteOffers = (Button) findViewById(R.id.favoriteOffers);
        requestedOffers = (Button) findViewById(R.id.requestedOffers);
        myRequestsIMade = (Button) findViewById(R.id.myRequestsIMade);
        myRequestsAsOwner = (Button) findViewById(R.id.myRequestsAsOwner);

        favoriteServices.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                print("loading . . .");
                _ServerCaller.GetMyFavoriteServices(new OnOprationDoing()
                {
                    @Override
                    public void OnSuccess(String result)
                    {
                        try
                        {
                            services = ExtracterDataFromJson.getServices(result);
                            main_servicetableLayout.setAdapter(new ServicesArrayAdapter(getApplicationContext(), services));
                            lasttype = 2;
                            print("");
                        }
                        catch (Exception e)
                        {
                            OnFail(0, e.getMessage());
                        }
                    }

                    @Override
                    public void OnFail(int code, String result)
                    {
                        print(result);
                    }
                });
            }
        });

        favoriteOffers.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                print("loading . . .");

                _ServerCaller.GetMyLikedOffers(new OnOprationDoing()
                {
                    @Override
                    public void OnSuccess(String result)
                    {
                        try
                        {
                            offers = ExtracterDataFromJson.getOffers(result);
                            main_servicetableLayout.setAdapter(new OffersArrayAdapter(getApplicationContext(), offers));
                            lasttype = 3;
                            print("");
                        } catch (Exception e)
                        {
                            OnFail(0, e.getMessage());
                        }
                    }

                    @Override
                    public void OnFail(int code, String result)
                    {
                        print(result);
                    }
                });
            }
        });

        requestedOffers.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                print("loading . . .");
                _ServerCaller.GetMyRequstedOffers(new OnOprationDoing()
                {
                    @Override
                    public void OnSuccess(String result)
                    {
                        try
                        {
                            offers = ExtracterDataFromJson.getOffers(result);
                            main_servicetableLayout.setAdapter(new OffersArrayAdapter(getApplicationContext(), offers));
                            lasttype = 3;
                            print("");
                        } catch (Exception e)
                        {
                            OnFail(0, e.getMessage());
                        }
                    }

                    @Override
                    public void OnFail(int code, String result)
                    {
                        print(result);
                    }
                });
            }
        });

        myRequestsIMade.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                print("loading . . .");
                _ServerCaller.GetMyRequests(new OnOprationDoing()
                {
                    @Override
                    public void OnSuccess(String result)
                    {
                        try
                        {
                            requests = ExtracterDataFromJson.getRequests(result);
                            main_servicetableLayout.setAdapter(new RequestsArrayAdapter(getApplicationContext(), requests));
                            lasttype = 4;
                            print("");
                        } catch (Exception e)
                        {
                            OnFail(0, e.getMessage());
                        }
                    }

                    @Override
                    public void OnFail(int code, String result)
                    {
                        print(result);
                    }
                });
            }
        });
        myRequestsAsOwner.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                print("loading . . .");
                _ServerCaller.GetRequestsToAdmin(new OnOprationDoing()
                {
                    @Override
                    public void OnSuccess(String result)
                    {
                        try
                        {
                            requests = ExtracterDataFromJson.getRequests(result);
                            main_servicetableLayout.setAdapter(new RequestsArrayAdapter(getApplicationContext(), requests));
                            lasttype = 4;
                            print("");
                        } catch (Exception e)
                        {
                            OnFail(0, e.getMessage());
                        }
                    }

                    @Override
                    public void OnFail(int code, String result)
                    {
                        print(result);
                    }
                });
            }
        });

        newService.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(MainActivity.this, CreateServices.class));
            }
        });

        myServices.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                print("loading . . .");
                _ServerCaller.GetMyServices(new OnOprationDoing()
                {
                    @Override
                    public void OnSuccess(String result)
                    {
                        try
                        {
                            services = ExtracterDataFromJson.getServices(result);
                            main_servicetableLayout.setAdapter(new ServicesArrayAdapter(getApplicationContext(), services));
                            lasttype = 2;
                            print("");
                        }
                        catch (Exception e)
                        {
                            OnFail(0, e.getMessage());
                        }
                    }

                    @Override
                    public void OnFail(int code, String result)
                    {
                        print(result);
                    }
                });
            }
        });

        main_servicetableLayout.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                if (lasttype == 1)
                    alert.AlertFriendHandler(freinds.get(position), true);
                else if (lasttype == 2)
                    alert.AlertServicesHandler(services.get(position), true);
                else if (lasttype == 3)
                    alert.AlertOffersHandler(offers.get(position), true);
                else if (lasttype == 4)
                    alert.AlertRequestsHandler(requests.get(position));
            }
        });

        main_servicetableLayout.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
            {
                if (lasttype == 1)
                    alert.AlertFriendHandler(freinds.get(position), false);
                else if (lasttype == 2)
                    alert.AlertServicesHandler(services.get(position), false);
                else if (lasttype == 3)
                    alert.AlertOffersHandler(offers.get(position), false);
                return true;
            }
        });

        myFriends.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                print("loading . . .");

                _ServerCaller.GetFriends(new OnOprationDoing()
                {
                    @Override
                    public void OnSuccess(String result)
                    {
                        try
                        {
                            freinds = ExtracterDataFromJson.getFriends(result);
                            main_servicetableLayout.setAdapter(new FriendsArrayAdapter(getApplicationContext(), freinds));
                            lasttype = 1;
                            print("");
                        } catch (Exception e)
                        {
                            OnFail(0, e.getMessage());
                        }
                    }

                    @Override
                    public void OnFail(int code, String result)
                    {
                        print(result);
                    }
                });
            }
        });

        checkUserTypeToHideItemsThatHeDontAllowedToDoIt();
        getLocation();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == RESULT_OK){
                if (data.getBooleanExtra("loginSuccess", true))
                    checkUserTypeToHideItemsThatHeDontAllowedToDoIt();
            }
        }
    }
    public void checkUserTypeToHideItemsThatHeDontAllowedToDoIt()  // nice name
    {
        int visiblity = (ServerSDK._User == null || ServerSDK._User.typeisSP) ? View.VISIBLE : View.GONE;

        Button b1 = (Button) findViewById(R.id.myRequestsAsOwner);
        Button b2 = (Button) findViewById(R.id.myServices);
        Button b3 = (Button) findViewById(R.id.newService);

        b1.setVisibility(visiblity);
        b2.setVisibility(visiblity);
        b3.setVisibility(visiblity);
    }

    public void print(String str) {
        //Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
        statuslabel.setText(str);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.action_setting)
        {
            startActivity(new Intent(MainActivity.this,Settings.class));
        }

        if (item.getItemId() == R.id.action_search)
        {
            startActivity(new Intent(MainActivity.this, Search.class));
        }
        if (item.getItemId() == R.id.action_logout)
        {
            new AlertDialog.Builder(this)
                    .setTitle("Logout")
                    .setMessage("are you sure ?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            print("loading . . .");
                            _ServerCaller.logout(new OnOprationDoing()
                            {
                                @Override
                                public void OnSuccess(String result)
                                {
                                    print("");
                                }

                                @Override
                                public void OnFail(int code, String result)
                                {
                                    print(result);
                                }

                                @Override
                                public void afterFinish()
                                {
                                    _ServerCaller._User = null;
                                    clearStoredData();
                                    startActivityForResult(new Intent(MainActivity.this, Login.class), 1);
                                }

                            });
                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

        return super.onOptionsItemSelected(item);
    }


    public void getLocation()
    {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        String provider = LocationManager.GPS_PROVIDER;
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
            provider = LocationManager.NETWORK_PROVIDER;
        else if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            print("Location Service turned off in your device, pleace tune it on");
            //return;
        }

        locationManager.requestLocationUpdates(provider, 5000, 10, new LocationListener()
        {
            @Override
            public void onLocationChanged(Location location)
            {
                ServerSDK._myLatitude = location.getLatitude();
                ServerSDK._myLongitude = location.getLongitude();
//                String longitude = "Longitude: " + location.getLongitude();
//                String latitude = "Latitude: " + location.getLatitude();
//
//                // To get city name from coordinates
//                String cityName = null;
//                Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
//                List<Address> addresses;
//                try
//                {
//                    addresses = gcd.getFromLocation(location.getLatitude(),
//                            location.getLongitude(), 1);
//                    if (addresses.size() > 0)
//                        System.out.println(addresses.get(0).getLocality());
//                    cityName = addresses.get(0).getLocality();
//                } catch (IOException e)
//                {
//                    print("bad error");
//                }
//                print(longitude + "\n" + latitude + "\n\nMy Current City is: " + cityName);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras)
            {
            }

            @Override
            public void onProviderEnabled(String provider)
            {

            }

            @Override
            public void onProviderDisabled(String provider)
            {
            }
        });
    }
}


