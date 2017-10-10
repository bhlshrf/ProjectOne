package com.example.c.testwebapi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ExtracterDataFromJson
{
    public static List<Data.Service> getServices(String result) throws Exception{
        List<Data.Service> services = new ArrayList<>();

        JSONArray reader = new JSONArray(result);
        for (int i = 0; i < reader.length(); i++)
            services.add(convertService(reader.getJSONObject(i)));

        if(services.size()==0)
            throw new Exception("nothing found");

        return services;
    }
    public static Data.Service getService(String result) throws JSONException{
        return convertService(new JSONObject(result));
    }
    private static Data.Service convertService(JSONObject serviceData) throws JSONException{
        int id = serviceData.getInt("serviceid");
        String name = serviceData.getString("servicename");
        String username = serviceData.getString("username");

        String type = serviceData.getString("type");
        double laititude = serviceData.getDouble("laititude");
        double longitude = serviceData.getDouble("longitude");
        String phone = serviceData.getString("phone");
        String desc = serviceData.getString("desc");
        String url = serviceData.getString("url");
        int likes = serviceData.getInt("likecount");
        boolean liked = serviceData.getBoolean("liked");

        JSONArray imgs = serviceData.getJSONArray("imgs");
        List<String> imgsList = new ArrayList<>();
        for (int j = 0; j < imgs.length(); j++)
        {
            String img = imgs.getJSONObject(j).getString("image");

            if (!imgsList.contains(img))
                imgsList.add(0, img);
        }

        return new Data.Service(id,name,longitude,laititude,type,phone,url,desc,username,likes,liked);
    }

    public static List<Data.Friend> getFriends(String result) throws Exception{
        JSONArray json = new JSONArray(result);
        List<Data.Friend> rst = new ArrayList<>();
        for (int i = 0; i < json.length(); i++)
            rst.add(convertFriend(json.getJSONObject(i)));

        if(rst.size()==0)
            throw new Exception("nothing found");

        return rst;
    }
    private static Data.Friend convertFriend(JSONObject serviceData) throws JSONException{

        String username = serviceData.getString("username");
        String name = serviceData.getString("name");
        String lnam = serviceData.getString("lastname");
        boolean type = serviceData.getString("type").equals("service");
        boolean followed = serviceData.getBoolean("followed");

        return new Data.Friend(username,name,lnam,type,followed);
    }

    public static List<Data.Offer> getOffers(String result) throws Exception{
        JSONArray json = new JSONArray(result);
        List<Data.Offer> rst = new ArrayList<>();
        for (int i = 0; i < json.length(); i++)
            rst.add(convertOffer(json.getJSONObject(i)));

        if(rst.size()==0)
            throw new Exception("nothing found");

        return rst;
    }
    public static Data.Offer getOffer(String result) throws JSONException{
        return convertOffer(new JSONObject(result));
    }
    private static Data.Offer convertOffer(JSONObject json) throws JSONException{
            int offerid = json.getInt("offerid");
            String offername = json.getString("offername");
            int serviceid = json.getInt("serviceid");
            String servicename = json.getString("servicename");
            String username = json.getString("username");
            String desc = json.getString("desc");

            int quantity = json.getInt("quantity");
            int maxcost = json.getInt("maxcost");
            int mixcost = json.getInt("mixcost");

            int likecount = json.getInt("likecount");
            int requestcount = json.getInt("requestcount");

            boolean liked = json.getBoolean("liked");
            boolean requestable = json.getBoolean("requestable");

             boolean requested = json.getBoolean("requested");

            JSONArray imgs = json.getJSONArray("imgs");
            List<String> imgsList = new ArrayList<>();
            for (int j = 0; j < imgs.length(); j++)
            {
                String img = imgs.getJSONObject(j).getString("image");
                if (!imgsList.contains(img))
                    imgsList.add(0, img);
            }

            return new Data.Offer(offerid, serviceid, offername, servicename, username, desc, quantity, maxcost, mixcost, likecount, requestcount, requestable, liked, imgsList,requested);
    }

    public static Data.User getUser(String result) throws JSONException{
        JSONObject json = new JSONObject(result);

        Data.User usr = new Data.User(
                json.getString("accesstoken"),
                json.getString("username"),
                json.getString("name"),
                json.getString("lastname"),
                json.getString("type").equals("service"));

        if (usr.lname == "null")
            usr.lname = "";

        return usr;
    }


    public static List<Data.Request> getRequests(String result) throws Exception{
        JSONArray json = new JSONArray(result);
        List<Data.Request> rst = new ArrayList<>();
        for (int i = 0; i < json.length(); i++)
            rst.add(convertRequest(json.getJSONObject(i)));

        if(rst.size()==0)
            throw new Exception("nothing found");

        return rst;
    }
    public static Data.Request getRequest(String result) throws JSONException{
        return convertRequest(new JSONObject(result));
    }
    private static Data.Request convertRequest(JSONObject requestData) throws JSONException{
        int offerid = requestData.getInt("offerid");
        String offername = requestData.getString("offername");
        int serviceid = requestData.getInt("serviceid");
        String servicename = requestData.getString("servicename");

        String username = requestData.getString("username");

        String requsetMassage = requestData.getString("requsetMassage");
        String requestDate = requestData.getString("requestDate");

        String response = requestData.getString("response");
        String responseMassage = requestData.getString("responseMassage");

        String responseDate = requestData.getString("responseDate");

        int rating = requestData.getInt("rating");

        return new Data.Request(offerid,serviceid,offername,servicename,username,requsetMassage,response,responseMassage,requestDate,responseDate,rating);
    }
}
