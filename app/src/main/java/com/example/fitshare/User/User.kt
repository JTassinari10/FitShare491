package com.example.fitshare.User

import com.example.fitshare.Recipe.Recipe
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmField
import org.bson.types.ObjectId


open class User(
    @PrimaryKey @RealmField("_id") var id: String = "",
    var _partition: String = "",
    //var recipes: RealmList<Recipe> ?= null,
    var name: String = ""
): RealmObject()