import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.belya.databinding.ItemRecievedMeesageBinding
import com.example.belya.databinding.ItemSentMeesageBinding
import com.example.belya.model.Message

class MessagesAdapter(private val messages: List<Message>, private val currentUserId: String) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val SENT_MESSAGE_TYPE = 0
    private val RECEIVED_MESSAGE_TYPE = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == SENT_MESSAGE_TYPE) {
            val binding = ItemSentMeesageBinding.inflate(inflater, parent, false)
            SentMessageViewHolder(binding)
        } else {
            val binding = ItemRecievedMeesageBinding.inflate(inflater, parent, false)
            ReceivedMessageViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        when (holder) {
            is SentMessageViewHolder -> holder.bind(message)
            is ReceivedMessageViewHolder -> holder.bind(message)
        }
    }

    override fun getItemCount(): Int = messages.size

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        return if (message.senderId == currentUserId) {
            SENT_MESSAGE_TYPE
        } else {
            RECEIVED_MESSAGE_TYPE
        }
    }

    inner class SentMessageViewHolder(private val binding: ItemSentMeesageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            binding.sentMessage.text = message.content
        }
    }

    inner class ReceivedMessageViewHolder(private val binding: ItemRecievedMeesageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            binding.message.text = message.content
        }
    }
}