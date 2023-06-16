package com.capstoneproject.edusign.ui.challenge

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ScrollCaptureCallback
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.capstoneproject.edusign.data.model.ChallengePicture
import com.capstoneproject.edusign.databinding.ItemChallengeBinding

class ChallengeAdapter(private val listChallenge: ArrayList<ChallengePicture>): RecyclerView.Adapter<ChallengeAdapter.ListViewHolder>() {
    private lateinit var onItemClickCallback: OnItemClickCallback


    fun setOnitemClickCallback (onItemClickCallback: OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemChallengeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val (name, photo) = listChallenge[position]
        holder.binding.challengePicture.setImageResource(photo)
        holder.binding.textWordDictionary.text = name

        holder.binding.buttonCat1.setOnClickListener {
            val intent: Intent
            when (name) {
                "Hewan" -> {
                    intent = Intent(holder.itemView.context, ActivityDetailChallenge::class.java)
                }
                "Anggota \nTubuh" -> {
                    intent = Intent(holder.itemView.context, ActivityDetailChallenge2::class.java)
                }
                "Warna" -> {
                    intent = Intent(holder.itemView.context, ActivityDetailChallenge3::class.java)
                }
                "Keluarga" -> {
                    intent = Intent(holder.itemView.context, ActivityDetailChallenge4::class.java)
                }
                else -> {
                    return@setOnClickListener
                }
            }
            holder.itemView.context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int = listChallenge.size

    class ListViewHolder(var binding: ItemChallengeBinding) : RecyclerView.ViewHolder(binding.root)

    @SuppressLint("NotifyDataSetChanged")
    fun setChallengeData(challengeList: ArrayList<ChallengePicture>) {
        listChallenge.clear()
        listChallenge.addAll(challengeList)
        notifyDataSetChanged()
    }



    interface  OnItemClickCallback{
        fun onItemClicked(data: ChallengePicture)
    }
}