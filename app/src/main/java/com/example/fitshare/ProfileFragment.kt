package com.example.fitshare

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.fitshare.Profile.Profile
import com.example.fitshare.Profile.ProfileEditButton
import com.example.fitshare.User.User
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.realm.Realm
import io.realm.mongodb.sync.SyncConfiguration

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters

    private var user: io.realm.mongodb.User? = null
    private lateinit var profileRealm: Realm
    private lateinit var userRealm: Realm
    //private lateinit var adapter: ProfileAdapter
    private lateinit var fab: FloatingActionButton
    private lateinit var partition: String
    private lateinit var meetUp: CheckBox
    private lateinit var otherProfileButton: Button
    private lateinit var profileName : TextView
    private lateinit var username : TextView
    private lateinit var phone : TextView
    private lateinit var address : TextView
    private lateinit var bio : TextView
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_profile, container, false)
        user = fitApp.currentUser()
        partition = "Profile"
        val config = SyncConfiguration.Builder(user!!, partition).build()

        //Profile Realm sync config
        Realm.getInstanceAsync(config, object: Realm.Callback(){
            override fun onSuccess(realm: Realm) {
                this@ProfileFragment.profileRealm = realm

                val oldProf = profileRealm.where(Profile::class.java).
                    equalTo("userid", user?.id.toString()).findFirst()

                if(oldProf?.meetUp == true){
                        meetUp.isChecked = true
                    }else{meetUp.isChecked = false}

                profileName = view.findViewById(R.id.profileName)
                username = view.findViewById(R.id.username)
                phone = view.findViewById(R.id.phone)
                address = view.findViewById(R.id.address)
                bio = view.findViewById(R.id.bio)

                username.setText(oldProf?.username)
                profileName.setText(oldProf?.firstName + ", " + oldProf?.lastName)
                phone.setText(oldProf?.phoneNumber)
                address.setText(oldProf?.address + ", " + oldProf?.zipcode)
                bio.setText(oldProf?.bio)
                }

        })

        //User Realm sync config
        val user_config: SyncConfiguration =
            SyncConfiguration.Builder(user!!, "user=${user!!.id}")
                .build()
        Realm.getInstanceAsync(user_config, object: Realm.Callback(){
            override fun onSuccess(realm: Realm) {
                this@ProfileFragment.userRealm = realm
            }
        })


        //Meet-up status functionality
        meetUp = view.findViewById(R.id.meetUp)
        meetUp.setOnClickListener{

            //Check box functionality
            profileRealm.executeTransactionAsync{
                val oldProf = it.where(Profile::class.java).
                equalTo("userid", user?.id.toString()).findFirst()
                if(meetUp.isChecked()){
                        oldProf?.meetUp = true
                    }
                else if(!meetUp.isChecked()){
                        oldProf?.meetUp = false
                    }
            }
        }

        //Button for adding/editing a profile
        fab = view.findViewById(R.id.btnEditProfile)
        fab.setOnClickListener{
            val editProfileButton : ProfileEditButton = ProfileEditButton.newInstance()
            editProfileButton.show(parentFragmentManager, null)
        }

        //Test Viewing Other Profile Activity **DELETE LATER**
        otherProfileButton = view.findViewById(R.id.other_profile_button)
        otherProfileButton.setOnClickListener{

            profileRealm.executeTransactionAsync{

                val oldProf = it.where(Profile::class.java).
                equalTo("userid", user?.id.toString()).findFirst()

                val profUserId = oldProf?.userid.toString()


                var otherProfileFragment : Fragment = OtherProfileFragment()
                val bundle = Bundle()
                bundle.putString("profileUserId", profUserId)
                otherProfileFragment.arguments = bundle
                requireActivity().supportFragmentManager.beginTransaction().replace(R.id.frameLayout, otherProfileFragment).commit()
            }
        }

        return view
    }

    override fun onDestroy() {
        super.onDestroy()

        userRealm.close()
        profileRealm.close()
    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}