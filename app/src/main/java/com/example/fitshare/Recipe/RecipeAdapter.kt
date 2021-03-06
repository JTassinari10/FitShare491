package com.example.fitshare.Recipe

import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.fitshare.R
import com.example.fitshare.User.User
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter
import io.realm.RealmResults
import io.realm.kotlin.where
import io.realm.mongodb.sync.SyncConfiguration
import org.bson.types.ObjectId
import androidx.appcompat.app.AppCompatActivity




/*
* TaskAdapter: extends the Realm-provided RealmRecyclerViewAdapter to provide data for a RecyclerView to display
* Realm objects on screen to a user.
*/
class RecipeAdapter(data: OrderedRealmCollection<Recipe>,
                    val user: io.realm.mongodb.User,
                    private val partition: String
                    ) : RealmRecyclerViewAdapter<Recipe, RecipeAdapter.RecipeViewHolder?>
    (data, true) {

    private lateinit var mListener: onItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): RecipeViewHolder {
        val itemView: View = LayoutInflater.from(parent.context).
        inflate(R.layout.layout_recipe, parent, false)
        return RecipeViewHolder(itemView, mListener)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val obj: Recipe? = getItem(position)
        holder.data = obj
        holder.recipeName.text = obj?.recipeName
        holder.description.text = obj?.description
        holder.prepTime.text = obj?.prepTime + " hrs"

    }

    inner class RecipeViewHolder(view: View, listener: onItemClickListener) : RecyclerView.ViewHolder(view) {
        var recipeName: TextView = view.findViewById(R.id.tvRecipeName)
        var description: TextView = view.findViewById(R.id.tvDescription)
        var prepTime: TextView = view.findViewById(R.id.tvPrepTime)
        var data: Recipe? = null

        init{
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }


    interface onItemClickListener{
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener){
        mListener = listener
    }
}
