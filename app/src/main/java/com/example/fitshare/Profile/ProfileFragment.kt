package com.example.fitshare.Profile


import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import com.example.fitshare.Feeds.CommentFragment
import com.example.fitshare.MainActivity
import com.example.fitshare.MapsActivity
import com.example.fitshare.MessageForum.ForumPostFragment
import com.example.fitshare.R
import com.example.fitshare.User.UserLocation
import com.example.fitshare.fitApp
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import io.realm.Realm
import io.realm.mongodb.sync.SyncConfiguration


class ProfileFragment : Fragment() {

    private var user: io.realm.mongodb.User? = null
    private lateinit var profileRealm: Realm
    private lateinit var mapRealm: Realm
    private lateinit var partition: String
    private lateinit var meetUp: SwitchCompat
    private lateinit var messageBtn: ImageButton
    private lateinit var btnLocation: ImageButton
    private lateinit var profileDetails : TextView

    private lateinit var fullName : TextView
    private lateinit var username : TextView
    private lateinit var phone : TextView
    private lateinit var address : TextView
    private lateinit var bio : TextView
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
        val view: View = inflater.inflate(R.layout.fragment_profile, container, false)

        user = fitApp.currentUser()
        partition = "location"
        val map_config = SyncConfiguration.Builder(user!!, partition)
            .build()

        Realm.getInstanceAsync(map_config, object : Realm.Callback() {
            override fun onSuccess(realm: Realm) {
                this@ProfileFragment.mapRealm = realm
            }
        })
        partition = "Profile"
        val config = SyncConfiguration.Builder(user!!, partition).build()

        //Profile Realm sync config
        Realm.getInstanceAsync(config, object: Realm.Callback(){
            override fun onSuccess(realm: Realm) {
                this@ProfileFragment.profileRealm = realm

                //Find the profile of a user
                val oldProf = profileRealm.where(Profile::class.java).
                equalTo("userid", user?.id.toString()).findFirst()

                //Set the status of the meet-up and location buttons
                if(oldProf?.meetUp == true){
                    meetUp.isChecked = true
                    btnLocation.isClickable = true
                }else{
                    meetUp.isChecked = false
                    btnLocation.isClickable = false
                }

                username = view.findViewById(R.id.tvUsername)
                fullName = view.findViewById(R.id.txtFullName)
                phone = view.findViewById(R.id.txtPhone)
                address = view.findViewById(R.id.txtAddress)
                bio = view.findViewById(R.id.txtBio)

                if(oldProf == null){
                    username.setText("Username")
                    fullName.setText("First Name, Last Name")
                    phone.setText("Phone Number")
                    address.setText("Address, City, State, Zipcode")
                    bio.setText("My Bio")
                }else{
                    username.setText(oldProf?.username.toString())
                    fullName.setText(oldProf?.firstName.toString() + ", " + oldProf?.lastName.toString())
//                    phone.setText(oldProf?.phoneNumber?.get(0)?.toString() + oldProf?.phoneNumber?.get(1)?.toString() + oldProf?.phoneNumber?.get(2)?.toString()
//                                    + "-" + oldProf?.phoneNumber?.get(3)?.toString() + oldProf?.phoneNumber?.get(4)?.toString() + oldProf?.phoneNumber?.get(5)?.toString()
//                                +"-"+ oldProf?.phoneNumber?.get(6)?.toString() + oldProf?.phoneNumber?.get(7)?.toString() + oldProf?.phoneNumber?.get(8)?.toString()
//                                + oldProf?.phoneNumber?.get(9)?.toString())
                    phone.setText(oldProf?.phoneNumber?.toString())
                    address.setText(oldProf?.address.toString() + ", " + oldProf?.city.toString()
                            +", "+ oldProf?.state.toString() +", " + oldProf?.zipcode.toString())
                    bio.setText(oldProf?.bio.toString())
                }
            }
        })

        //Add google map functionality here
        btnLocation = view.findViewById(R.id.btnLocation)
        btnLocation.setOnClickListener{
            val intent = Intent(this@ProfileFragment.context, MapsActivity::class.java);
            startActivity(intent)
        }

        //Meet-up status functionality
        meetUp = view.findViewById(R.id.switchMeetUp)
        meetUp.setOnClickListener{

        //Check box functionality
        if(meetUp.isChecked()){
                profileRealm.executeTransactionAsync{
                    val oldProf = it.where(Profile::class.java).
                    equalTo("userid", user?.id.toString()).findFirst()
                    oldProf?.meetUp = true
                }
            btnLocation.isClickable = true
        }
        else if(!meetUp.isChecked()){
            profileRealm.executeTransactionAsync{
                val oldProf = it.where(Profile::class.java).
                equalTo("userid", user?.id.toString()).findFirst()
                oldProf?.meetUp = false
            }
            mapRealm.executeTransactionAsync{
                val locationList = it.where(UserLocation::class.java).equalTo("userID", user?.id.toString()).findAll()
                locationList.deleteAllFromRealm()
                }
            btnLocation.isClickable = false
            }
        }

        messageBtn = view.findViewById(R.id.btnChat)
        messageBtn.setOnClickListener{
            var forumFragment : Fragment = ForumPostFragment()
            val bundle = Bundle()
            bundle.putString("userID", user?.id.toString())
            forumFragment.arguments = bundle
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, forumFragment).addToBackStack(null).commit()
        }


        //Profile Details Fragment
        profileDetails = view.findViewById(R.id.linkProfileDetails)
        profileDetails.setOnClickListener {
            var profileDetailsFragment: Fragment = ProfileDetailsFragment()
//            val bundle = Bundle()
//            bundle.putString("postID", adapter.getItem(position)?.id.toString())
//            commentFragment.arguments = bundle
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, profileDetailsFragment, "profileDetails")
                .addToBackStack("profileDetails")
                .commit()
        }
        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        profileRealm.close()
    }
}