package com.capstoneproject.edusign.ui.challenge

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.capstoneproject.edusign.data.db.ChallengeEntity
import com.capstoneproject.edusign.data.model.ChallengePicture
import com.capstoneproject.edusign.databinding.ItemChallengeBinding

class ChallengeAdapter(private val listChallenge: ArrayList<ChallengePicture>, private val challengeName: ArrayList<ChallengeEntity>) :
    RecyclerView.Adapter<ChallengeAdapter.ListViewHolder>() {
    private lateinit var onItemClickCallback: OnItemClickCallback


    fun setOnitemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding =
            ItemChallengeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        if (listChallenge.size <= 0 && challengeName.size <= 0) {
            return
        }
        val (photo) = listChallenge[position]
        val challenge = challengeName[position]
        holder.binding.challengePicture.setImageResource(photo)
        holder.binding.textWordDictionary.text = challenge.name

        holder.binding.buttonCat1.setOnClickListener {
            onItemClickCallback.onItemClicked(challenge.id)
        }

    }

    override fun getItemCount(): Int = listChallenge.size

    class ListViewHolder(var binding: ItemChallengeBinding) : RecyclerView.ViewHolder(binding.root)

    @SuppressLint("NotifyDataSetChanged")
    fun setChallengeData(challengeList: ArrayList<ChallengePicture>, challengeName: List<ChallengeEntity>) {
        listChallenge.clear()
        listChallenge.addAll(challengeList)
        this.challengeName.clear()
        this.challengeName.addAll(challengeName)
        notifyDataSetChanged()
    }


    interface OnItemClickCallback {
        fun onItemClicked(data: Int)
    }
}