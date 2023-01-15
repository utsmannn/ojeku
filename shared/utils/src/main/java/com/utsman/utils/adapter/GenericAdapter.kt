package com.utsman.utils.adapter

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.*
import com.utsman.core.data.Equatable
import com.utsman.core.data.EquatableProvider
import com.utsman.core.extensions.inflateRoot
import com.utsman.utils.R
import com.utsman.utils.orNol
import kotlinx.coroutines.delay
import java.util.*

class GenericAdapter<T : Equatable>(
    @LayoutRes private val layoutRes: Int,
    @LayoutRes private val layoutPlaceholder: Int = R.layout.util_item_loading,
    @LayoutRes private val layoutEmpty: Int = R.layout.util_item_empty,
    @LayoutRes private val layoutError: Int = R.layout.util_item_error
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val differCallback = object : DiffUtil.ItemCallback<Equatable>() {
        override fun areItemsTheSame(oldItem: Equatable, newItem: Equatable): Boolean {
            return oldItem.isEqual(newItem)
        }

        override fun areContentsTheSame(oldItem: Equatable, newItem: Equatable): Boolean {
            return oldItem.isEqual(newItem)
        }
    }

    private val differ = AsyncListDiffer(this, differCallback)

    private var items: List<Equatable>
        get() = differ.currentList
        set(value) {
            differ.submitList(value)
        }

    private var isEndedTemp = false
    private var attachedRecyclerView: RecyclerView? = null
    var previousCount = 0

    var onBindItem: View.(position: Int, item: T) -> Unit = { _, _ -> }
    var onErrorBindItem: View.(message: String, () -> Unit) -> Unit = { _, _ -> }
    var retryAction: () -> Unit = {}
    var onDragListener: (from: Int, to: Int, newList: List<T>) -> Unit = { _, _, _ -> }
    var isEndScrollListener: () -> Unit = {}
    var onScrollListener: (isTop: Boolean, isEnd: Boolean) -> Unit = { _, _ -> }

    var isTop: Boolean = false
    var isEnd: Boolean = false

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        attachedRecyclerView = recyclerView

        val attachedLayoutManager = recyclerView.layoutManager

        if (attachedLayoutManager is GridLayoutManager) {
            val spanCount = attachedLayoutManager.spanCount
            attachedLayoutManager.spanSizeLookup = setGridSpan(spanCount)
        }

        attachedRecyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                onScrollListener.invoke(
                    !recyclerView.canScrollVertically(-1),
                    !recyclerView.canScrollVertically(0)
                )
                isTop = !recyclerView.canScrollVertically(-1)
                isEnd = !recyclerView.canScrollVertically(0)
                if (dy > 0 && !isEndedTemp) {
                    val visibleItemCount = attachedLayoutManager?.childCount.orNol()
                    val totalItemCount = attachedLayoutManager?.itemCount.orNol()
                    val pastVisibleItem =
                        (attachedLayoutManager as? LinearLayoutManager)?.findFirstVisibleItemPosition()
                            .orNol()
                    val isLast = visibleItemCount + pastVisibleItem >= totalItemCount
                    val isEnded = isLast && !isContainIndicator()

                    if (isEndedTemp != isEnded && !isContainLoading()) {
                        isEndedTemp = isEnded
                        if (isEndedTemp) {
                            isEndScrollListener.invoke()
                        }
                    }
                }
            }
        })

    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        attachedRecyclerView = null
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            Type.DATA -> DataViewHolder<T>(parent.inflateRoot(layoutRes))
            Type.LOADING -> LoadingViewHolder(parent.inflateRoot(layoutPlaceholder))
            Type.EMPTY -> EmptyViewHolder(parent.inflateRoot(layoutEmpty))
            else -> ErrorViewHolder(parent.inflateRoot(layoutError))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        when (getItemViewType(position)) {
            Type.DATA -> {
                try {
                    (holder as DataViewHolder<T>).bind(position, item as T, onBindItem)
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                }
            }
            Type.ERROR -> {
                (holder as ErrorViewHolder).bind(
                    (item as Error).message,
                    onErrorBindItem,
                    retryAction
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemId(position: Int): Long {
        return items[position].longId
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is Loading -> Type.LOADING
            is Error -> Type.ERROR
            is Empty -> Type.EMPTY
            else -> Type.DATA
        }
    }

    private fun setGridSpan(column: Int): GridLayoutManager.SpanSizeLookup {
        return object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (getItemViewType(position)) {
                    Type.DATA -> 1
                    else -> column
                }
            }
        }
    }

    fun pushItems(items: List<T>, isRemoveDuplicate: Boolean = true) {
        val newItem = calculateMutableItems()
        val currentItem = calculateMutableItems(differ.currentList)
        if (items.isNotEmpty()) newItem.addAll(items)
        if (newItem.isEmpty()) newItem.add(Empty)
        isEndedTemp = false
        val newList = (currentItem + newItem).run {
            if (isRemoveDuplicate) {
                distinct()
            } else {
                this
            }
        }
        differ.submitList(newList)

        previousCount += items.size
    }

    fun pushLoading() {
        val newItem = calculateMutableItems()
        newItem.add(Loading)
        differ.submitList(newItem)
    }

    fun pushError(throwable: Throwable) {
        val newItem = calculateMutableItems()
        newItem.add(Error(throwable.localizedMessage.orEmpty()))
        differ.submitList(newItem)
        attachedRecyclerView?.smoothScrollToPosition(itemCount)
    }

    fun changeItem(items: List<T>, isRemoveDuplicate: Boolean = true) {
        if (isRemoveDuplicate) {
            differ.submitList(items.distinct())
        } else {
            differ.submitList(items)
        }

        previousCount = items.size
    }

    suspend fun clearItems() {
        delay(500L)
        differ.submitList(emptyList())
    }

    fun removeAdditional() {
        val newItem = items.toMutableList()
        newItem.remove(Loading)
        newItem.remove(newItem.find { it is Error })
        newItem.remove(Empty)
        isEndedTemp = false
        differ.submitList(newItem)
    }

    fun getList(): List<T> {
        return items as List<T>
    }

    private fun calculateMutableItems(items: List<Equatable> = this.items): MutableList<Equatable> {
        val newItem = items.toMutableList()
        newItem.remove(Loading)
        newItem.remove(newItem.find { it is Error })
        newItem.remove(Empty)
        return newItem
    }

    private fun isContainLoading(): Boolean {
        return items.find { it is Loading } != null
    }

    private fun isContainError(): Boolean {
        return items.find { it is Error } != null
    }

    private fun isContainIndicator(): Boolean {
        return isContainLoading() || isContainError()
    }

    class DataViewHolder<T : Equatable>(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(position: Int, item: T, onBind: View.(position: Int, item: T) -> Unit) {
            itemView.tag = "tag-for-$position"
            onBind.invoke(itemView, position, item)
        }
    }

    class LoadingViewHolder(view: View) : RecyclerView.ViewHolder(view)

    class ErrorViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(
            message: String,
            onBind: View.(String, () -> Unit) -> Unit,
            retryAction: () -> Unit
        ) {
            onBind.invoke(itemView, message, retryAction)
            itemView.findViewById<Button>(R.id.item_btn_retry).setOnClickListener {
                retryAction.invoke()
            }
        }
    }

    class EmptyViewHolder(view: View) : RecyclerView.ViewHolder(view)

    object Loading : EquatableProvider(ITEM_LOADING)
    data class Error(val message: String) : EquatableProvider(ITEM_ERROR)
    object Empty : EquatableProvider(ITEM_EMPTY)

    object Type {
        const val DATA = 1
        const val LOADING = 2
        const val ERROR = 3
        const val EMPTY = 4
    }

    companion object {
        internal const val ITEM_LOADING = "loading"
        internal const val ITEM_ERROR = "error"
        internal const val ITEM_EMPTY = "empty"
    }
}

/**
 * val adapter by genericAdapter()
 *
 */
fun <T: Equatable> genericAdapter(
    layoutRes: Int,
    loadingLayoutRes: Int = R.layout.util_item_loading,
    onBindItem: View.(position: Int, item: T) -> Unit
): Lazy<GenericAdapter<T>> {
    return lazy {
        val adapter = GenericAdapter<T>(
            layoutRes = layoutRes,
            layoutPlaceholder = loadingLayoutRes
        )
        adapter.onBindItem = onBindItem
        adapter
    }
}