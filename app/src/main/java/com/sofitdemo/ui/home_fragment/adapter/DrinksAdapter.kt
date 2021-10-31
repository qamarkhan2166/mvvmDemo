package com.sofitdemo.ui.home_fragment.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sofitdemo.R
import com.sofitdemo.data.remote.responce.drinks.DrinksModel
import com.sofitdemo.databinding.RvDrinksDesignBinding

class DrinksAdapter(
    private var modelList: List<DrinksModel>,
    private var context: Context,
    private val saveDrinks:SaveDrink
) : RecyclerView.Adapter<DrinksAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            RvDrinksDesignBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(modelList[position])
    }

    override fun getItemCount() = modelList.size

    interface SaveDrink {
        fun onClickSave(data: DrinksModel)
        fun onClickDel(data: DrinksModel)
    }
    inner class ViewHolder(val binding: RvDrinksDesignBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun onBind(data: DrinksModel) {
            binding.apply {
                Glide.with(context)
                    .load(data.strDrinkThumb)
                    .placeholder(android.R.drawable.progress_indeterminate_horizontal) // need placeholder to avoid issue like glide annotations
                    .error(android.R.drawable.stat_notify_error)
                    .into(cameraImage)
                txtDrinkName.text=  data.strDrink
                drinksDescription.text = data.strInstructions
                checkbox.isChecked = data.strAlcoholic == "Alcoholic"
                imgFav.setOnClickListener {
                    imgFav.setImageResource(R.drawable.ic_baseline_star_rate_24)
                    saveDrinks.onClickSave(data)
                    /*if (!isSave){
                        imgFav.setImageResource(R.drawable.ic_baseline_star_rate_24)
                        saveDrinks.onClickSave(data)
                        isSave = true
                    }else if (isSave){
                        imgFav.setImageResource(R.drawable.ic_baseline_star_outline_24)
                        saveDrinks.onClickDel(data)
                        isSave = false
                    }*/
                }
            }
        }
    }


}