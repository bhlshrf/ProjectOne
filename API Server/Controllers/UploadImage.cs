using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Net.Http.Headers;
using System.Threading.Tasks;
using System.Web;
using System.Web.Hosting;
using System.Web.Http;
using MvcApplication3.Models;

namespace MvcApplication3.Controllers
{
    // source : http://hintdesk.com/android-upload-files-to-asp-net-web-api-service/

    public class ImageController: ApiController
    {
        private const string UploadFolder = "imgs";

        public HttpResponseMessage Get(string fileName, string accesstoken = "", string action = "get")
        {
            HttpResponseMessage result = null;

            if (action == "get")
            {
                DirectoryInfo directoryInfo = new DirectoryInfo(HostingEnvironment.MapPath("~/" + UploadFolder));
                FileInfo foundFileInfo = directoryInfo.GetFiles().Where(x => x.Name == fileName).FirstOrDefault();
                if (foundFileInfo != null)
                {
                    FileStream fs = new FileStream(foundFileInfo.FullName, FileMode.Open);

                    result = new HttpResponseMessage(HttpStatusCode.OK);
                    result.Content = new StreamContent(fs);
                    result.Content.Headers.ContentType = new System.Net.Http.Headers.MediaTypeHeaderValue("application/octet-stream");
                    result.Content.Headers.ContentDisposition = new ContentDispositionHeaderValue("attachment");
                    result.Content.Headers.ContentDisposition.FileName = foundFileInfo.Name;
                    return result;
                }
                else
                    return ControllerContext.Request.CreateResponse(HttpStatusCode.NotFound, "file not found");
            }
            else if (action == "delete")
            {
                Session s = Server.currentUser.FirstOrDefault(x => x.accessToken == accesstoken);
                if (s == null)
                    return ControllerContext.Request.CreateResponse(HttpStatusCode.Forbidden, "you must login");

                service sr = Server.database.services.FirstOrDefault(z => z.service_images.FirstOrDefault(zz => zz.image == fileName) != null);
                offer o = Server.database.offers.FirstOrDefault(z => z.offer_images.FirstOrDefault(zz => zz.image == fileName) != null);
                if (sr == null && o == null)
                    return ControllerContext.Request.CreateResponse(HttpStatusCode.NotFound, "service or offer image not found");
                if (sr != null)
                {
                    if (sr.owner.username != s.user.username)
                        return ControllerContext.Request.CreateResponse(HttpStatusCode.Forbidden, "you are not owner of this image");

                    sr.service_images.Remove(sr.service_images.FirstOrDefault(zz => zz.image == fileName));
                }
                else
                {
                    if (o.service.owner.username != s.user.username)
                        return ControllerContext.Request.CreateResponse(HttpStatusCode.Forbidden, "you are not owner of this image");

                    o.offer_images.Remove(o.offer_images.FirstOrDefault(zz => zz.image == fileName));
                }

                FileInfo foundFileInfo = new DirectoryInfo(HostingEnvironment.MapPath("~/" + UploadFolder)).GetFiles().Where(x => x.Name == fileName).FirstOrDefault();
                if (foundFileInfo == null)
                    return ControllerContext.Request.CreateResponse(HttpStatusCode.NotFound, "file not found");
                File.Delete(foundFileInfo.FullName);

                Server.database.SaveChanges();
            }
            return ControllerContext.Request.CreateResponse(HttpStatusCode.OK, "ok");
        }

        public Task<IQueryable<HDFile>> Post([FromUri]string accesstoken, [FromUri] string forname, [FromUri] bool forService = true)
        {

            Session u2 = Server.currentUser.FirstOrDefault(z => z.accessToken == accesstoken);
            if (u2 == null)
                throw new HttpResponseException(Request.CreateResponse(HttpStatusCode.Forbidden, "you must login"));
            var u = u2.user;

            var uploadFolderPath = HostingEnvironment.MapPath("~/" + UploadFolder);
            if (Request.Content.IsMimeMultipartContent())
            {
                var streamProvider = new WithExtensionMultipartFormDataStreamProvider(uploadFolderPath);
                var task = Request.Content.ReadAsMultipartAsync(streamProvider).ContinueWith<IQueryable<HDFile>>(t =>
                {
                    if (t.IsFaulted || t.IsCanceled)
                        throw new HttpResponseException(HttpStatusCode.InternalServerError);

                    var fileInfo = streamProvider.FileData.Select(i =>
                    {
                        var info = new FileInfo(i.LocalFileName);

                        //
                        // to do later : check user permission
                        if (forService)
                        {
                            var s=Server.database.services.FirstOrDefault(z => z.name == forname);
                            if (s == null)
                                throw new HttpResponseException(Request.CreateResponse(HttpStatusCode.NotFound, "Service not found"));
                            if (s.owner.username != u.username)
                                throw new HttpResponseException(Request.CreateResponse(HttpStatusCode.Forbidden, "you are not owner of this service"));
                            s.service_images.Add(new service_images() { image = info.Name });

                            Server.database.SaveChanges();
                        }
                        else
                        {
                            var s=Server.database.offers.FirstOrDefault(z => z.name == forname);
                            if (s == null)
                                throw new HttpResponseException(Request.CreateResponse(HttpStatusCode.NotFound, "Offer not found"));
                            if (s.service.owner.username != u.username)
                                throw new HttpResponseException(Request.CreateResponse(HttpStatusCode.Forbidden, "you are not owner of this service"));

                            s.offer_images.Add(new offer_images() { image = info.Name });

                            Server.database.SaveChanges();
                        }

                        return new HDFile(info.Name, Request.RequestUri.AbsoluteUri + "?filename=" + info.Name, (info.Length / 1024).ToString());
                    });
                    return fileInfo.AsQueryable();
                });

                return task;
            }
            else
                throw new HttpResponseException(Request.CreateResponse(HttpStatusCode.NotAcceptable, "This request is not properly formatted"));

        }
    }

    #region helper

    public class WithExtensionMultipartFormDataStreamProvider: MultipartFormDataStreamProvider
    {
        public WithExtensionMultipartFormDataStreamProvider(string rootPath) : base(rootPath) { }
        public override string GetLocalFileName(System.Net.Http.Headers.HttpContentHeaders headers)
        {
            string extension = !string.IsNullOrWhiteSpace(headers.ContentDisposition.FileName) ? Path.GetExtension(OSUtil.GetValidFileName(headers.ContentDisposition.FileName)) : "";
            return Guid.NewGuid().ToString() + extension;
        }
    }
    public class OSUtil
    {
        public static string GetValidFileName(string filePath)
        {
            char[] invalids = System.IO.Path.GetInvalidFileNameChars();
            return String.Join("_", filePath.Split(invalids, StringSplitOptions.RemoveEmptyEntries)).TrimEnd('.');
        }
    }
    public class HDFile
    {
        public HDFile(string name, string url, string size)
        {
            Name = name;
            Url = url;
            Size = size;
        }
        public string Name { get; set; }
        public string Url { get; set; }
        public string Size { get; set; }
    }

    #endregion
}