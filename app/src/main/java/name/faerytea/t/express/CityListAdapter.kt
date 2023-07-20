package name.faerytea.t.express

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import name.faerytea.t.express.values.City

class CityListAdapter(private val router: Router) : RecyclerView.Adapter<CityListAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var text: CharSequence
            set(value) {
                (itemView as TextView).text = value
            }
            get() = (itemView as TextView).text
    }

    var cities: List<City> = emptyList()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_city, parent, false)
        )

    override fun getItemCount(): Int = cities.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.text = cities[position].nameWithFlag()
        holder.itemView.setOnClickListener {
            router.selectCity(cities[position])
        }
    }
}