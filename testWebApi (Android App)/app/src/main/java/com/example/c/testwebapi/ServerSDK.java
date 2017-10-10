package com.example.c.testwebapi;

import android.app.Activity;
import android.net.Uri;
import android.widget.Toast;

public class ServerSDK
{

    public static double _myLongitude  = 0, _myLatitude = 0;
    public static Data.User _User;


    public static String serverAdrress = "http://bhlshrf2-001-site1.smarterasp.net/";
    //public static String serverAdrress = "http://10.0.2.2:23512/";


    public ServerSDK(Activity a)
    {
        A = a;
    }
    public Activity A;
    public void print(String str){
        Toast.makeText(A.getApplicationContext(), str, Toast.LENGTH_SHORT).show();
    }


//////// USERS ////////////////////////////////////////////////////////////////////////////////////////////////

    public void login(String username, String pwd, OnOprationDoing delegate){
        new ServerCaller(delegate).execute(Uri.parse(serverAdrress + "login")
                .buildUpon()
                .appendQueryParameter("user", username)
                .appendQueryParameter("pwd", pwd)
                .build().toString());
    }

    public void login(String username, String pwd)
    {
        login(username, pwd, new OnOprationDoing()
        {
            @Override
            public void OnSuccess(String result)
            {
                print("login success");
            }

            @Override
            public void OnFail(int code, String result)
            {
                print("login fail\n" + result);
            }
        });
    }

    public void logout(OnOprationDoing delegate)
    {
        if (_User != null)
        {
            new ServerCaller(delegate).execute(Uri.parse(serverAdrress + "login")
                    .buildUpon()
                    .appendQueryParameter("user", _User.accesstoken)
                    .appendQueryParameter("action", "logout")
                    .build().toString());
        } else
            print("You are not loged in!");
    }

    public void logout()
    {
        logout(new OnOprationDoing()
        {
            @Override
            public void OnSuccess(String result)
            {
                _User = null;
                print("logout success");
            }

            @Override
            public void OnFail(int code, String result)
            {
                print("logout fail\nerror code:" + code + "\nmore info:\n" + result);
            }
        });
    }


    public void RegisterUser(String username, String pwd, String name, String lname, boolean isSerivceProvider, OnOprationDoing delegate)
    {

        new ServerCaller(delegate).execute(
                Uri.parse(serverAdrress + "Register")
                        .buildUpon()
                        .appendQueryParameter("user", username)
                        .appendQueryParameter("pwd", pwd)
                        .appendQueryParameter("name", name)
                        .appendQueryParameter("lastname", lname)
                        .appendQueryParameter("type", (isSerivceProvider ? "service" : "user"))
                        .build().toString());
    }

    public void RegisterUser(String username, String pwd, String name, String lname, boolean isSerivceProvider)
    {
        RegisterUser(username, pwd, name, lname, isSerivceProvider,
                new OnOprationDoing()
                {
                    @Override
                    public void OnSuccess(String result)
                    {
                        print("Register user success");
                    }

                    @Override
                    public void OnFail(int code, String result)
                    {
                        print("Register user fail\nerror code:" + code + "\nmore info:\n" + result);
                    }
                });
    }


//////// SERVICES ////////////////////////////////////////////////////////////////////////////////////////////////

    public void CreateService(String name, double x, double y, String phone, String desc, String website, String type, OnOprationDoing delegate)
    {
        new ServerCaller(delegate).execute(Uri.parse(serverAdrress + "Services").buildUpon()
                .appendQueryParameter("accesstoken", _User.accesstoken)
                .appendQueryParameter("servicename", name)
                .appendQueryParameter("x", x + "")
                .appendQueryParameter("y", y + "")
                .appendQueryParameter("desc", desc)
                .appendQueryParameter("phone", phone)
                .appendQueryParameter("url", website)
                .appendQueryParameter("type", type)
                .appendQueryParameter("action", "create")
                .build().toString());
    }

    public void EditService(int serviceid,String name, double x, double y, String phone, String desc, String website, String type, OnOprationDoing delegate)
    {
        new ServerCaller(delegate).execute(Uri.parse(serverAdrress + "Services").buildUpon()
                .appendQueryParameter("accesstoken", _User.accesstoken)
                .appendQueryParameter("serviceid", serviceid+"")
                .appendQueryParameter("servicename", name)
                .appendQueryParameter("x", x + "")
                .appendQueryParameter("y", y + "")
                .appendQueryParameter("desc", desc)
                .appendQueryParameter("phone", phone)
                .appendQueryParameter("url", website)
                .appendQueryParameter("type", type)
                .appendQueryParameter("action", "edit")
                .build().toString());
    }

    public void CreateService(String name, double x, double y, String phone, String desc, String website, String type)
    {
        CreateService(name, x, y, phone, desc, website, type,
                new OnOprationDoing()
                {
                    @Override
                    public void OnSuccess(String result)
                    {
                        print("Create Service success");
                    }

                    @Override
                    public void OnFail(int code, String result)
                    {
                        print("Create Service fail\nerror code:" + code + "\nmore info:\n" + result);
                    }
                });
    }


    public enum ServiceActions
    {
        read, like, unlike, delete
    }

    public void ServiceAction(int serviceid, ServiceActions sa, OnOprationDoing delegate)
    {
        new ServerCaller(delegate).execute(Uri.parse(serverAdrress + "Services").buildUpon()
                .appendQueryParameter("accesstoken", _User.accesstoken)
                .appendQueryParameter("serviceid", serviceid + "")
                .appendQueryParameter("action", sa.name())
                .build().toString());
    }

    public void ServiceAction(int serviceid, ServiceActions sa)
    {
        ServiceAction(serviceid, sa, new OnOprationDoing()
        {
            @Override
            public void OnSuccess(String result)
            {
                print("Create Service success");
            }

            @Override
            public void OnFail(int code, String result)
            {
                print("Can't delete Service\nerror code:" + code + "\nmore info:\n" + result);
            }
        });
    }


    public void GetMyServices(OnOprationDoing delegate)
    {
        new ServerCaller(delegate).execute(Uri.parse(serverAdrress + "ServicesGetter").buildUpon()
                .appendQueryParameter("accesstoken", _User.accesstoken)
                .appendQueryParameter("from", "me")
                .build().toString());
    }

    public void GetMyFavoriteServices(OnOprationDoing delegate)
    {
        new ServerCaller(delegate).execute(Uri.parse(serverAdrress + "ServicesGetter").buildUpon()
                .appendQueryParameter("accesstoken", _User.accesstoken)
                .appendQueryParameter("from", "favorite")
                .build().toString());
    }

    public void GetHisServices(String hisname, OnOprationDoing delegate)
    {
        new ServerCaller(delegate).execute(Uri.parse(serverAdrress + "ServicesGetter").buildUpon()
                .appendQueryParameter("accesstoken", _User.accesstoken)
                .appendQueryParameter("from", "friend")
                .appendQueryParameter("friend", hisname)
                .build().toString());
    }


//////// OFFERS  and REQUESTS ///////////////////////////////////////////////////////////////////////////////////


    public void GetMyLikedOffers(OnOprationDoing delegate)
    {
        new ServerCaller(delegate).execute(Uri.parse(serverAdrress + "OffersGetter").buildUpon()
                .appendQueryParameter("accesstoken", _User.accesstoken)
                .appendQueryParameter("from", "like")
                .build().toString());
    }

    public void GetMyRequstedOffers(OnOprationDoing delegate)
    {
        new ServerCaller(delegate).execute(Uri.parse(serverAdrress + "OffersGetter").buildUpon()
                .appendQueryParameter("accesstoken", _User.accesstoken)
                .appendQueryParameter("from", "requestoffer")
                .build().toString());
    }

    public void GetOffersOfServices(int serviceid, OnOprationDoing delegate)
    {
        new ServerCaller(delegate).execute(Uri.parse(serverAdrress + "OffersGetter").buildUpon()
                .appendQueryParameter("accesstoken", _User.accesstoken)
                .appendQueryParameter("from", "service")
                .appendQueryParameter("serviceid", serviceid+"")
                .build().toString());
    }

    public void GetRequestsToAdmin(OnOprationDoing delegate)
    {
        System.out.println("___________________________________________________");
        System.out.println(Uri.parse(serverAdrress + "OffersGetter").buildUpon()
                .appendQueryParameter("accesstoken", _User.accesstoken)
                .appendQueryParameter("from", "requestsOwner")
                .build().toString());
        System.out.println("__________________________________________________");

        new ServerCaller(delegate).execute(Uri.parse(serverAdrress + "OffersGetter").buildUpon()
                .appendQueryParameter("accesstoken", _User.accesstoken)
                .appendQueryParameter("from", "requestsOwner")
                .build().toString());
    }

    public void GetMyRequests(OnOprationDoing delegate)
    {
        System.out.println("___________________________________________________");
        System.out.println(Uri.parse(serverAdrress + "OffersGetter").buildUpon()
                .appendQueryParameter("accesstoken", _User.accesstoken)
                .appendQueryParameter("from", "myrequests")
                .build().toString());
        System.out.println("__________________________________________________");
        new ServerCaller(delegate).execute(Uri.parse(serverAdrress + "OffersGetter").buildUpon()
                .appendQueryParameter("accesstoken", _User.accesstoken)
                .appendQueryParameter("from", "myrequests")
                .build().toString());
    }

    public void RequestOffer(int offerid, String requsetMassage, OnOprationDoing delegate)
    {
        new ServerCaller(delegate).execute(Uri.parse(serverAdrress + "Offers").buildUpon()
                .appendQueryParameter("accesstoken", _User.accesstoken)
                .appendQueryParameter("offerid", offerid + "")
                .appendQueryParameter("requsetMassage", requsetMassage)
                .appendQueryParameter("action", "request")
                .build().toString());
    }

    public void unRequestOffer(int offerid, OnOprationDoing delegate)
    {
        new ServerCaller(delegate).execute(Uri.parse(serverAdrress + "Offers").buildUpon()
                .appendQueryParameter("accesstoken", _User.accesstoken)
                .appendQueryParameter("offerid", offerid + "")
                .appendQueryParameter("action", "unrequest")
                .build().toString());
    }

    public void ResponseOffer(int offerid, String clientname, String response, String responseMassage, OnOprationDoing delegate)
    {
        new ServerCaller(delegate).execute(Uri.parse(serverAdrress + "Offers").buildUpon()
                .appendQueryParameter("accesstoken", _User.accesstoken)
                .appendQueryParameter("offerid", offerid + "")
                .appendQueryParameter("clientname", clientname)
                .appendQueryParameter("response", response)
                .appendQueryParameter("responseMassage", responseMassage)
                .appendQueryParameter("action", "response")
                .build().toString());
    }

    public void LikeOffer(int offerid, OnOprationDoing delegate)
    {
        new ServerCaller(delegate).execute(Uri.parse(serverAdrress + "Offers").buildUpon()
                .appendQueryParameter("accesstoken", _User.accesstoken)
                .appendQueryParameter("offerid", offerid + "")
                .appendQueryParameter("action", "like")
                .build().toString());
    }
    public void DislikeOffer(int offerid, OnOprationDoing delegate)
    {
        new ServerCaller(delegate).execute(Uri.parse(serverAdrress + "Offers").buildUpon()
                .appendQueryParameter("accesstoken", _User.accesstoken)
                .appendQueryParameter("offerid", offerid + "")
                .appendQueryParameter("action", "unlike")
                .build().toString());
    }



    public void CreateOffer(int serviceid,String ofrname,int min,int max,int qunt,boolean requestable,String desc,OnOprationDoing delegate)
    {
        new ServerCaller(delegate).execute(Uri.parse(serverAdrress + "Offers").buildUpon()
                .appendQueryParameter("accesstoken", _User.accesstoken)
                .appendQueryParameter("serviceid", serviceid + "")

                .appendQueryParameter("offername", ofrname)
                .appendQueryParameter("mincost", min + "")
                .appendQueryParameter("maxcost", max + "")
                .appendQueryParameter("quantity", qunt + "")
                .appendQueryParameter("desc", desc)
                .appendQueryParameter("requestable", requestable+"")

                .appendQueryParameter("action", "create")
                .build().toString());
    }

    public void DeleteOffer(int offerid,OnOprationDoing delegate)
    {
        new ServerCaller(delegate).execute(Uri.parse(serverAdrress + "Offers").buildUpon()
                .appendQueryParameter("accesstoken", _User.accesstoken)
                .appendQueryParameter("offerid", offerid + "")
                .appendQueryParameter("action", "delete")
                .build().toString());
    }


    public void EditOffer(int offerid,String ofrname,int min,int max,int qunt,boolean requestable,String desc,OnOprationDoing delegate)
    {
        new ServerCaller(delegate).execute(Uri.parse(serverAdrress + "Offers").buildUpon()
                .appendQueryParameter("accesstoken", _User.accesstoken)
                .appendQueryParameter("offerid", offerid + "")

                .appendQueryParameter("offername", ofrname)
                .appendQueryParameter("mincost", min + "")
                .appendQueryParameter("maxcost", max + "")
                .appendQueryParameter("quantity", qunt + "")
                .appendQueryParameter("desc", desc)
                .appendQueryParameter("requestable", requestable+"")

                .appendQueryParameter("action", "edit")
                .build().toString());
    }

//////// FRIENDS /////////////////////////////////////////////////////////////////////////////////////////////////

    public void AddFriend(String friend, OnOprationDoing delegate){
        new ServerCaller(delegate).execute(Uri.parse(serverAdrress + "Friends").buildUpon()
                .appendQueryParameter("accesstoken", _User.accesstoken)
                .appendQueryParameter("friend", friend)
                .appendQueryParameter("action", "follow")
                .build().toString());
    }

    public void DeleteFriend(String friend, OnOprationDoing delegate){
        new ServerCaller(delegate).execute(Uri.parse(serverAdrress + "Friends").buildUpon()
                .appendQueryParameter("accesstoken", _User.accesstoken)
                .appendQueryParameter("friend", friend)
                .appendQueryParameter("action", "unfollow")
                .build().toString());
    }

    public void GetMyFriendsCount(OnOprationDoing delegate){
        new ServerCaller(delegate).execute(Uri.parse(serverAdrress + "Friends").buildUpon()
                .appendQueryParameter("accesstoken", _User.accesstoken)
                .appendQueryParameter("action", "getCount")
                .build().toString());
    }

    public void GetFriends(OnOprationDoing delegate){
        new ServerCaller(delegate).execute(Uri.parse(serverAdrress + "Friends").buildUpon()
                .appendQueryParameter("accesstoken", _User.accesstoken)
                .appendQueryParameter("action", "getFriends")
                .build().toString());
    }

    public void GetHisFriendsCount(String friend, OnOprationDoing delegate){
        new ServerCaller(delegate).execute(Uri.parse(serverAdrress + "Friends").buildUpon()
                .appendQueryParameter("accesstoken", _User.accesstoken)
                .appendQueryParameter("friend", friend)
                .appendQueryParameter("action", "getCount")
                .build().toString());
    }

    public void GetHisInfo(String hisname, OnOprationDoing delegate){
        new ServerCaller(delegate).execute(Uri.parse(serverAdrress + "Friends").buildUpon()
                .appendQueryParameter("accesstoken", _User.accesstoken)
                .appendQueryParameter("friend", hisname)
                .appendQueryParameter("action", "getAccountInfo")
                .build().toString());
    }


    //////// SEARCHer ////////////////////////////////////////////////////////////////////////////////////////////////

    public void SearchUsers(String txt, OnOprationDoing delegate){
        new ServerCaller(delegate).execute(Uri.parse(serverAdrress + "Search").buildUpon()
                .appendQueryParameter("accesstoken", _User.accesstoken)
                .appendQueryParameter("query", txt)
                .appendQueryParameter("from", "users")
                .build().toString());
    }

    public void SearchServices(String txt,OnOprationDoing delegate){
        new ServerCaller(delegate).execute(Uri.parse(serverAdrress + "Search").buildUpon()
                .appendQueryParameter("accesstoken", _User.accesstoken)
                .appendQueryParameter("query", txt)
                .appendQueryParameter("from", "Services")
                .build().toString());
    }

    public void SearchServices(String txt,double myLat,double myLon,double distance, OnOprationDoing delegate)
    {
        new ServerCaller(delegate).execute(Uri.parse(serverAdrress + "Search").buildUpon()
                .appendQueryParameter("accesstoken", _User.accesstoken)
                .appendQueryParameter("query", txt)

                .appendQueryParameter("myLat", myLat + "")
                .appendQueryParameter("myLon", myLon + "")
                .appendQueryParameter("distance", distance + "")

                .appendQueryParameter("from", "Services")
                .build().toString());
    }

    public void SearchOffers(String txt, OnOprationDoing delegate){
        new ServerCaller(delegate).execute(Uri.parse(serverAdrress + "Search").buildUpon()
                .appendQueryParameter("accesstoken", _User.accesstoken)
                .appendQueryParameter("query", txt)
                .appendQueryParameter("from", "offers")
                .build().toString());
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////



    public static double GetDistanceFrom(Data.Service ds) {
         return ConvertCoordinatesToMeters(_myLatitude,_myLongitude,ds.laititude,ds.longitude);
    }

    public static double ConvertCoordinatesToMeters(double lat1, double lon1, double lat2, double lon2){
        double R = 6378.137; // Radius of earth in KM
        double dLat = (lat2 - lat1) * Math.PI / 180;
        double dLon = (lon2 - lon1) * Math.PI / 180;
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = R * c;
        return d * 1000; // meters
    }
}
