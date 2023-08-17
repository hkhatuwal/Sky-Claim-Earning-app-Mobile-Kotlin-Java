package com.sgamerapps.android.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.sgamerapps.android.activity.MainActivity
import com.sgamerapps.android.adapters.PlansRecyclerViewAdapter
import com.sgamerapps.android.api.ApiClient
import com.sgamerapps.android.config.AppConfig
import com.sgamerapps.android.data.RedeemPlan
import com.sgamerapps.android.databinding.FragmentProfileBinding
import com.sgamerapps.android.utils.DialogHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileFragment : Fragment() {


    lateinit var binding: FragmentProfileBinding

    lateinit var plansAdapter:PlansRecyclerViewAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater)


        updateProfileUi()

//        Recycler View settup
        binding.plansRecyclerView.layoutManager=LinearLayoutManager(requireContext())
        binding.plansRecyclerView.setHasFixedSize(true)
        getPlanList()

        return binding.root
    }


     fun  updateProfileUi(){
        var user=AppConfig.getCurrentUser(true)
        binding.nameTv.text=user!!.name
        binding.emailTv.text=user!!.email
    }


    private fun  getPlanList(){

        DialogHelper.showLoading(requireContext())
        ApiClient.getInstance().getRedeemPlans()!!.enqueue(object :Callback<List<RedeemPlan>>{
            override fun onResponse(
                call: Call<List<RedeemPlan>>,
                response: Response<List<RedeemPlan>>
            ) {
                DialogHelper.hideLoading()
                plansAdapter= PlansRecyclerViewAdapter(response.body()!!,activity as MainActivity)
                binding.plansRecyclerView.adapter=plansAdapter

            }

            override fun onFailure(call: Call<List<RedeemPlan>>, t: Throwable) {
                DialogHelper.hideLoading()
                if(t.message!!.contains("internet")){
                     if(context!=null){
                        DialogHelper.noInternetDialog(context!!)

                    }
                }
            }

        })
    }


}