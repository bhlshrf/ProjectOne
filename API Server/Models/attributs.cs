using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using Newtonsoft.Json;
using System.ComponentModel.DataAnnotations;

namespace MvcApplication3.Models
{
    [MetadataType(typeof(userMetadata))]
    public partial class user 
    {

    }
    public class userMetadata
    {
        [JsonIgnore]
        public int user_id { get; set; }
        public string username { get; set; }
        [JsonIgnore]
        public string password { get; set; }

        public virtual user_details user_details { get; set; }
        [JsonIgnore]
        public virtual ICollection<service> services { get; set; }
        [JsonIgnore]
        public virtual ICollection<request> requests { get; set; }
        [JsonIgnore]
        public virtual ICollection<service> favorite { get; set; }
        [JsonIgnore]
        public virtual ICollection<user> follows { get; set; }
        [JsonIgnore]
        public virtual ICollection<user> followers { get; set; }
        [JsonIgnore]
        public virtual ICollection<offer> offer_like { get; set; }
    }

    [MetadataType(typeof(user_detailsMetadata))]
    public partial class user_details
    {

    }
    internal sealed class user_detailsMetadata
    {
        [JsonIgnore]
        public int user_id { get; set; }
        public string type { get; set; }
        public string name { get; set; }
        public string lastname { get; set; }
        public Nullable<System.DateTime> birthdate { get; set; }

        public user user { get; set; }
    }


    [MetadataType(typeof(offerMetadata))]
    public partial class offer
    {

    }
    public class offerMetadata
    {
        [JsonIgnore]
        public int offer_id { get; set; }
        [JsonIgnore]
        public int service_id { get; set; }
        public string name { get; set; }
        public Nullable<int> min_cost { get; set; }
        public Nullable<int> max_cost { get; set; }
        public Nullable<int> quantity { get; set; }
        public string description { get; set; }
        public Nullable<bool> requestable { get; set; }

        public virtual service service { get; set; }
        [JsonIgnore]
        public virtual ICollection<user> usersliked { get; set; }
        [JsonIgnore]
        public virtual ICollection<request> requests { get; set; }
        [JsonIgnore]
        public virtual ICollection<offer_images> offer_images { get; set; }
    }

    [MetadataType(typeof(offer_imagesMetadata))]
    public partial class offer_images
    {

    }
    public class offer_imagesMetadata
    {
        [JsonIgnore]
        public int id { get; set; }
        [JsonIgnore]
        public int offer_id { get; set; }
        
        public string image { get; set; }

        [JsonIgnore]
        public offer offer { get; set; }
    }


    [MetadataType(typeof(serviceMetadata))]
    public partial class service
    {

    }
    public class serviceMetadata
    {
        [JsonIgnore]
        public int service_id { get; set; }
        public string name { get; set; }
        public string type { get; set; }
        public Nullable<double> longitude { get; set; }
        public Nullable<double> laititude { get; set; }
        public string description { get; set; }
        public string phone { get; set; }
        [JsonIgnore]
        public int user_id { get; set; }
        public string url { get; set; }
        public Nullable<byte> @class { get; set; }

        
        public virtual ICollection<offer> offers { get; set; }
        public virtual user owner { get; set; }
        [JsonIgnore]
        public virtual ICollection<user> usersliked { get; set; }
        public virtual ICollection<service_images> service_images { get; set; }
    }

    [MetadataType(typeof(service_imagesMetadata))]
    public partial class service_images
    {

    }

    public class service_imagesMetadata
    {
        [JsonIgnore]
        public int id { get; set; }
        [JsonIgnore]
        public int service_id { get; set; }
        public string image { get; set; }

        [JsonIgnore]
        public service service { get; set; }
    }
}