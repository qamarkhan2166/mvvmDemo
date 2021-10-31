package com.sofitdemo.ui.home_fragment

import android.app.ProgressDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.sofitdemo.data.local.db.nosql.dao.FavouriteDrinksDb
import com.sofitdemo.data.remote.responce.drinks.DrinksModel
import com.sofitdemo.databinding.FragmentHomeBinding
import com.sofitdemo.ui.home_fragment.adapter.DrinksAdapter
import com.sofitdemo.ui.mainclass.MainViewModel
import com.sofitdemo.utils.Resource
import com.sofitdemo.utils.Utilities
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.util.*


@AndroidEntryPoint
class HomeFragment : Fragment(), DrinksAdapter.SaveDrink {

    private lateinit var binding: FragmentHomeBinding
    private val mainActViewModel: MainViewModel by activityViewModels()
    private var progressDialog: ProgressDialog? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressDialog = ProgressDialog(requireContext())
        progressDialog!!.setMessage("Please wait...")
        progressDialog!!.setCancelable(false)
        binding.apply {
            if (mainActViewModel.getButtonState() == "1") {
                nameRb.isChecked = true
            } else {
                alphabetRb.isChecked = true
            }

            searchEdt.editText?.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (!s.isNullOrEmpty()) {
                        mainActViewModel.searchWord(s.toString())
                        when {
                            nameRb.isChecked -> {
                                mainActViewModel.saveButtonState("1")
                                progressBar.visibility = View.VISIBLE
                                getDrinksKeyword(s.toString())
                            }
                            alphabetRb.isChecked -> {
                                mainActViewModel.saveButtonState("0")
                                progressBar.visibility = View.VISIBLE
                                getDrinksAlphabets(s.toString())
                                Utilities.hideKeyboard(requireActivity())
                            }
                            !nameRb.isChecked && !alphabetRb.isChecked -> {
                                searchEdt.error = "Please Check Radio Button"
                            }
                        }
                    }
                }
            })

            searchEdt.editText?.setText(mainActViewModel.getSearchWord())
            getDrinksKeyword(mainActViewModel.getSearchWord())
            progressBar.visibility = View.VISIBLE
            searchEdt.editText?.setOnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    val searchText: String = searchEdt.editText?.text.toString()
                    if (searchText.isNotEmpty()) {
                        mainActViewModel.searchWord(searchText)
                        when {
                            nameRb.isChecked -> {
                                mainActViewModel.saveButtonState("1")
                                progressBar.visibility = View.VISIBLE
                                getDrinksKeyword(searchText)
                            }
                            alphabetRb.isChecked -> {
                                mainActViewModel.saveButtonState("0")
                                progressBar.visibility = View.VISIBLE
                                getDrinksAlphabets(searchText)
                            }
                            !nameRb.isChecked && !alphabetRb.isChecked -> {
                                searchEdt.error = "Please Check Radio Button"
                            }
                        }
                    } else {
                        searchEdt.error = "Please Enter Drink Name"
                    }
                    Utilities.hideKeyboard(requireActivity())
                    true
                } else false
            }


        }

    }

    private fun setUpRecyclerView(modelList: List<DrinksModel>) {
        val myAdapter = DrinksAdapter(modelList, requireContext(), this)
        val linearLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.drinksRv.setHasFixedSize(true)
        binding.drinksRv.layoutManager = linearLayoutManager
        binding.drinksRv.adapter = myAdapter
    }

    override fun onClickSave(data: DrinksModel) {
        showprogressDialog()
        CoroutineScope(Dispatchers.IO).launch {
            Glide.with(requireContext())
                .asBitmap()
                .load(data.strDrinkThumb)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onLoadCleared(placeholder: Drawable?) {

                    }

                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        Log.e("ImagePath", getImageUri(requireContext(), resource).toString())
                        val favItems = FavouriteDrinksDb()
                        favItems.idDrink = data.idDrink?.toInt()
                        favItems.strAlcoholic = data.strAlcoholic
                        favItems.strDrink = data.strDrink
                        favItems.strCategory = data.strCategory
                        favItems.strInstructions = data.strInstructions
                        favItems.strDrinkThumb = getImageUri(requireContext(), resource).toString()
                        saveImage(favItems)
                    }
                })
        }

    }

    fun saveImage(favItems: FavouriteDrinksDb) {

        requireActivity().runOnUiThread {
            mainActViewModel.addDrinks(favItems)
            hideprogressDialog()
        }

    }

    fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            inContext.contentResolver,
            inImage,
            Calendar.getInstance().time.toString(),
            null
        )
        return Uri.parse(path)
    }

    override fun onClickDel(data: DrinksModel) {
        Toast.makeText(requireContext(), "Deleted", Toast.LENGTH_SHORT).show()
    }


    private fun getDrinksKeyword(searchText: String) {

        mainActViewModel.getDrinks(searchText).observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    it.data!!.drinks?.let { it1 -> setUpRecyclerView(it1) }
                    binding.progressBar.visibility = View.GONE
                }
                Resource.Status.ERROR -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    binding.progressBar.visibility = View.GONE
                }
                Resource.Status.LOADING -> {
                }
            }
        })
    }

    private fun getDrinksAlphabets(searchText: String) {

        mainActViewModel.getDrinksAlphabets(searchText).observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    it.data!!.drinks?.let { it1 -> setUpRecyclerView(it1) }
                    binding.progressBar.visibility = View.GONE
                }
                Resource.Status.ERROR -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    binding.progressBar.visibility = View.GONE
                }
                Resource.Status.LOADING -> {
                }
            }
        })
    }


    private fun showprogressDialog() {
        if (!progressDialog!!.isShowing) progressDialog!!.show()
    }

    private fun hideprogressDialog() {
        if (progressDialog!!.isShowing) progressDialog!!.dismiss()
    }

}