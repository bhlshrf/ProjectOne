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
    
    public partial class user
    {
        public user()
        {
            this.services = new HashSet<service>();
            this.favorite = new HashSet<service>();
            this.follows = new HashSet<user>();
            this.followers = new HashSet<user>();
            this.offer_like = new HashSet<offer>();
            this.requests = new HashSet<request>();
        }
    
        public int user_id { get; set; }
        public string username { get; set; }
        public string password { get; set; }
    
        public virtual user_details user_details { get; set; }
        public virtual ICollection<service> services { get; set; }
        public virtual ICollection<service> favorite { get; set; }
        public virtual ICollection<user> follows { get; set; }
        public virtual ICollection<user> followers { get; set; }
        public virtual ICollection<offer> offer_like { get; set; }
        public virtual ICollection<request> requests { get; set; }
    }
}
