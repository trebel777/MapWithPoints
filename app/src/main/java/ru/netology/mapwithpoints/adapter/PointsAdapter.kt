package ru.netology.mapwithpoints.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import ru.netology.mapwithpoints.models.Point
import ru.netology.mapwithpoints.R
import ru.netology.mapwithpoints.activitys.Points

class PointsAdapter(
    private val context: Context,
    private val parentFragment: Fragment,
    private val onPointInteractionListener: OnPointInteractionListener
) :
    RecyclerView.Adapter<PointsAdapter.PointViewHolder>() {

    private var pointsList: List<Point> = emptyList()
    private val fullList = ArrayList<Point>()

    interface OnPointInteractionListener {
        fun onPointDelete(point: Point)
        fun onPointEdit(point: Point)
    }


    inner class PointViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val pointsLayout = itemView.findViewById<CardView>(R.id.point_layout)
        val title = itemView.findViewById<TextView>(R.id.tvTitle)
        val description = itemView.findViewById<TextView>(R.id.tvDescription)

        init {
            itemView.setOnLongClickListener {
                val point = pointsList[adapterPosition]
                showPointMenu(point)
                true
            }
        }
            private fun showPointMenu(point: Point) {
                val popupMenu = PopupMenu(context, itemView)
                popupMenu.menuInflater.inflate(R.menu.point_menu, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.menu_delete_point -> {
                            onPointInteractionListener.onPointDelete(point)
                            true
                        }
                        R.id.menu_edit_point -> {
                            onPointInteractionListener.onPointEdit(point)
                            true
                        }
                        else -> false
                    }
                }
                popupMenu.show()
            }
        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PointViewHolder {
        return PointViewHolder(
            LayoutInflater.from(context).inflate(R.layout.point_card, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return pointsList.size
    }

    override fun onBindViewHolder(holder: PointViewHolder, position: Int) {
        val currentPoint = pointsList[position]
        holder.title.text = currentPoint.title
        holder.description.text = currentPoint.description

        holder.pointsLayout.setOnClickListener {
            val pointsFragment = parentFragment as Points
            pointsFragment.navigateToMapFragment(currentPoint)
        }

        }


    fun updateList(newList: List<Point>) {
        fullList.clear()
        fullList.addAll(newList)
        pointsList = fullList
        notifyDataSetChanged()
    }


    fun setData(points: List<Point>) {
        pointsList = points
        notifyDataSetChanged()
    }


}