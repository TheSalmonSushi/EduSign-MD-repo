package com.capstoneproject.edusign.ui.detailChallenge

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.capstoneproject.edusign.R
import com.capstoneproject.edusign.data.db.QuestionEntity

class DetailChallengeAdapter(private var questions:MutableList<QuestionEntity>, private val clickListener: OnIntentToCamera):
    RecyclerView.Adapter<DetailChallengeAdapter.PagerViewHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    fun update(newQuestions: QuestionEntity, index: Int ) {
        questions[index] = newQuestions
        Log.d("Adapter", "update:$questions ")
        notifyDataSetChanged()
    }

    inner class PagerViewHolder internal constructor(itemView: View): RecyclerView.ViewHolder(itemView){
        private val pagerTvQuestion = itemView.findViewById<TextView>(R.id.pager_tv_questions)
        private val pagerBtnPeragakan = itemView.findViewById<TextView>(R.id.btn_peragakan)
        private val rightAnsIV = itemView.findViewById<ImageView>(R.id.rightAnswer)
        private val wrongAnsIV = itemView.findViewById<ImageView>(R.id.wrongAnswer)

        fun bind (questionEntity: QuestionEntity) {
            pagerTvQuestion.text = questionEntity.kata
            Log.d("Adapter", "bind: ${questionEntity.isAnswered}")
            if (questionEntity.isAnswered) {
                rightAnsIV.visibility = View.VISIBLE
                wrongAnsIV.visibility = View.GONE
            } else {
                wrongAnsIV.visibility = View.VISIBLE
                rightAnsIV.visibility = View.GONE
            }

            pagerBtnPeragakan.setOnClickListener {

                clickListener.onClick(questionEntity)

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.pager_detail_challenge_item, parent, false)
        return PagerViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return questions.size
    }

    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
        val question = questions[position]

        holder.bind(question)
    }
}

class OnIntentToCamera(val clickListener: (question: QuestionEntity) -> Unit) {
    fun onClick(questionEntity: QuestionEntity) = clickListener(questionEntity)
}




