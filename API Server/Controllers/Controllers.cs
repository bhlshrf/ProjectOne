
/////////////////////////////////////////////////////////////////////////////////////////////
////  PROJECT 1 - FITE - 2015 ///////////////////////////////////////////////////////////////
/////////////////   S E R V I C E S    O V E R    L O C A T I O N    ////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////
////                           S E R V E R   S I D E                                     ////
////                                                                                     ////
////  AUTHOR : BAHA'A AL-SHARIF  ////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////

using System;
using System.IO;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;
using System.Text;
using System.Data.EntityClient;
using Newtonsoft.Json;

using MvcApplication3.Models;
using System.Web.Hosting;

namespace MvcApplication3.Controllers
{
    public static class Server
    {
        public static Project1Entities database = new Project1Entities();
        public static List<Session> currentUser=new List<Session>();

        public static void Loger(string massage)
        {
            try
            {
                File.AppendAllText(HostingEnvironment.MapPath("~/App_Data/LogERROR.txt"), massage + "\r\n\r\n");
            }
            catch { }
        }
    }

    public class Session
    {
        public Session()
        {
            start = DateTime.Now;
        }
        public DateTime start { get; private set; }
        public user user { get; set; }
        public string accessToken { get; set; }

        public static string GenerateAccessToken()
        {
            // to do later : add more security, and add expire time
            
            StringBuilder sb=new StringBuilder();
            Random r=new Random();
            for (int i = 0 ; i < 40 ; i++)
                sb.Append(r.Next(1, 16).ToString("x"));
            return sb.ToString();
        }
    }

    public enum Action { create, delete, edit, read, like, unlike, request, response, unrequest }
    public enum UserType { user, service, admin }
    public enum ServiceType { Any, Public, Hotels, Restaurants, Sport, Education, Medical, IT, Other }





    
    public class LoginController: ApiController
    {
        public HttpResponseMessage Get(bool all = true)
        {
            try
            {
                if (all)
                    return ControllerContext.Request.CreateResponse(HttpStatusCode.OK, Server.database.users.Select(z => z.username + " - " + z.password + " - " + z.user_details.type));

                return ControllerContext.Request.CreateResponse(HttpStatusCode.OK, Server.currentUser.Select(x => new { x.start, x.user.username, x.accessToken }));
            }
            catch (Exception ex)
            {
                Server.Loger(DateTime.Now + " - LoginController - " + ex.Message);
                return ControllerContext.Request.CreateResponse(HttpStatusCode.InternalServerError, "Internal Error on server");
            }
        }

        public HttpResponseMessage Get(string user, string pwd = "", string action = "login")
        {
            try
            {
                action = action.ToLower();

                if (action == "login")
                {
                    var ur = Server.database.users.FirstOrDefault(x => x.username == user && x.password == pwd);
                    if (ur == null)
                        return ControllerContext.Request.CreateResponse(HttpStatusCode.BadRequest, "Invaild username or password");

                    string accesToken = Session.GenerateAccessToken();
                    Server.currentUser.Add(new Session() { user = ur, accessToken = accesToken });

                    return ControllerContext.Request.CreateResponse(HttpStatusCode.OK,
                        new
                            {
                                accesstoken = accesToken,
                                username = ur.username,
                                name = ur.user_details.name,
                                lastname = ur.user_details.lastname,
                                type = ur.user_details.type
                            });
                }
                else if (action == "logout")
                {
                    try
                    {
                        Session s=Server.currentUser.FirstOrDefault(x => x.accessToken == user);
                        if (s == null)
                            return ControllerContext.Request.CreateResponse(HttpStatusCode.BadRequest, "you loged-out already");
                        Server.currentUser.Remove(s);
                        return ControllerContext.Request.CreateResponse(HttpStatusCode.OK, "ok");
                    }
                    catch
                    {
                        return ControllerContext.Request.CreateResponse(HttpStatusCode.InternalServerError, "logout error");
                    }
                }

                return ControllerContext.Request.CreateResponse(HttpStatusCode.BadRequest, "bad request");
            }
            catch (Exception ex)
            {
                Server.Loger(DateTime.Now + " - LoginController - " + ex.Message);
                return ControllerContext.Request.CreateResponse(HttpStatusCode.InternalServerError, "Internal Error on server");
            }
        }

    }

    public class RegisterController: ApiController
    {
        public HttpResponseMessage Get(string user, string pwd, string name = "", string lastname = "", UserType type = UserType.user)
        {
            try
            {
                var ur = Server.database.users.FirstOrDefault(x => x.username == user);
                if (ur != null)
                    return ControllerContext.Request.CreateResponse(HttpStatusCode.BadRequest, "this username used, try something different");
                if (user.Contains(' '))
                    return ControllerContext.Request.CreateResponse(HttpStatusCode.BadRequest, "username can't contains Spaces");
                if (pwd == "")
                    return ControllerContext.Request.CreateResponse(HttpStatusCode.BadRequest, "bad password");
                if (string.IsNullOrWhiteSpace(name))
                    return ControllerContext.Request.CreateResponse(HttpStatusCode.BadRequest, "name can't be empty");

                user u = new user()
                {
                    username = user,
                    password = pwd,
                    user_details = new user_details()
                    {
                        type = type.ToString(),
                        name = name,
                        lastname = lastname
                    }
                };

                Server.database.users.Add(u);
                Server.database.SaveChanges();
                return ControllerContext.Request.CreateResponse(HttpStatusCode.OK, "ok");
            }
            catch (Exception ex)
            {
                Server.Loger(DateTime.Now + " - RegisterController - " + ex.Message);
                return ControllerContext.Request.CreateResponse(HttpStatusCode.InternalServerError, "Internal Error on server");
            }
        }

    }

    public class ServicesGetterController: ApiController
    {
        public enum ReturnType { favorite, me, friend }
        public HttpResponseMessage Get(string accesstoken, ReturnType from = ReturnType.favorite, string friend = "")
        {
            try
            {
                Session u2 = Server.currentUser.FirstOrDefault(z => z.accessToken == accesstoken);
                if (u2 == null)
                    return ControllerContext.Request.CreateResponse(HttpStatusCode.Forbidden, "you must login");
                user u = u2.user;
                switch (from)
                {
                    case ReturnType.favorite:
                        return selectServicesDataToReturn(u.favorite, u);

                    case ReturnType.me:
                        if (u.user_details.type == UserType.user.ToString())
                            return ControllerContext.Request.CreateResponse(HttpStatusCode.NotFound, "you are normal user, you can't have any service");

                        return selectServicesDataToReturn(u.services, u);

                    case ReturnType.friend:
                        user frnd = Server.database.users.FirstOrDefault(x => x.username == friend);
                        if (frnd == null)
                            return ControllerContext.Request.CreateResponse(HttpStatusCode.NotFound, "this person not found");
                        return selectServicesDataToReturn(frnd.services, u);

                    default:
                        return ControllerContext.Request.CreateResponse(HttpStatusCode.BadRequest, "bad request");
                }
            }
            catch (Exception ex)
            {
                Server.Loger(DateTime.Now + " - ServiceController - " + ex.Message);
                return ControllerContext.Request.CreateResponse(HttpStatusCode.InternalServerError, "Internal Error on server");
            }
        }
        private HttpResponseMessage selectServicesDataToReturn(ICollection<service> s, user usr)
        {
            return ControllerContext.Request.CreateResponse(HttpStatusCode.OK, s.Select(
                            z => new
                            {
                                serviceid = z.service_id,                                                         //<-------
                                servicename = z.name,                                                       //<-------
                                type = z.type,
                                laititude = z.laititude,
                                longitude = z.longitude,
                                phone = z.phone ?? "",
                                likecount = z.usersliked.Count,
                                url = z.url ?? "",
                                desc = z.description ?? "",
                                username = z.owner.username,
                                imgs = z.service_images.Select(q => q.image),
                                liked = usr != null && usr.favorite.Contains(z)
                            }));
        }
    }

    public class ServicesController: ApiController
    {
        public HttpResponseMessage Get(
            string accesstoken,
            int serviceid = -1,                                     //<--------------
            string servicename = "",                               //<--------------
            double x = 0.0,
            double y = 0.0,
            string desc = "",
            string phone = "",
            string url = "",
            ServiceType type = ServiceType.Other,
            Action action = Action.read)
        {
            try
            {
                Session u = Server.currentUser.FirstOrDefault(z => z.accessToken == accesstoken);
                if (u == null)
                    return ControllerContext.Request.CreateResponse(HttpStatusCode.Forbidden, "you must login");

                service s = Server.database.services.FirstOrDefault(z => z.service_id == serviceid);                                                 //<-----------------
                if (action != Action.create)
                    if (s == null)
                        return ControllerContext.Request.CreateResponse(HttpStatusCode.BadRequest, "service not found");

                switch (action)
                {
                    case Action.create:
                        if (u.user.user_details.type == UserType.user.ToString())
                            return ControllerContext.Request.CreateResponse(HttpStatusCode.Forbidden, "you not allowed create serivces");

                        if (Server.database.services.FirstOrDefault(z => z.name == servicename) != null)
                            return ControllerContext.Request.CreateResponse(HttpStatusCode.BadRequest, "used name, try different name");

                        if (string.IsNullOrWhiteSpace(servicename))
                            return ControllerContext.Request.CreateResponse(HttpStatusCode.BadRequest, "service name can't be empty");

                        if (type == ServiceType.Any)
                            return ControllerContext.Request.CreateResponse(HttpStatusCode.BadRequest, "you most select the Type of service");

                        Server.database.services.Add(new service()
                        {
                            name = servicename,
                            longitude = x,
                            laititude = y,
                            type = type.ToString(),
                            description = desc,
                            phone = phone,
                            url = url,
                            owner = u.user
                        });
                        break;
                    case Action.delete:
                        if (s.owner.username != u.user.username)
                            return ControllerContext.Request.CreateResponse(HttpStatusCode.Forbidden, "You not have Permission for this operation");

                        Server.database.services.Remove(s);
                        break;
                    case Action.edit:
                        if (s.owner.username != u.user.username)
                            return ControllerContext.Request.CreateResponse(HttpStatusCode.Forbidden, "you are not the owner of this service");

                        if (type == ServiceType.Any)
                            return ControllerContext.Request.CreateResponse(HttpStatusCode.BadRequest, "you most select the Type of service");

                        if (string.IsNullOrWhiteSpace(servicename))
                            return ControllerContext.Request.CreateResponse(HttpStatusCode.BadRequest, "service name can't be empty");

                        s.name = servicename;
                        s.longitude = x;
                        s.laititude = y;
                        s.type = type.ToString();
                        s.description = desc;
                        s.phone = phone;
                        s.url = url;

                        break;
                    case Action.read:
                        return ControllerContext.Request.CreateResponse(HttpStatusCode.OK,
                            new
                            {
                                serviceid = s.service_id,
                                servicename = s.name,
                                type = s.type,
                                laititude = s.laititude,
                                longitude = s.longitude,
                                phone = s.phone ?? "",
                                likecount = s.usersliked.Count,
                                url = s.url ?? "",
                                desc = s.description ?? "",
                                username = s.owner.username,
                                imgs = s.service_images.Select(q => q.image),
                                liked = u.user.favorite.Contains(s)
                            });

                    case Action.like:
                        if (u.user.favorite.Contains(s))
                            return ControllerContext.Request.CreateResponse(HttpStatusCode.BadRequest, "you liked this service already");
                        u.user.favorite.Add(s);

                        break;
                    case Action.unlike:
                        if (!u.user.favorite.Contains(s))
                            return ControllerContext.Request.CreateResponse(HttpStatusCode.BadRequest, "you did not like this service yet");
                        u.user.favorite.Remove(s);
                        break;
                    default:
                        return ControllerContext.Request.CreateResponse(HttpStatusCode.BadRequest, "Bad action");
                }
                Server.database.SaveChanges();
                return ControllerContext.Request.CreateResponse(HttpStatusCode.OK, "ok");
            }
            catch (Exception ex)
            {
                Server.Loger(DateTime.Now + " - ServiceController - " + ex.Message);
                return ControllerContext.Request.CreateResponse(HttpStatusCode.InternalServerError, "Internal Error on server");
            }
        }

    }

    public class OffersGetterController: ApiController
    {
        // please test this class later
        public enum ReturnType { like, myrequests, service, requestoffer, requestsOwner }
        public HttpResponseMessage Get(string accesstoken, int serviceid = -1, ReturnType from = ReturnType.like) // need test
        {
            try
            {
                Session u2 = Server.currentUser.FirstOrDefault(z => z.accessToken == accesstoken);
                if (u2 == null)
                    return ControllerContext.Request.CreateResponse(HttpStatusCode.Forbidden, "you must login");
                user u = u2.user;

                switch (from)
                {
                    case ReturnType.like: // return offers which user X liked it
                        return selectIoffersDataToReturn(u.offer_like, u);

                    case ReturnType.requestoffer: // return offers which user X requested it
                        return selectIoffersDataToReturn(u.requests.Select(z => z.offer), u);


                    case ReturnType.myrequests: // return REQUESTS of offers which user X requested it, to see response of his request
                        return ControllerContext.Request.CreateResponse(HttpStatusCode.OK,
                            u.requests.Select(z => new
                            {
                                offerid = z.offer_id,
                                offername = z.offer.name,
                                serviceid = z.offer.service_id,
                                servicename = z.offer.service.name,
                                username = z.user.username,
                                requsetMassage = z.requsetMassage ?? "",
                                requestDate = z.requestDate != null ? (z.requestDate.Value.Year + "/" + z.requestDate.Value.Month + "/" + z.requestDate.Value.Day) : "",

                                response = z.response ?? "Binding",
                                responseMassage = z.responseMassage ?? "",
                                responseDate = z.responseDate != null ? (z.responseDate.Value.Year + "/" + z.responseDate.Value.Month + "/" + z.responseDate.Value.Day) : "",
                                rating = z.rating ?? 0
                            }));

                    case ReturnType.requestsOwner: // return REQUESTS of offers of service which user X CREATED it to response of others requests
                        if (u.user_details.type != "service")
                            return ControllerContext.Request.CreateResponse(HttpStatusCode.Forbidden, "you are not service provider");


                        return ControllerContext.Request.CreateResponse(HttpStatusCode.OK,
                        //u.services.Select(z => z.offers).Select(q => q.Select(w =>w.requests.Select(
                        Server.database.requests.Where(x => x.offer.service.owner.username == u.username).ToArray().Select(
                                z => new
                                {
                                    offerid = z.offer_id,
                                    offername = z.offer.name,
                                    serviceid = z.offer.service_id,
                                    servicename = z.offer.service.name,
                                    username = z.user.username,
                                    requsetMassage = z.requsetMassage ?? "",
                                    requestDate = z.requestDate != null ? (z.requestDate.Value.Year + "/" + z.requestDate.Value.Month + "/" + z.requestDate.Value.Day):"",

                                    response = z.response ?? "Binding",
                                    responseMassage = z.responseMassage ?? "",
                                    responseDate = z.responseDate != null ? (z.responseDate.Value.Year + "/" + z.responseDate.Value.Month + "/" + z.responseDate.Value.Day) : "",
                                    rating = z.rating ?? 0
                                }));

                    case ReturnType.service:
                        service s = Server.database.services.FirstOrDefault(x => x.service_id == serviceid);
                        if (s == null)
                            return ControllerContext.Request.CreateResponse(HttpStatusCode.NotFound, "service not found");

                        return selectIoffersDataToReturn(s.offers, u);

                    default:
                        return ControllerContext.Request.CreateResponse(HttpStatusCode.BadRequest, "bad request");
                }
            }
            catch (Exception ex)
            {
                Server.Loger(DateTime.Now + " - OfferController - " + ex.Message);
                return ControllerContext.Request.CreateResponse(HttpStatusCode.InternalServerError, "Internal Error on server");
            }
        }
        public HttpResponseMessage selectIoffersDataToReturn(IEnumerable<offer> s, user usr)
        {

            return ControllerContext.Request.CreateResponse(HttpStatusCode.OK, s.Select(
                            z => new
                            {
                                offerid = z.offer_id,
                                offername = z.name,
                                serviceid = z.service.service_id,
                                servicename = z.service.name,
                                username = z.service.owner.username,
                                quantity = z.quantity ?? 0,
                                maxcost = z.max_cost ?? 0,
                                mixcost = z.min_cost ?? 0,
                                desc = z.description ?? "",
                                requestable = z.requestable ?? true,
                                imgs = z.offer_images.Select(q => q.image),
                                liked = usr != null && usr.offer_like.Contains(z),
                                likecount = z.usersliked.Count,
                                requestcount = z.requests.Count,
                                requested = usr != null && usr.requests.Select(r => r.offer_id).Contains(z.offer_id)
                            }));
        }

    }

    public class OffersController: ApiController
    {
        public HttpResponseMessage Get(
            string accesstoken,
            int offerid = -1,                                                                  //<-----------------
            string offername = "",                                                             //<-----------------
            int serviceid = -1,                                                                //<-----------------
            string clientname = "",
            string requsetMassage = "",
            string response = "",
            string responseMassage = "",
            string desc = "",
            int mincost = 0,
            int maxcost = 0,
            int quantity = 0,
            bool requestable = true,
            Action action = Action.read)
        {

            try
            {
                Session u = Server.currentUser.FirstOrDefault(z => z.accessToken == accesstoken);
                if (u == null)
                    return ControllerContext.Request.CreateResponse(HttpStatusCode.Forbidden, "you must login");

                service s = Server.database.services.FirstOrDefault(x => x.service_id == serviceid);                                 //<-----------------
                offer o = Server.database.offers.FirstOrDefault(x => x.offer_id == offerid);                                         //<-----------------

                if (action == Action.create || action == Action.edit || action == Action.delete || action == Action.response)
                {
                    if (u.user.user_details.type == UserType.user.ToString())
                        return ControllerContext.Request.CreateResponse(HttpStatusCode.Forbidden, "you not allowed manage serivces and offers");

                    if ((s != null && s.owner.username != u.user.username) || (o != null && o.service.owner.username != u.user.username))
                        return ControllerContext.Request.CreateResponse(HttpStatusCode.Forbidden, "you are not owner of this service");

                    if ((action == Action.create) && Server.database.offers.Select(z => z.name).Contains(offername))
                        return ControllerContext.Request.CreateResponse(HttpStatusCode.BadRequest, "used name, try different name");
                }

                if (action != Action.create && o == null)
                    return ControllerContext.Request.CreateResponse(HttpStatusCode.NotFound, "offer not found");


                switch (action)
                {
                    case Action.create:
                        if (s == null)
                            return ControllerContext.Request.CreateResponse(HttpStatusCode.NotFound, "service not found");

                        if (string.IsNullOrWhiteSpace(offername))
                            return ControllerContext.Request.CreateResponse(HttpStatusCode.BadRequest, "offer name can't be empty");

                        s.offers.Add(
                            new offer()
                            {
                                name = offername,
                                min_cost = mincost,
                                max_cost = maxcost,
                                quantity = quantity,
                                requestable = requestable,
                                description = desc,
                            });

                        break;
                    case Action.delete:
                        Server.database.offers.Remove(o); // no need to check permission here, we already do
                        break;
                    case Action.edit:
                        if (string.IsNullOrWhiteSpace(offername))
                            return ControllerContext.Request.CreateResponse(HttpStatusCode.BadRequest, "offer name can't be empty");

                        o.name = offername;
                        o.min_cost = mincost;
                        o.max_cost = maxcost;
                        o.quantity = quantity;
                        o.requestable = requestable;
                        o.description = desc;
                        break;
                    case Action.read:
                        return ControllerContext.Request.CreateResponse(HttpStatusCode.OK,
                            new
                            {
                                offerid = o.offer_id,
                                offername = o.name,
                                serviceid = o.service.service_id,
                                servicename = o.service.name,
                                username = o.service.owner.username,
                                quantity = o.quantity ?? 0,
                                maxcost = o.max_cost ?? 0,
                                mixcost = o.min_cost ?? 0,
                                desc = o.description ?? "",
                                requestable = o.requestable ?? true,
                                imgs = o.offer_images.Select(q => q.image),
                                liked = u.user.offer_like.Contains(o),
                                likecount = o.usersliked.Count,
                                requestcount = o.requests.Count,
                                requested = u.user != null && u.user.requests.Select(r => r.offer_id).Contains(o.offer_id)
                            });

                    case Action.like:
                        if (u.user.offer_like.Contains(o))
                            return ControllerContext.Request.CreateResponse(HttpStatusCode.BadRequest, "you liked this offer already");
                        u.user.offer_like.Add(o);
                        break;

                    case Action.unlike:
                        if (!u.user.offer_like.Contains(o))
                            return ControllerContext.Request.CreateResponse(HttpStatusCode.BadRequest, "you did not like this offer yet");
                        u.user.offer_like.Remove(o);
                        break;
                    case Action.request:
                        if (o.requests.FirstOrDefault(x => x.user.username == u.user.username) != null)
                            return ControllerContext.Request.CreateResponse(HttpStatusCode.BadRequest, "you requested this offer already");

                        if (o.requestable != null && o.requestable == false)
                            return ControllerContext.Request.CreateResponse(HttpStatusCode.Forbidden, "this offer not Requstable now, try later");

                        o.requests.Add(new request()
                        {
                            user = u.user,
                            requsetMassage = requsetMassage,
                            requestDate = DateTime.Now,
                        });
                        break;
                    case Action.unrequest:
                        {
                            request o2=o.requests.FirstOrDefault(x => x.user.username == u.user.username);
                            if (o2 == null)
                                return ControllerContext.Request.CreateResponse(HttpStatusCode.BadRequest, "you did not request this offer yet");
                            o.requests.Remove(o2);
                        }
                        break;
                    case Action.response:
                        {
                            request o2=o.requests.FirstOrDefault(x => x.user.username == clientname);
                            if (o2 == null)
                                return ControllerContext.Request.CreateResponse(HttpStatusCode.BadRequest, "this client did not request this offer");
                            if (string.IsNullOrWhiteSpace(response))
                                return ControllerContext.Request.CreateResponse(HttpStatusCode.BadRequest, "response can't be empty");

                            o2.response = response;
                            o2.responseMassage = responseMassage;
                            o2.responseDate = DateTime.Now;
                        }
                        break;

                    default:
                        return ControllerContext.Request.CreateResponse(HttpStatusCode.BadRequest, "bad action");
                }
                Server.database.SaveChanges();
                return ControllerContext.Request.CreateResponse(HttpStatusCode.OK, "ok");
            }
            catch (Exception ex)
            {
                Server.Loger(DateTime.Now + " - OfferController - " + ex.Message);
                return ControllerContext.Request.CreateResponse(HttpStatusCode.InternalServerError, "Internal Error on server");
            }
        }

    }


    public class FriendsController: ApiController
    {
        public enum ActionType { follow, unfollow, getFriends, getAccountInfo, getNews, getCount }
        public HttpResponseMessage Get(string accesstoken, string friend = "", int top = 10, ActionType action = ActionType.getNews)
        {
            try
            {
                Session u = Server.currentUser.FirstOrDefault(z => z.accessToken == accesstoken);
                if (u == null)
                    return ControllerContext.Request.CreateResponse(HttpStatusCode.Forbidden, "you must login");

                user usr = Server.database.users.FirstOrDefault(x => x.username == friend);

                switch (action)
                {
                    case ActionType.follow:
                        if (usr == null)
                            return ControllerContext.Request.CreateResponse(HttpStatusCode.NotFound, "user not found in database");
                        if (u.user.follows.FirstOrDefault(x => x.username == usr.username) != null)
                            return ControllerContext.Request.CreateResponse(HttpStatusCode.BadRequest, "you are following this guy already");

                        u.user.follows.Add(usr);
                        usr.followers.Add(u.user);
                        break;
                    case ActionType.unfollow:
                        if (usr == null)
                            return ControllerContext.Request.CreateResponse(HttpStatusCode.NotFound, "user not found in database");
                        user y = u.user.follows.FirstOrDefault(x => x.username == usr.username);
                        if (y == null)
                            return ControllerContext.Request.CreateResponse(HttpStatusCode.BadRequest, "you are not following this guy");

                        u.user.follows.Remove(y);
                        y.followers.Remove(u.user);
                        break;
                    case ActionType.getFriends:
                        return ControllerContext.Request.CreateResponse(HttpStatusCode.OK, u.user.follows.Select(
                            x => new
                            {
                                username = x.username,
                                name = x.user_details.name,
                                lastname = x.user_details.lastname ?? "",
                                type = x.user_details.type,
                                followed = u.user.follows.Contains(x)
                            }));

                    case ActionType.getAccountInfo:
                        if (usr == null)
                            return ControllerContext.Request.CreateResponse(HttpStatusCode.NotFound, "Account not found");
                        return ControllerContext.Request.CreateResponse(HttpStatusCode.OK, new
                        {
                            username = usr.username,
                            name = usr.user_details.name,
                            lastname = usr.user_details.lastname ?? "",
                            type = usr.user_details.type,
                            followed = u.user.follows.Contains(usr)
                        });

                    case ActionType.getNews:
                        //////////////////////////////////////////////////////////////////////////////////////////////////////////
                        //////////////////////////////////////////////////////////////////////////////////////////////////////////
                        //////////////////////////////////////////////////////////////////////////////////////////////////////////
                        //////////////////////////////////////////////////////////////////////////////////////////////////////////
                        return ControllerContext.Request.CreateResponse(HttpStatusCode.NotImplemented, "Not Implemented yet");

                    case ActionType.getCount:
                        if (usr == null)
                            return ControllerContext.Request.CreateResponse(HttpStatusCode.OK, new { follows = u.user.follows.Count, followers = u.user.followers.Count });
                        else
                            return ControllerContext.Request.CreateResponse(HttpStatusCode.OK, new { follows = usr.follows.Count, followers = usr.followers.Count });

                    default:
                        return ControllerContext.Request.CreateResponse(HttpStatusCode.BadRequest, "Bad action");
                }
                Server.database.SaveChanges();
                return ControllerContext.Request.CreateResponse(HttpStatusCode.OK, "ok");
            }
            catch (Exception ex)
            {
                Server.Loger(DateTime.Now + " - FriendController - " + ex.Message);
                return ControllerContext.Request.CreateResponse(HttpStatusCode.InternalServerError, "Internal Error on server");
            }
        }

    }





    public class SearchController: ApiController
    {
        public enum FROM { Services, Offers, Users }
        

        private HttpResponseMessage selectServicesDataToReturn(ICollection<service> s, user usr)
        {
            return ControllerContext.Request.CreateResponse(HttpStatusCode.OK, s.Select(
                            z => new
                            {
                                serviceid = z.service_id,
                                servicename = z.name,
                                type = z.type,
                                laititude = z.laititude,
                                longitude = z.longitude,
                                phone = z.phone ?? "",
                                likecount = z.usersliked.Count,
                                url = z.url ?? "",
                                desc = z.description ?? "",
                                username = z.owner.username,
                                imgs = z.service_images.Select(q => q.image),
                                liked = usr != null && usr.favorite.Contains(z)
                            }));
        }

        private HttpResponseMessage selectIoffersDataToReturn(IEnumerable<offer> s, user usr)
        {
            return ControllerContext.Request.CreateResponse(HttpStatusCode.OK, s.Select(
                            z => new
                            {
                                offerid = z.offer_id,
                                offername = z.name,
                                serviceid = z.service.service_id,
                                servicename = z.service.name,
                                username = z.service.owner.username,
                                quantity = z.quantity ?? 0,
                                maxcost = z.max_cost ?? 0,
                                mixcost = z.min_cost ?? 0,
                                desc = z.description ?? "",
                                requestable = z.requestable ?? true,
                                imgs = z.offer_images.Select(q => q.image),
                                liked = usr != null && usr.offer_like.Contains(z),
                                likecount = z.usersliked.Count,
                                requestcount = z.requests.Count,
                                requested = usr != null && usr.requests.Select(r => r.offer_id).Contains(z.offer_id)
                            }));
        }




        public HttpResponseMessage Get(string query, string accesstoken = "", double myLat = double.MaxValue, double myLon = double.MaxValue, double distance = double.MaxValue, FROM from = FROM.Services)
        {
            try
            {
                user usr = null;

                Session u = Server.currentUser.FirstOrDefault(z => z.accessToken == accesstoken);
                if (u != null)
                    usr = u.user;

                switch (from)
                {
                    case FROM.Services:
                        {
                            ICollection<service> result;
                            if (string.IsNullOrWhiteSpace(query))
                                result = Server.database.services.ToArray();
                            else
                                result = Server.database.services.Where(z => z.name.Contains(query)).ToArray();

                            if (result.Count == 0)
                                return ControllerContext.Request.CreateResponse(HttpStatusCode.NotFound, "nothing found");

                            if (myLat == double.MaxValue || myLon == double.MaxValue)
                                return selectServicesDataToReturn(result, usr);


                            if (distance < 0)
                                return ControllerContext.Request.CreateResponse(HttpStatusCode.BadRequest, "distance must by bigger then 0");

                            return selectServicesDataToReturn(
                                result.Where(z => ConvertCoordinatesToMeters(myLat, myLon, z.laititude ?? double.MaxValue, z.longitude ?? double.MaxValue) <= distance).ToArray(), usr);
                        }
                    case FROM.Offers:
                        {
                            ICollection<offer> result;
                            if (string.IsNullOrWhiteSpace(query))
                                result = Server.database.offers.ToArray();
                            else
                                result = Server.database.offers.Where(z => z.name.Contains(query)).ToArray();
                            if (result.Count == 0)
                                return ControllerContext.Request.CreateResponse(HttpStatusCode.NotFound, "nothing found");
                            return selectIoffersDataToReturn(result, usr);
                        }
                    case FROM.Users:
                        {
                            if (string.IsNullOrWhiteSpace(query))
                                return ControllerContext.Request.CreateResponse(HttpStatusCode.BadRequest, "the query can't be empty");

                            if (Server.database.users.Where(z => z.username.Contains(query) ||
                                z.user_details.name.Contains(query) || z.user_details.lastname.Contains(query)).Count() == 0)
                                return ControllerContext.Request.CreateResponse(HttpStatusCode.NotFound, "nothing found");

                            return ControllerContext.Request.CreateResponse(HttpStatusCode.OK,
                                Server.database.users.Where(z => z.username.Contains(query) || z.user_details.name.Contains(query)
                                    || z.user_details.lastname.Contains(query)).ToArray().Select(
                                z => new
                                {
                                    username = z.username,
                                    name = z.user_details.name,
                                    lastname = z.user_details.lastname ?? "",
                                    type = z.user_details.type,
                                    followed = usr != null && usr.follows.Contains(z)
                                }));
                        }
                    default:
                        return ControllerContext.Request.CreateResponse(HttpStatusCode.BadRequest, "Bad action");
                }
            }
            catch (Exception ex)
            {
                Server.Loger(DateTime.Now + " - SearchController - " + ex.Message);
                return ControllerContext.Request.CreateResponse(HttpStatusCode.InternalServerError, "internal server error");
            }
        }

        private double ConvertCoordinatesToMeters(double lat1, double lon1, double lat2, double lon2)
        {
            double R = 6378.137; // Radius of earth in KM
            double dLat = (lat2 - lat1) * Math.PI / 180;
            double dLon = (lon2 - lon1) * Math.PI / 180;
            double a = Math.Sin(dLat / 2) * Math.Sin(dLat / 2) + Math.Cos(lat1 * Math.PI / 180) * Math.Cos(lat2 * Math.PI / 180) * Math.Sin(dLon / 2) * Math.Sin(dLon / 2);
            double c = 2 * Math.Atan2(Math.Sqrt(a), Math.Sqrt(1 - a));
            double d = R * c;
            return d * 1000; // meters
        }
    }

}