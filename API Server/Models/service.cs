//------------------------------------------------------------------------------
// <auto-generated>
//    This code was generated from a template.
//
//    Manual changes to this file may cause unexpected behavior in your application.
//    Manual changes to this file will be overwritten if the code is regenerated.
// </auto-generated>
//------------------------------------------------------------------------------

namespace MvcApplication3.Models
{
    using System;
    using System.Collections.Generic;
    
    public partial class service
    {
        public service()
        {
            this.offers = new HashSet<offer>();
            this.usersliked = new HashSet<user>();
            this.service_images = new HashSet<service_images>();
        }
    
        public int service_id { get; set; }
        public string name { get; set; }
        public string type { get; set; }
        public Nullable<double> longitude { get; set; }
        public Nullable<double> laititude { get; set; }
        public string description { get; set; }
        public string phone { get; set; }
        public int user_id { get; set; }
        public string url { get; set; }
        public Nullable<byte> @class { get; set; }
    
        public virtual ICollection<offer> offers { get; set; }
        public virtual user owner { get; set; }
        public virtual ICollection<user> usersliked { get; set; }
        public virtual ICollection<service_images> service_images { get; set; }
    }
}
