package com.example.fitshare.Recipe

import com.example.fitshare.User.User
import io.realm.RealmObject
import io.realm.RealmResults
import io.realm.annotations.LinkingObjects
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmField
import io.realm.annotations.Required
import org.bson.types.ObjectId


open class Recipe(
    _name: String = "Recipe", _desc: String = "Description",
    _ingr: String = "Ingredients", _steps: String = "Steps",
    _time: String = "Prep_Time", _userId: String = "UserID",_cal: String = "Calories",
    _totalRating: Double = 0.0, _numsRating: Int = 0) : RealmObject() {
    @PrimaryKey @RealmField ("_id") var id: ObjectId = ObjectId()
    @Required
    var recipeName: String = _name
    @Required
    var description: String = _desc
    @Required
    var ingredients: String = _ingr
    @Required
    var steps: String = _steps
    @Required
    var prepTime: String = _time
    @Required
    var calories: String = _cal
    @LinkingObjects("recipes")
    val user: RealmResults<User>? = null
    var user_id: String = _userId
    var totalRating: Double = _totalRating
    var numsRating: Int = _numsRating
}

/*
open class Recipe(
    @PrimaryKey @RealmField("_id") var id: ObjectId = ObjectId(),
    @Required
    var name: String = "",
    @Required
    var description: String = "",
    @Required
    var ingredients: String = "",
    @Required
    var steps: String = "",
    @LinkingObjects("recipes")
    val user: RealmResults<User>? = null

    constructor(name: String?, description: String?,
                ingredients: String?, steps: String?){
        this.name = name
        this.description = description
        this.ingredients = ingredients
        this.steps = steps
    }

    constructor(){}
): RealmObject()
*/
