package com.example.fitshare.Profile

import com.example.fitshare.User.User
import io.realm.RealmObject
import io.realm.RealmResults
import io.realm.annotations.LinkingObjects
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmField
import io.realm.annotations.Required
import org.bson.types.ObjectId

open class Profile (_fname: String = "First Name", _lname: String = "Last Name",
                    _bio: String = "Bio", _addr: String = "Address",
                    _zip: String = "Zipcode",_city: String = "City", _state: String = "State", _phone: String = "Phone Number",
                    _username: String = "Username" ,_meet: Boolean = false, _uid: String = "uid") : RealmObject(){
    @PrimaryKey @RealmField ("_id") var id: ObjectId = ObjectId()
    @Required
    var firstName: String = _fname
    @Required
    var lastName: String = _lname
    @Required
    var bio: String = _bio
    @Required
    var address: String = _addr
    @Required
    var zipcode: String = _zip
    @Required
    var phoneNumber: String = _phone
    @Required
    var username: String = _username
    @Required
    var state: String = _state
    @Required
    var city: String = _city
    var meetUp: Boolean = _meet
    @Required
   var userid: String = _uid
//
//    @LinkingObjects("profile")
//    val user: RealmResults<User>? = null

}