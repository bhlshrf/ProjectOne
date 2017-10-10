package com.example.c.testwebapi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by 01 on 23/5/2015.
 */
public class AlertWindow
{
    public AlertWindow(Activity a) { mainActivity = a; }
    public Activity mainActivity;

    public void print(String str){
        Toast.makeText(mainActivity.getApplicationContext(), str, Toast.LENGTH_SHORT).show();
    }

    public void AlertFriendHandler(final Data.Friend usr, boolean justShowHisInfoOrFollowHim){

        boolean me = usr.username.equalsIgnoreCase(ServerSDK._User.username);
        if (justShowHisInfoOrFollowHim)
        {
            new AlertDialog.Builder(mainActivity)
                    .setTitle("Person Info")
                    .setMessage(usr.toString(me))
                    .setPositiveButton(android.R.string.ok, null)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .show();
        }
        else
        {
            if(me)
                return;

            if (usr.followed)
                new AlertDialog.Builder(mainActivity)
                        .setTitle("Friends")
                        .setMessage("unfollow this guy ?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                MainActivity._ServerCaller.DeleteFriend(usr.username, new OnOprationDoing()
                                {
                                    @Override
                                    public void OnSuccess(String result)
                                    {
                                        usr.followed = false;
                                        print("unfollowed");
                                    }

                                    @Override
                                    public void OnFail(int code, String result)
                                    {
                                        print(result);
                                    }
                                });
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            else
                new AlertDialog.Builder(mainActivity)
                        .setTitle("Friends")
                        .setMessage("follow this guy ?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                MainActivity._ServerCaller.AddFriend(usr.username, new OnOprationDoing()
                                {
                                    @Override
                                    public void OnSuccess(String result)
                                    {
                                        usr.followed = true;
                                        print("followed");
                                    }

                                    @Override
                                    public void OnFail(int code, String result)
                                    {
                                        print(result);
                                    }
                                });
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
        }
    }
    public void AlertServicesHandler(final Data.Service srv, boolean justShowItsInfoOrLikeIt){
        if (justShowItsInfoOrLikeIt)
        {
            new AlertDialog.Builder(mainActivity)
                    .setTitle("Service Info")
                    .setMessage(srv.toString())
                    .setPositiveButton(android.R.string.ok, null)
                    .setNeutralButton("show map", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            MapsActivity.PassedDatas = new Data.Service[] { srv };
                            mainActivity.startActivity(new Intent(mainActivity,MapsActivity.class));
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .show();
        }
        else
        {
            if(srv.owner.equalsIgnoreCase(MainActivity._ServerCaller._User.username))
            {
                new AlertDialog.Builder(mainActivity)
                        .setTitle("Service")
                        .setMessage("did you want edit this Service?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                CreateServices.service = srv;
                                mainActivity.startActivity(new Intent(mainActivity, CreateServices.class));
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setNeutralButton("show map", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                MapsActivity.PassedDatas = new Data.Service[] { srv };
                                mainActivity.startActivity(new Intent(mainActivity, MapsActivity.class));
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
            else if (srv.liked)
                new AlertDialog.Builder(mainActivity)
                        .setTitle("Service")
                        .setMessage("did you want remove this Service from favorite?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                MainActivity._ServerCaller.ServiceAction(srv.id, ServerSDK.ServiceActions.unlike, new OnOprationDoing()
                                {
                                    @Override
                                    public void OnSuccess(String result)
                                    {
                                        srv.liked = false;
                                        srv.likes--;
                                        print("removed");
                                    }

                                    @Override
                                    public void OnFail(int code, String result)
                                    {
                                        print(result);
                                    }
                                });
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setNeutralButton("show map", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {

                                MapsActivity.PassedDatas = new Data.Service[] { srv };
                                mainActivity.startActivity(new Intent(mainActivity, MapsActivity.class));
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            else
                new AlertDialog.Builder(mainActivity)
                        .setTitle("Service")
                        .setMessage("did you want add this Service to favorite?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                MainActivity._ServerCaller.ServiceAction(srv.id, ServerSDK.ServiceActions.like, new OnOprationDoing()
                                {
                                    @Override
                                    public void OnSuccess(String result)
                                    {
                                        srv.liked = true;
                                        srv.likes++;
                                        print("done");
                                    }

                                    @Override
                                    public void OnFail(int code, String result)
                                    {
                                        print(result);
                                    }
                                });
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setNeutralButton("show map", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                MapsActivity.PassedDatas = new Data.Service[] { srv };
                                mainActivity.startActivity(new Intent(mainActivity, MapsActivity.class));
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
        }
    }

    public void AlertOffersHandler(final Data.Offer ofr, boolean justShowItsInfoOrLikeIt){
        if (justShowItsInfoOrLikeIt)
        {
            AlertDialog.Builder x = new AlertDialog.Builder(mainActivity)
                    .setTitle("Offer Info")
                    .setMessage(ofr.toString())
                    .setNegativeButton("Ok", null)
                    .setIcon(android.R.drawable.ic_dialog_info);

            if (!ofr.owner.equalsIgnoreCase(MainActivity._ServerCaller._User.username))
            {
                if (!ofr.requested)
                    x.setPositiveButton("Request", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            MainActivity._ServerCaller.RequestOffer(ofr.id, "I want request your offer", new OnOprationDoing()
                            {
                                @Override
                                public void OnSuccess(String result)
                                {
                                    ofr.requestcount++;
                                    print("done");
                                }

                                @Override
                                public void OnFail(int code, String result)
                                {
                                    print(result);
                                }
                            });
                        }
                    });
                else
                    x.setPositiveButton("Remove Request", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            MainActivity._ServerCaller.unRequestOffer(ofr.id, new OnOprationDoing()
                            {
                                @Override
                                public void OnSuccess(String result)
                                {
                                    ofr.requestcount--;
                                    print("done");
                                }

                                @Override
                                public void OnFail(int code, String result)
                                {
                                    print(result);
                                }
                            });
                        }
                    });
            }

            x.show();
        }
        else
        {
            if(ofr.owner.equalsIgnoreCase(MainActivity._ServerCaller._User.username))
            {
                new AlertDialog.Builder(mainActivity)
                        .setTitle("Offer")
                        .setMessage("did you want edit this Offer?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                CreateOffers.offer = ofr;
                                mainActivity.startActivity(new Intent(mainActivity,CreateOffers.class));
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
            else if (ofr.liked)
                new AlertDialog.Builder(mainActivity)
                        .setTitle("Offer")
                        .setMessage("Did you want unlike this offer ?")
                        .setPositiveButton(android.R.string.ok, null)
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                MainActivity._ServerCaller.DislikeOffer(ofr.id,new OnOprationDoing()
                                {
                                    @Override
                                    public void OnSuccess(String result)
                                    {
                                        ofr.likecount--;
                                        ofr.liked = false;
                                        print("done");
                                    }

                                    @Override
                                    public void OnFail(int code, String result)
                                    {
                                        print(result);
                                    }
                                });
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            else
                new AlertDialog.Builder(mainActivity)
                        .setTitle("Offer")
                        .setMessage("Did you want like this offer ?")
                        .setPositiveButton(android.R.string.ok, null)
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                MainActivity._ServerCaller.LikeOffer(ofr.id,new OnOprationDoing()
                                {
                                    @Override
                                    public void OnSuccess(String result)
                                    {
                                        ofr.likecount++;
                                        ofr.liked = true;
                                        print("done");
                                    }

                                    @Override
                                    public void OnFail(int code, String result)
                                    {
                                        print(result);
                                    }
                                });
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
        }
    }

    public void AlertRequestsHandler(final Data.Request rqst){
            AlertDialog.Builder x = new AlertDialog.Builder(mainActivity)
                    .setTitle("Request Info")
                    .setMessage(rqst.toString())
                    .setNeutralButton("Ok", null)
                    .setIcon(android.R.drawable.ic_dialog_info);

            if (!rqst.usernameWhoRequest.equalsIgnoreCase(MainActivity._ServerCaller._User.username))
            {
                x.setPositiveButton("Accept", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        MainActivity._ServerCaller.ResponseOffer(rqst.offerid,rqst.usernameWhoRequest,"Accepted", "you are welcome", new OnOprationDoing()
                        {
                            @Override
                            public void OnSuccess(String result)
                            {
                                print("done");
                            }

                            @Override
                            public void OnFail(int code, String result)
                            {
                                print(result);
                            }
                        });
                    }
                });
                x.setNegativeButton("Delete", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        MainActivity._ServerCaller.ResponseOffer(rqst.offerid,rqst.usernameWhoRequest,"NotAccepted", "we are sorry, try later", new OnOprationDoing()
                        {
                            @Override
                            public void OnSuccess(String result)
                            {
                                print("done");
                            }

                            @Override
                            public void OnFail(int code, String result)
                            {
                                print(result);
                            }
                        });
                    }
                });
            }

            x.show();
    }
}
