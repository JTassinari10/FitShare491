package com.example.fitshare.MessageForum

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitshare.Feeds.Comment
import com.example.fitshare.MainActivity
import com.example.fitshare.Profile.Profile
import com.example.fitshare.R
import com.example.fitshare.fitApp
import com.google.android.material.textfield.TextInputLayout
import io.realm.Realm
import io.realm.kotlin.where
import io.realm.mongodb.sync.SyncConfiguration
import kotlinx.android.synthetic.main.forum_comment_fragment.*
import kotlinx.android.synthetic.main.forum_fragment.*
import kotlinx.android.synthetic.main.forum_fragment.rvPost
import kotlinx.android.synthetic.main.fragment_comment.*
import org.bson.types.ObjectId
import java.text.SimpleDateFormat
import java.util.*

class ForumCommentFragment : Fragment(){
    private lateinit var recyclerView: RecyclerView
    private var user: io.realm.mongodb.User ?= null
    private lateinit var partition: String
    private lateinit var username: String
    private lateinit var commentAdapter: ForumCommentAdapter
    private lateinit var commentRealm: Realm
    private lateinit var postRealm: Realm
    private lateinit var profileRealm: Realm
    private lateinit var sendButton: Button
    private lateinit var commentLayout: TextInputLayout
    private var removeNavBar = View.GONE

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        if (activity is MainActivity){
            var mainActivity = activity as MainActivity
            mainActivity.setBottomNavigationVisibility(removeNavBar)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState : Bundle?
    ): View?{
        val view: View = inflater.inflate(R.layout.forum_comment_fragment, container, false)
        user = fitApp.currentUser()
        partition = arguments?.getString("postID").toString()
        recyclerView = view.findViewById(R.id.rvComment)

        val config = SyncConfiguration.Builder(user!!, partition).build()

        Realm.getInstanceAsync(config, object: Realm.Callback(){
            override fun onSuccess(realm: Realm){
                this@ForumCommentFragment.commentRealm = realm

                var title = arguments?.getString("title")
                var content = arguments?.getString("content")
                var creator = arguments?.getString("creator")

                var tvTitle: TextView = view.findViewById(R.id.commentTitle)
                var tvContent: TextView = view.findViewById(R.id.tvContent)
                var tvCreator: TextView = view.findViewById(R.id.tvCreator)


                tvTitle.setText(title)
                tvContent.setText(content)
                tvCreator.setText("By " + creator)

                commentAdapter = ForumCommentAdapter(commentRealm.where<ForumComment>().findAll(), user!!, partition)
                rvComment.layoutManager = LinearLayoutManager(requireActivity().applicationContext)
                rvComment.adapter = commentAdapter

                rvComment.setHasFixedSize(true)
            }
        })

//        val user_config : SyncConfiguration =
//            SyncConfiguration.Builder(user!!, "user=${user!!.id}")
//                .build()
//
//        Realm.getInstanceAsync(user_config, object: Realm.Callback() {
//            override fun onSuccess(realm: Realm) {
//                // since this realm should live exactly as long as this activity, assign the realm to a member variable
//                this@ForumCommentFragment.postRealm = realm
//            }
//        })

        val profile_config = SyncConfiguration.Builder(user!!, "Profile").build()

        //Profile Realm sync config
        Realm.getInstanceAsync(profile_config, object: Realm.Callback() {
            override fun onSuccess(realm: Realm) {
                this@ForumCommentFragment.profileRealm = realm
                //Find the profile of a user
                val oldProf =
                    profileRealm.where(Profile::class.java).equalTo("userid", user?.id.toString())
                        .findFirst()
                username = oldProf?.username.toString()
            }
        })

        commentLayout = view.findViewById(R.id.enter_message)
        commentLayout.setEndIconOnClickListener{
            val sdf = SimpleDateFormat("M/dd/yyyy")
            val date = sdf.format(Date())
            var creator = username

            //Create the ForumComment object
            val comment = ForumComment(ObjectId(), txtMsg.text.toString(), creator, date.toString())

            commentRealm.executeTransactionAsync{
                it.insert(comment)
            }
            txtMsg.text?.clear()
            commentAdapter.notifyDataSetChanged()
        }

        return view
    }

    override fun onDestroy(){
        super.onDestroy()
//        postRealm.close()
        commentRealm.close()
        profileRealm.close()
    }
}