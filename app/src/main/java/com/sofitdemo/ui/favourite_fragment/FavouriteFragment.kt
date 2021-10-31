package com.sofitdemo.ui.favourite_fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.sofitdemo.data.local.db.nosql.dao.FavouriteDrinksDb
import com.sofitdemo.data.remote.responce.drinks.DrinksModel
import com.sofitdemo.databinding.ActivitySplashBinding
import com.sofitdemo.databinding.FragmentFavouriteBinding
import com.sofitdemo.ui.favourite_fragment.adapter.FavouriteAdapter
import com.sofitdemo.ui.home_fragment.adapter.DrinksAdapter
import com.sofitdemo.ui.mainclass.MainViewModel
import com.sofitdemo.ui.splash.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavouriteFragment : Fragment() {

    private lateinit var binding: FragmentFavouriteBinding
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavouriteBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getFavouriteDrinks()?.let { setUpRecyclerView(it) }
    }

    private fun setUpRecyclerView(modelList: List<FavouriteDrinksDb>) {
        val myAdapter = FavouriteAdapter(modelList, requireContext())
        val linearLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewFav.setHasFixedSize(true)
        binding.recyclerViewFav.layoutManager = linearLayoutManager
        binding.recyclerViewFav.adapter = myAdapter
    }
}