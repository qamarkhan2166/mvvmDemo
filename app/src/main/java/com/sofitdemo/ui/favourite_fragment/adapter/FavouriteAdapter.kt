package com.sofitdemo.ui.favourite_fragment.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sofitdemo.R
import com.sofitdemo.data.local.db.nosql.dao.FavouriteDrinksDb
import com.sofitdemo.data.remote.responce.drinks.DrinksModel
import com.sofitdemo.databinding.RvDrinksDesignBinding

class FavouriteAdapter(
    private var modelList: List<FavouriteDrinksDb>,
    private var context: Context
    //private val favDrinks:FavDrink
) : RecyclerView.Adapter<FavouriteAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            RvDrinksDesignBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(modelList[position])
    }

    override fun getItemCount() = modelList.size

    inner class ViewHolder(val binding: RvDrinksDesignBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun onBind(data: FavouriteDrinksDb) {
            binding.apply {
                Glide.with(context)
                    .load(data.strDrinkThumb)
                    .placeholder(android.R.drawable.progress_indeterminate_horizontal) // need placeholder to avoid issue like glide annotations
                    .error(android.R.drawable.stat_notify_error)
                    .into(cameraImage)
                txtDrinkName.text=  data.strDrink
                drinksDescription.text = data.strInstructions
                checkbox.isChecked = data.strAlcoholic == "Alcoholic"
                imgFav.setImageResource(R.drawable.ic_baseline_star_rate_24)
            }
        }
    }


}