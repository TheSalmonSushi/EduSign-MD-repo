package com.capstoneproject.edusign.ui.dictionary


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstoneproject.edusign.databinding.FragmentDictionaryBinding
import androidx.appcompat.widget.SearchView


class DictionaryFragment : Fragment() {

    private lateinit var dictionaryAdapter: DictionaryAdapter
    private lateinit var dictionaryViewModel: DictionaryViewModel

    private var _binding: FragmentDictionaryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDictionaryBinding.inflate(inflater, container, false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dictionaryViewModel = ViewModelProvider(this)[DictionaryViewModel::class.java]

        binding.rvMain.layoutManager = LinearLayoutManager(requireContext())
        dictionaryAdapter = DictionaryAdapter(emptyList())
        binding.rvMain.adapter = dictionaryAdapter

        dictionaryViewModel.wordsLiveData.observe(viewLifecycleOwner) { words ->
            dictionaryAdapter.updateWords(words)
        }

        dictionaryViewModel.errorLiveData.observe(viewLifecycleOwner) { errorMessage ->
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
        }

        dictionaryViewModel.loadingLiveData.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.INVISIBLE
            }
        }

        dictionaryViewModel.fetchWords()


        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                dictionaryAdapter.filter.filter(newText)
                return true
            }

        })


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
