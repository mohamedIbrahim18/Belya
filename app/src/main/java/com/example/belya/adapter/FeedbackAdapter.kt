import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.belya.R
import com.example.belya.databinding.RecyclerFeedbackListItemBinding
import com.example.belya.model.Feedback
import java.text.SimpleDateFormat
import java.util.Locale

class FeedbackAdapter(private var listOfFeedbacks: List<Feedback>) :
    RecyclerView.Adapter<FeedbackAdapter.ViewHolder>() {

    inner class ViewHolder(private val itemBinding: RecyclerFeedbackListItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(feedback: Feedback) {
            //
            Log.d("Test", "Binding")
            itemBinding.feedbackUserName.text = feedback.userName
            Log.e("Name",feedback.userName)
            itemBinding.feedbackMessage.text = feedback.message
            itemBinding.feedbackRating.rating = feedback.rating
            Glide.with(itemBinding.root.context).load(feedback.imagePath)
                .placeholder(R.drawable.ic_profileimg)
                .into(itemBinding.feedbackUserImage)

            feedback.time?.let { time ->
                val date = time.toDate()
                val dateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
                val formattedDate = dateFormat.format(date)
                itemBinding.feedbackTime.text = formattedDate
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding = RecyclerFeedbackListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(itemBinding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listOfFeedbacks[position])
    }

    override fun getItemCount(): Int = listOfFeedbacks.size

    fun updateData(feedbackList: List<Feedback>) {
        listOfFeedbacks = feedbackList
        notifyDataSetChanged()
    }
}
