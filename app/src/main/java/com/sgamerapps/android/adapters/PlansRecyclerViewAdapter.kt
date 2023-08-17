package com.sgamerapps.android.adapters

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.sgamerapps.android.R
import com.sgamerapps.android.activity.MainActivity
import com.sgamerapps.android.api.ApiClient
import com.sgamerapps.android.config.AppConfig
import com.sgamerapps.android.data.RedeemPlan
import com.sgamerapps.android.data.User
import com.sgamerapps.android.utils.DialogHelper
import com.sgamerapps.android.utils.PreferenceManager
import retrofit2.Call
import retrofit2.Response

class PlansRecyclerViewAdapter(var plans: List<RedeemPlan>,var  activity: Activity) :
    RecyclerView.Adapter<PlansRecyclerViewAdapter.PlanViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanViewHolder {

        var view =
            LayoutInflater.from(parent.context).inflate(R.layout.plan_item_layout, parent, false)
        return PlanViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlanViewHolder, position: Int) {
        var plan = plans[position]
        holder.coinsTv.text = plan.coins.toString()
        holder.coinsValueTv.text = "${plan.value} Rs"


        if(plan.active){
            holder.inactiveTv.visibility=GONE
        }
        else{
            holder.inactiveTv.visibility= VISIBLE


        }

        holder.itemView.setOnClickListener {
            if(!plan.active){
                Toast.makeText(holder.itemView.context, "Your Daily Instant Limit is Over Please Try Again After 12 Hours", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            var user = AppConfig.getCurrentUser()
            if (user!!.wallet!! < plan.coins) {
                Toast.makeText(
                    holder.itemView.context,
                    "You dont have enough coins",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                showWithdrawDialog(holder.itemView.context, plan.id)
            }
        }

    }

    override fun getItemCount(): Int {
        return plans.size
    }


    private fun sendWithdrawRequest(context: Context, planId: Int, phone: String) {
        DialogHelper.showLoading(context)
        var data = HashMap<String, String>()
        data["plan_id"] = planId.toString()
        data["phone"] = phone
        ApiClient.getInstance().redeem(data)!!.enqueue(object : retrofit2.Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                DialogHelper.hideLoading()

            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                response.body().let {
                    DialogHelper.hideLoading()
                    if (it != null) {
                        if (it.get("success").asBoolean) {
                            PreferenceManager.saveUser(Gson().fromJson(it.get("data").toString(), User::class.java))
                            var mainActivity=activity as MainActivity
                            mainActivity.updateWallet()

                            Toast.makeText(context, it.get("message").asString, Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            Toast.makeText(context, it.get("message").asString, Toast.LENGTH_LONG)
                                .show()

                        }
                    } else {
                        Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()

                    }
                }
            }

        })
    }


    private fun showWithdrawDialog(context: Context, planId: Int) {

        var builder = AlertDialog.Builder(context);
        var dialog:AlertDialog?=null
        var view = LayoutInflater.from(context).inflate(R.layout.withdraw_dialog, null)

        builder.setView(view)

        var phoneEt = view.findViewById<EditText>(R.id.phoneEt)
        var withdrawBtn = view.findViewById<Button>(R.id.withdrawButton)



        withdrawBtn.setOnClickListener {
                dialog!!.dismiss()
            var phone = phoneEt.text.toString()
            sendWithdrawRequest(context, planId, phone)
        }


        dialog=  builder.create()
        dialog.show()


    }


    class PlanViewHolder(view: View) : RecyclerView.ViewHolder(view) {
         var coinsTv = view.findViewById<TextView>(R.id.coinsTv)
         var coinsValueTv = view.findViewById<TextView>(R.id.coinsValueTv)
         var inactiveTv = view.findViewById<TextView>(R.id.inactiveTv)


    }
}