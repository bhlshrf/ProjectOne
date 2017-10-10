using System;
using System.Collections.Generic;
using System.Collections.Specialized;
using System.IO;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Text;
using System.Web;
using System.Web.Http;

namespace MvcApplication3.Controllers
{
    public class PushController : ApiController
    {
        public void Get()
        {
            new PushNotification().Android("5grDMrPboQIz0Fpyojo-_u2", "myapplication@gmail.com", "myapppassword", "Testing Testing");
        }

        public HttpResponseMessage Get(string Accesstoken, string id)
        {
            Session s = Server.currentUser.FirstOrDefault(x => x.accessToken == Accesstoken);
            if (s == null)
                return ControllerContext.Request.CreateResponse(HttpStatusCode.Forbidden, "you must login");

            //
            // to-do later : save id to user info
            //


            return ControllerContext.Request.CreateResponse(HttpStatusCode.OK, "ok");
        }
    }

    public class PushNotification
    {
        public string Android(string RegistrationID, string SenderID, string Password, string Message)
        {
            string Status = "";
            //--Validating the required parameter--//
            if (CheckAndroidValidation(RegistrationID, SenderID, Password, Message) == false)
                Status = "Provided parameter missing the currect value.";
            else
            {
                //-- Check Authentication --//
                Android objAndroid = new Android();
                string AuthString = objAndroid.CheckAuthentication(SenderID, Password);
                Status = (AuthString == "Fail") ? "Authentication Fail" : objAndroid.SendMessage(RegistrationID, Message, AuthString);
            }
            //-- Return the Status of Push Notification --//
            return Status;
        }

        /// Check Parameter Validation for Android 
        private bool CheckAndroidValidation(string RegistrationID, string SenderID, string Password, string Message)
        {
            bool RetValue = true;
            if (RegistrationID.Trim() == "") RetValue = false;
            if (SenderID.Trim() == "") RetValue = false;
            if (Password.Trim() == "") RetValue = false;
            if (Message.Trim() == "") RetValue = false;
            return RetValue;
        }
    }

    public class Android
    {
        /// Check authentication with supplied credential
        /// <param name="SenderID">Google EmailID</param>
        /// <param name="Password">Password of EmailID</param>
        public string CheckAuthentication(string SenderID, string Password)
        {
            string Array = "";
            string URL = "https://www.google.com/accounts/ClientLogin?";
            string fullURL = URL + "Email=" + SenderID.Trim() + "&Passwd=" + Password.Trim() + "&accountType=GOOGLE" + "&source=Company-App-Version" + "&service=ac2dm";
            HttpWebRequest Request = (HttpWebRequest)HttpWebRequest.Create(fullURL);

            try
            {
                //-- Post Authentication URL --//
                HttpWebResponse Response = (HttpWebResponse)Request.GetResponse();
                StreamReader Reader;
                int Index = 0;

                //-- Check Response Status --//
                if (Response.StatusCode == HttpStatusCode.OK)
                {
                    Stream Stream = Response.GetResponseStream();
                    Reader = new StreamReader(Stream);
                    string File = Reader.ReadToEnd();

                    Reader.Close();
                    Stream.Close();
                    Index = File.ToString().IndexOf("Auth=") + 5;
                    int len = File.Length - Index;
                    Array = File.ToString().Substring(Index, len);
                }
            }
            catch (Exception ex)
            {
                Array = ex.Message;
                ex = null;
            }
            return Array;
        }

        /// Send Push Message to Device
        /// <param name="RegistrationID">RegistrationID or Token</param>
        /// <param name="Message">Message to be sent on device</param>
        /// <param name="AuthString">Authentication string</param>
        public string SendMessage(string RegistrationID, string Message, string AuthString)
        {
            //-- Create C2DM Web Request Object --//
            HttpWebRequest Request = (HttpWebRequest)WebRequest.Create("https://android.clients.google.com/c2dm/send");
            Request.Method = "POST";
            Request.KeepAlive = false;

            //-- Create Query String --//
            NameValueCollection postFieldNameValue = new NameValueCollection();
            postFieldNameValue.Add("registration_id", RegistrationID);
            postFieldNameValue.Add("collapse_key", "1");
            postFieldNameValue.Add("delay_while_idle", "0");
            // postFieldNameValue.Add("data.message", Message);
            postFieldNameValue.Add("data.payload", Message);
            string postData = GetPostStringFrom(postFieldNameValue);
            byte[] byteArray = Encoding.UTF8.GetBytes(postData);

            Request.ContentType = "application/x-www-form-urlencoded;charset=UTF-8";
            Request.ContentLength = byteArray.Length;

            Request.Headers.Add(HttpRequestHeader.Authorization, "GoogleLogin auth=" + AuthString);

            //-- Delegate Modeling to Validate Server Certificate --//
            ServicePointManager.ServerCertificateValidationCallback += delegate(
                        object
                        sender,
                        System.Security.Cryptography.X509Certificates.X509Certificate
                        pCertificate,
                        System.Security.Cryptography.X509Certificates.X509Chain pChain,
                        System.Net.Security.SslPolicyErrors pSSLPolicyErrors)
            {
                return true;
            };

            //-- Create Stream to Write Byte Array --// 
            Stream dataStream = Request.GetRequestStream();
            dataStream.Write(byteArray, 0, byteArray.Length);
            dataStream.Close();

            //-- Post a Message --//
            WebResponse Response = Request.GetResponse();
            HttpStatusCode ResponseCode = ((HttpWebResponse)Response).StatusCode;
            if (ResponseCode.Equals(HttpStatusCode.Unauthorized) || ResponseCode.Equals(HttpStatusCode.Forbidden))
                return "Unauthorized - need new token";
            else if (!ResponseCode.Equals(HttpStatusCode.OK))
            {
                return "Response from web service isn't OK";
                //Console.WriteLine("Response from web service not OK :");
                //Console.WriteLine(((HttpWebResponse)Response).StatusDescription);
            }

            StreamReader Reader = new StreamReader(Response.GetResponseStream());
            string responseLine = Reader.ReadLine();
            Reader.Close();

            return responseLine;
        }

        /// Create Query String From Name Value Pair
        private string GetPostStringFrom(NameValueCollection postFieldNameValue)
        {
            //throw new NotImplementedException();
            List<string> items = new List<string>();

            foreach (String name in postFieldNameValue)
                items.Add(String.Concat(name, "=", System.Web.HttpUtility.UrlEncode(postFieldNameValue[name])));

            return String.Join("&", items.ToArray());
        }
    }
}



/*


using System;
using System.Collections.Generic;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;
using System.Net;
using System.Text;
using System.IO;
using System.Collections;
using System.Net.Security;
using System.Security.Cryptography.X509Certificates;
using System.Configuration;
using System.Data.SqlClient;
using System.Collections.Specialized;
using System.Diagnostics;
using System.Configuration;
using System.Web.Script.Serialization;
using PushNotification.Entities;
namespace PushNotification
{
    public class AndroidCommunicationService
    {

        public bool AcceptAllCertifications(object sender, System.Security.Cryptography.X509Certificates.X509Certificate certification, System.Security.Cryptography.X509Certificates.X509Chain chain, System.Net.Security.SslPolicyErrors sslPolicyErrors)
        {
            return true;
        }

        public string GetAuthenticationCode()
        {
            string returnUrl = "";
            string URL = "https://accounts.google.com/o/oauth2/auth";
            NameValueCollection postFieldNameValue = new NameValueCollection();
            postFieldNameValue.Add("response_type", "code");
            postFieldNameValue.Add("scope", "https://android.apis.google.com/c2dm/send");
            postFieldNameValue.Add("client_id", ConfigurationManager.AppSettings["ClientId"].ToString());
            postFieldNameValue.Add("redirect_uri", "http://localhost:8080/TestServer/test");
            postFieldNameValue.Add("state", "profile");
            postFieldNameValue.Add("access_type", "offline");
            postFieldNameValue.Add("approval_prompt", "auto");
            postFieldNameValue.Add("additional_param", DateTime.Now.Ticks.ToString());
            string postData = GetPostStringFrom(postFieldNameValue);
            byte[] byteArray = Encoding.UTF8.GetBytes(postData);

            HttpWebRequest Request = (HttpWebRequest)WebRequest.Create(URL);
            Request.Method = "POST";
            Request.KeepAlive = false;

            Request.ContentType = "application/x-www-form-urlencoded;charset=UTF-8";
            Request.ContentLength = byteArray.Length;

            ServicePointManager.ServerCertificateValidationCallback = new System.Net.Security.RemoteCertificateValidationCallback(AcceptAllCertifications);
            Stream dataStream = Request.GetRequestStream();
            // Write the data to the request stream.
            dataStream.Write(byteArray, 0, byteArray.Length);
            // Close the Stream object.
            dataStream.Close();

            Request.Method = "POST";

            try
            {
                WebResponse Response = Request.GetResponse();

                var sr = new StreamReader(Response.GetResponseStream());
               // You can do this and return the content on the screen ( I am using MVC )
                returnUrl = sr.ReadToEnd();
              // Or 
                returnUrl = Response.RedirectUri.ToString();
            }
            catch
            {
                throw ;
            }
            return returnUrl;
        }
        public TokenResponse GetNewToken(string Code)
        {
            TokenResponse tokenResponse = new TokenResponse();
            string URL = ConfigurationManager.AppSettings["TokenCodeUrl"];
            NameValueCollection postFieldNameValue = new NameValueCollection();
            postFieldNameValue.Add("code", Code);
            postFieldNameValue.Add("client_id", ConfigurationManager.AppSettings["ClientId"]);
            postFieldNameValue.Add("client_secret", ConfigurationManager.AppSettings["ClientSecret"]);
            postFieldNameValue.Add("redirect_uri", ConfigurationManager.AppSettings["RedirectUrl"]);
            postFieldNameValue.Add("grant_type", "authorization_code");

            string postData = GetPostStringFrom(postFieldNameValue);
            byte[] byteArray = Encoding.UTF8.GetBytes(postData);

            HttpWebRequest Request = (HttpWebRequest)WebRequest.Create(URL);
            Request.Method = "POST";
            Request.KeepAlive = false;

            Request.ContentType = "application/x-www-form-urlencoded;charset=UTF-8";
            Request.ContentLength = byteArray.Length;

            ServicePointManager.ServerCertificateValidationCallback = new System.Net.Security.RemoteCertificateValidationCallback(AcceptAllCertifications);
            Stream dataStream = Request.GetRequestStream();
            // Write the data to the request stream.
            dataStream.Write(byteArray, 0, byteArray.Length);
            // Close the Stream object.
            dataStream.Close();

            Request.Method = "POST";

            try
            {
                WebResponse Response = Request.GetResponse();
                var tokenStreamRead = new StreamReader(Response.GetResponseStream());
                JavaScriptSerializer js = new JavaScriptSerializer();
                var objText = tokenStreamRead.ReadToEnd();
                 tokenResponse = (TokenResponse)js.Deserialize(objText, typeof(TokenResponse));            
            }
            catch (WebException wex)
            {
                var sr = new StreamReader(wex.Response.GetResponseStream());

                Exception ex = new WebException(sr.ReadToEnd() + wex.Message);
                throw ex;
            }

            return tokenResponse;
        }

        public TokenResponse RefreshToken(string RefreshToken)
        {
            TokenResponse tokenResponse = new TokenResponse();
            string URL = ConfigurationManager.AppSettings["TokenCodeUrl"]; 
            NameValueCollection postFieldNameValue = new NameValueCollection();
            postFieldNameValue.Add("refresh_token", RefreshToken);
            postFieldNameValue.Add("client_id", ConfigurationManager.AppSettings["ClientId"]);
            postFieldNameValue.Add("client_secret", ConfigurationManager.AppSettings["ClientSecret"]);
            postFieldNameValue.Add("grant_type", "refresh_token");

            string postData = GetPostStringFrom(postFieldNameValue);
            byte[] byteArray = Encoding.UTF8.GetBytes(postData);

            HttpWebRequest Request = (HttpWebRequest)WebRequest.Create(URL);
            Request.Method = "POST";
            Request.KeepAlive = false;

            Request.ContentType = "application/x-www-form-urlencoded;charset=UTF-8";
            Request.ContentLength = byteArray.Length;

            ServicePointManager.ServerCertificateValidationCallback = new System.Net.Security.RemoteCertificateValidationCallback(AcceptAllCertifications);
            Stream dataStream = Request.GetRequestStream();
            // Write the data to the request stream.
            dataStream.Write(byteArray, 0, byteArray.Length);
            // Close the Stream object.
            dataStream.Close();

            Request.Method = "POST";

            try
            {
                WebResponse Response = Request.GetResponse();
                var tokenStreamRead = new StreamReader(Response.GetResponseStream());
                JavaScriptSerializer js = new JavaScriptSerializer();
                var objText = tokenStreamRead.ReadToEnd();
                tokenResponse = (TokenResponse)js.Deserialize(objText, typeof(TokenResponse));
            }
            catch 
            {
                throw;
            }

            return tokenResponse;
        }

        public string SendPushMessage(string RegistrationId, string Message ,string AuthString)
        {
            HttpWebRequest Request = (HttpWebRequest)WebRequest.Create(ConfigurationManager.AppSettings["PushMessageUrl"]);
            Request.Method = "POST";
            Request.KeepAlive = false;

            //-- Create Query String --//
            NameValueCollection postFieldNameValue = new NameValueCollection();
            postFieldNameValue.Add("registration_id", RegistrationId);
            postFieldNameValue.Add("collapse_key", "fav_Message");
            postFieldNameValue.Add("data.message", Message);
            postFieldNameValue.Add("additional_value", DateTime.Now.Ticks.ToString());

            string postData = GetPostStringFrom(postFieldNameValue);
            byte[] byteArray = Encoding.UTF8.GetBytes(postData);


            Request.ContentType = "application/x-www-form-urlencoded;charset=UTF-8";
            Request.ContentLength = byteArray.Length;


// This is to be sent as a header and not as a param .
            Request.Headers.Add(HttpRequestHeader.Authorization, "Bearer " + AuthString);





            try
            {
                //-- Create Stream to Write Byte Array --// 
                Stream dataStream = Request.GetRequestStream();
                dataStream.Write(byteArray, 0, byteArray.Length);
                dataStream.Close();

                ServicePointManager.ServerCertificateValidationCallback = new System.Net.Security.RemoteCertificateValidationCallback(AcceptAllCertifications);
                WebResponse Response = Request.GetResponse();
                HttpStatusCode ResponseCode = ((HttpWebResponse)Response).StatusCode;

                if (ResponseCode.Equals(HttpStatusCode.Unauthorized) || ResponseCode.Equals(HttpStatusCode.Forbidden))
                {
                    return "Unauthorized - need new token";
                }
                else if (!ResponseCode.Equals(HttpStatusCode.OK))
                {
                    return "Response from web service isn't OK";

                }

                StreamReader Reader = new StreamReader(Response.GetResponseStream());
                string responseLine = Reader.ReadLine();
                Reader.Close();

                return responseLine;

            }
            catch 
            {

                throw;
            }


        }

        private string GetPostStringFrom(NameValueCollection postFieldNameValue)
        {
            //throw new NotImplementedException();
            List<string> items = new List<string>();

            foreach (String name in postFieldNameValue)
                items.Add(String.Concat(name, "=", System.Web.HttpUtility.UrlEncode(postFieldNameValue[name])));

            return String.Join("&", items.ToArray());
        }
        private static bool ValidateRemoteCertificate(object sender, X509Certificate certificate, X509Chain chain, SslPolicyErrors policyErrors)
        {
            return true;
        }

    }
}
public class TokenResponse
    {
        public String access_token{ get; set; }

        public String expires_in { get; set; }

        public String token_type { get; set; }

        public String refresh_token { get; set; }

    }
*/