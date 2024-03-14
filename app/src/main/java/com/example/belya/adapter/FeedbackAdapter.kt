import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.belya.R
import com.example.belya.databinding.RecyclerFeedbackListItemBinding
import com.example.belya.model.Feedback
import com.google.firebase.firestore.QuerySnapshot
import java.text.SimpleDateFormat
import java.util.Locale

class FeedbackAdapter(private var listOfFeedbacks: List<Feedback>) :
    RecyclerView.Adapter<FeedbackAdapter.ViewHolder>() {

    inner class ViewHolder(private val itemBinding: RecyclerFeedbackListItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(feedback: Feedback) {
            //
            Log.d("Test","Binding")
            itemBinding.feedbackUserName.text = feedback.userName
            itemBinding.feedbackMessage.text = feedback.message
            itemBinding.feedbackRating.rating = feedback.rating
            itemBinding.feedbackUserImage.load(feedback.userImageResId) {
                crossfade(true)
                placeholder(R.drawable.ic_profileimg)
            }
            // todo fix it
            val date = feedback.time?.toDate()
            val dateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
            val formattedDate = dateFormat.format(date)
            itemBinding.feedbackTime.text = formattedDate
         //   itemBinding.feedbackTime.text = feedback.time.toString()
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

    fun updateData(feedbackList: MutableList<Feedback>) {
        listOfFeedbacks = feedbackList
        notifyDataSetChanged()
    }


}
