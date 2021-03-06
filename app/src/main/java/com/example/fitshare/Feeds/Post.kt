package com.example.fitshare.Feeds

import com.example.fitshare.Profile.Profile
import com.example.fitshare.User.User
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmField
import io.realm.annotations.Required
import io.realm.mongodb.UserProfile
import org.bson.types.ObjectId
import java.util.*

open class Post: RealmObject{
    @PrimaryKey @RealmField ("_id") var id: ObjectId = ObjectId()
    @Required
    var content: String = ""
    var likesList: RealmList<User> ?= null
    @Required
    var date: Date = Date()
    @Required
    var username: String ?= null
    var profileID: ObjectId ?= null
//    var userID: String = ""


    constructor(
        content: String,
        profileID: ObjectId?,
        username: String?
//        userID: String
    ) {
        this.content = content
        this.profileID = profileID
        this.username = username
//        this.userID = userID
    }

    constructor() {} // RealmObject subclasses must provide an empty constructor
}