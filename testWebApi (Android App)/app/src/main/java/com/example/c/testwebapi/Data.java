package com.example.c.testwebapi;

import java.util.ArrayList;
import java.util.List;

public class Data
{
    public static abstract class Guy
    {
        public String username;
        public String fname;
        public String lname;

        public boolean typeisSP;

        protected Guy(String un,String fn,String ln,boolean type)
        {
            username = un;
            fname=fn;
            lname=ln;
            typeisSP = type;
        }
    }

    public static class User extends Guy
    {
        public String accesstoken;

        public User(String AT, String usr, String fnam, String lnam, boolean typ)
        {
            super(usr,fnam,lnam,typ);
            accesstoken = AT;
        }
    }

    public static class Friend extends Guy
    {
        boolean followed;
        public Friend(String usr, String fnam, String lnam, boolean typ,boolean folowed)
        {
            super(usr, fnam, lnam, typ);
            followed = folowed;
        }

		public String toString(boolean isItmyInfo)
		{
			return isItmyInfo
                    ?
                    ("username = " + username +
                    "\nFull name = " +  fname + " " + lname +
                    "\nService Provider = " + typeisSP +"\n\n")
                    :
                    ( "username = " + username +
                    "\nFull name = " +  fname + " " + lname +
                    "\nService Provider = " + typeisSP +"\n\n"+
                    (followed ? "you follow this person" : "you aren't follow this person"))
                    ;
		}
    }

    public static class Service
    {
        public int id;
        public String name;
        public String type;
        public double laititude;
        public double longitude;
        public String phone;
        public String website;
        public String desc;
        public int likes;

        public String owner;

        boolean liked;

        public List<String> images;

        public Service()
        {
            images = new ArrayList<String>();
        }

        public Service(int sid, String sname, double slong, double slait,
                       String stype, String sphone, String swebsite, String sdesc, String sowner, int slikes, boolean sliked)
        {
            id = sid;
            name = sname;
            longitude = slong;
            laititude = slait;
            type = stype;
            phone = sphone;
            website = swebsite;
            desc = sdesc;
            owner = sowner;
            images = new ArrayList<String>();
            likes = slikes;
            liked = sliked;
        }

        @Override
        public String toString()
        {
            return "ID = " + id +
                    "\nName = " + name +
                    "\nOwner = " + owner +
                    "\nType = " + type +
                    "\nLongitude = " + longitude +
                    "\nLaititude = " + laititude +
                    "\nPhone = " + phone +
                    "\nWebsite = " + website +
                    "\nDescription = " + desc +
                    "\nLikeCount = " + likes +
                    "\nDistance " + formationgDistance(ServerSDK.GetDistanceFrom(this)) +
                    "\n\n" + (liked ? "you liked this service" : "");
        }

        public static String formationgDistance(double d)
        {
            d = Math.ceil(d);
            String str = d + " meters";

            if(d >= 1000)
            {
                d /= 1000;
                str = d + " km";
            }

            return str;
        }
    }
	
	public static class Offer
    {
        public int id, serviceid, quantity, maxcost, mincost, likecount, requestcount;
        public String name, servicename, owner, desc;
        public boolean requestable, liked, requested;


        public List<String> images;

        public Offer()
        {
            images = new ArrayList<String>();
        }

        public Offer(int oid, int sid, String n, String sn, String sowner, String dsc, int qunty, int mx, int mn, int slikd, int requst, boolean rqust, boolean likd,List<String>imgs,boolean requeted)
        {
            id = oid;
            serviceid = sid;
            name = n;
            servicename = sn;
            owner = sowner;
            desc = dsc;
            quantity = qunty;
            maxcost = mx;
            mincost = mn;
            likecount = slikd;
            requestcount = requst;
            requestable = rqust;
            liked = likd;

            images=imgs;
            requested = requeted;
        }
		
		@Override
		public String toString()
		{
			return "ID = " + id +
				   "\nName = " + name +
				   "\nService = " + servicename +
				   "\nOwner = " + owner +
				   "\nQuantity = " + quantity +
					"\nMaxCost = " + maxcost +
					"\nMinCost = " + mincost +
					"\nRequestable = " + requestable +
					"\nDescription = " + desc +
					"\nLikeCount = " + likecount +
					"\nRequestCount = " + requestcount +
                    (requested ? "\n\nyou request this offer" : "")+
					"\n" + (liked ? "you liked this offer\n" : "");
		}
    }


    public static class Request{
        public int offerid, serviceid, rating;
        public String offername, servicename, usernameWhoRequest, requsetMassage,response ,responseMassage,
                requestDate,responseDate;

        public Request(int rid,int sid,String na,String sname, String oname,String rmsg,String rsns,String respmsg,String requestDat,String responseDat,int ratng){
            offerid=rid;
            serviceid=sid;
            rating=ratng;
            offername =na;
            servicename=sname;
            usernameWhoRequest=oname;
            requsetMassage=rmsg;
            response =rsns;
            responseMassage = respmsg;

            requestDate = requestDat;
            responseDate = responseDat;
        }

        @Override
        public String toString()
        {
            return "offerID = " + offerid +
                    "\nOffer = " + offername +

                    "\nServiceID = " + serviceid +
                    "\nService = " + servicename +
                    "\nRequest from = " + usernameWhoRequest +
                    "\nrequsetMassage = " + requsetMassage +
                    "\nrequsetDate = " + requestDate +
                    "\nresponse = " + response +
                    "\nresponseMassage = " + responseMassage +
                    "\nresponseDate = " + responseDate;
        }
    }
}