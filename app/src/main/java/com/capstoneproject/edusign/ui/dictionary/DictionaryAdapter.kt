package com.capstoneproject.edusign.ui.dictionary

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.capstoneproject.edusign.data.model.UserDictionaryResponse
import com.capstoneproject.edusign.databinding.ItemWordsBinding
import com.capstoneproject.edusign.ui.dictionaryDetail.DictionaryDetailActivity

class DictionaryAdapter(private var dictionaryList: List<UserDictionaryResponse>) :
    RecyclerView.Adapter<DictionaryAdapter.DictionaryViewHolder>(), Filterable {

    private var filteredDictionaryList: List<UserDictionaryResponse> = dictionaryList

    inner class DictionaryViewHolder(private val itemWordsBinding: ItemWordsBinding) :
        RecyclerView.ViewHolder(itemWordsBinding.root) {
        fun bind(dictionary: UserDictionaryResponse) {
            itemWordsBinding.textWordDictionary.text = dictionary.name

            itemWordsBinding.detailButton.setOnClickListener {
                val videoLink = dictionary.link
                val dictionaryWord = dictionary.name
                if (!videoLink.isNullOrEmpty()) {
                    val intent =
                        Intent(itemWordsBinding.root.context, DictionaryDetailActivity::class.java)
                    intent.putExtra("videoLink", videoLink)
                    intent.putExtra("dictWord", dictionaryWord)
                    itemWordsBinding.root.context.startActivity(intent)
                }
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DictionaryViewHolder {
        val itemWordsBinding =
            ItemWordsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DictionaryViewHolder(itemWordsBinding)
    }

    override fun getItemCount(): Int {
        return filteredDictionaryList.size
    }

    override fun onBindViewHolder(holder: DictionaryViewHolder, position: Int) {
        val word = filteredDictionaryList[position]
        holder.bind(word)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateWords(newWords: List<UserDictionaryResponse>) {
        dictionaryList = newWords.sortedBy { it.name }
        filteredDictionaryList = newWords.sortedBy { it.name }
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredResult: MutableList<UserDictionaryResponse> = mutableListOf()

                if (constraint.isNullOrEmpty()) {
                    filteredResult.addAll(dictionaryList)
                } else {
                    val filterPattern = constraint.toString().lowercase().trim()

                    for (word in dictionaryList) {
                        if (word.name?.lowercase()?.contains(filterPattern) == true) {
                            filteredResult.add(word)
                        }
                    }

                }

                val filterResults = FilterResults()
                filterResults.values = filteredResult
                return filterResults
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredDictionaryList = results?.values as List<UserDictionaryResponse>
                notifyDataSetChanged()
            }
        }
    }
}