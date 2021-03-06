package com.example.fitshare.Recipe

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import io.realm.Realm
import io.realm.kotlin.where
import io.realm.mongodb.sync.SyncConfiguration
import kotlinx.android.synthetic.main.fragment_recipe.*
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.RecyclerView
import com.example.fitshare.Helper.MyButton
import com.example.fitshare.Helper.MySwipeHelper
import com.example.fitshare.Listener.MyButtonClickListener
import com.example.fitshare.MainActivity
import com.example.fitshare.R
import com.example.fitshare.fitApp
import io.realm.Case
import kotlinx.android.synthetic.main.layout_add_recipe.*
import java.util.*

class RecipeFragment : Fragment(){
    private lateinit var recipeRealm: Realm
    private lateinit var userRealm: Realm
    private var user: io.realm.mongodb.User? = null
    private lateinit var adapter: RecipeAdapter
    private lateinit var partition: String
    private lateinit var searchview: SearchView
    private lateinit var myRecipe: AppCompatButton
    private lateinit var allRecipe: AppCompatButton
    //private lateinit var deleteRecipe: Recipe?
    private var removeNavBar = View.VISIBLE


    override fun onResume() {
        super.onResume()
        if (activity is MainActivity) {
            var  mainActivity = activity as MainActivity
            mainActivity.setBottomNavigationVisibility(removeNavBar)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_recipe, container, false)
        user = fitApp.currentUser()
        partition = "recipe"
        val config = SyncConfiguration.Builder(user!!, partition)
            .build()

        Realm.getInstanceAsync(config, object: Realm.Callback() {
            override fun onSuccess(realm: Realm) {
                // since this realm should live exactly as long as this activity, assign the realm to a member variable
                this@RecipeFragment.recipeRealm = realm
                rvRecipe.layoutManager =
                    LinearLayoutManager(requireActivity().applicationContext)
                rvRecipe.setHasFixedSize(true)
                adapter = RecipeAdapter(recipeRealm.where<Recipe>().sort("recipeName").findAll(), user!!, partition)
                rvRecipe.adapter = adapter

                adapter.setOnItemClickListener(object : RecipeAdapter.onItemClickListener{

                    override fun onItemClick(position: Int) {

                        var detailsFragment: Fragment = RecipeDetailsFragment()
                        val bundle = Bundle()
                        bundle.putString("recipeID", adapter.getItem(position)?.id.toString())
                        bundle.putString("recipeName", adapter.getItem(position)?.recipeName)
                        detailsFragment.arguments = bundle
                        requireActivity().supportFragmentManager.beginTransaction()
                            .replace(R.id.frameLayout, detailsFragment, "RecipeDetails")
                            .addToBackStack("RecipeDetails")
                            .commit()
                    }

                })

            }
        })

        val user_config : SyncConfiguration =
            SyncConfiguration.Builder(user!!, "user=${user!!.id}")
                .build()

        Realm.getInstanceAsync(user_config, object: Realm.Callback() {
            override fun onSuccess(realm: Realm) {
                // since this realm should live exactly as long as this activity, assign the realm to a member variable
                this@RecipeFragment.userRealm = realm
            }
        })

        searchview = view.findViewById(R.id.searchView)
        searchview.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(text: String): Boolean {
                recyclerSearch(recipeRealm, user, partition, text)
                return false
            }
            override fun onQueryTextChange(text: String): Boolean {
                recyclerSearch(recipeRealm, user, partition, text)
                return false
            }
        })


        myRecipe = view.findViewById(R.id.myRecipeFilter)
        myRecipe.setOnClickListener {
            adapter = RecipeAdapter(recipeRealm.where<Recipe>().contains("user_id", user?.id.toString())
                .findAll(), user!!, partition)
            rvRecipe.layoutManager = LinearLayoutManager(requireActivity().applicationContext)
            rvRecipe.adapter = adapter
            rvRecipe.setHasFixedSize(true)
            adapter.setOnItemClickListener(object : RecipeAdapter.onItemClickListener{
                override fun onItemClick(position: Int) {
                    var detailsFragment: Fragment = RecipeDetailsFragment()
                    val bundle = Bundle()
                    bundle.putString("recipeID", adapter.getItem(position)?.id.toString())
                    bundle.putString("recipeName", adapter.getItem(position)?.recipeName)
                    detailsFragment.arguments = bundle
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, detailsFragment, "RecipeDetails")
                        .addToBackStack("RecipeDetails")
                        .commit()
                }

            })


//            val swipe = object : MySwipeHelper(requireActivity().applicationContext, rvRecipe, 500){
//                override fun instantiateMyButton(
//                    viewHolder: RecyclerView.ViewHolder,
//                    buffer: MutableList<MyButton>
//                ) {
//                    buffer.add(MyButton(requireActivity().applicationContext,
//                        "Delete",
//                        50,
//                        0,
//                        Color.parseColor("#FF3C30"),
//                        object: MyButtonClickListener {
//                            override fun onClick(pos: Int) {
//                                recipeRealm.executeTransactionAsync{
//                                    val deleteRecipe = it.where(Recipe::class.java).
//                                    equalTo("_id", adapter.getItem(pos)?.id).findFirst()
//                                    Log.i("test", deleteRecipe?.id.toString())
//                                }

                                    //adapter.getItem(pos)?.deleteFromRealm()
//                                    deleteRecipe?.deleteFromRealm()
//                                    //adapter.notifyDataSetChanged()
//
//                            }
//                        }
//                    ))
//                }
//            }
        }

        allRecipe = view.findViewById(R.id.allFilter)
        allRecipe.setOnClickListener {
            rvRecipe.layoutManager =
                LinearLayoutManager(requireActivity().applicationContext)
            rvRecipe.setHasFixedSize(true)
            adapter = RecipeAdapter(recipeRealm.where<Recipe>().sort("recipeName").findAll(), user!!, partition)
            rvRecipe.adapter = adapter
            adapter.setOnItemClickListener(object : RecipeAdapter.onItemClickListener{
                override fun onItemClick(position: Int) {
                    var detailsFragment: Fragment = RecipeDetailsFragment()
                    val bundle = Bundle()
                    bundle.putString("recipeID", adapter.getItem(position)?.id.toString())
                    bundle.putString("recipeName", adapter.getItem(position)?.recipeName)
                    detailsFragment.arguments = bundle
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, detailsFragment, "RecipeDetails")
                        .addToBackStack("RecipeDetails")
                        .commit()
                }

            })
        }
        return view
    }

    private fun recyclerSearch(realm: Realm, user: io.realm.mongodb.User?, partition: String, text: String){
        adapter = RecipeAdapter(realm.where<Recipe>().contains("recipeName", text, Case.INSENSITIVE)
            .findAll(), user!!, partition)
        rvRecipe.layoutManager = LinearLayoutManager(requireActivity().applicationContext)
        rvRecipe.adapter = adapter
        rvRecipe.setHasFixedSize(true)
        adapter.setOnItemClickListener(object : RecipeAdapter.onItemClickListener{
            override fun onItemClick(position: Int) {
                var detailsFragment: Fragment = RecipeDetailsFragment()
                val bundle = Bundle()
                bundle.putString("recipeID", adapter.getItem(position)?.id.toString())
                bundle.putString("recipeName", adapter.getItem(position)?.recipeName)
                detailsFragment.arguments = bundle
                requireActivity().supportFragmentManager.beginTransaction().replace(R.id.frameLayout,
                    detailsFragment).commit()
            }

        })
    }


    override fun onDestroy() {
        super.onDestroy()
        userRealm.close()
        recipeRealm.close()
    }

}

