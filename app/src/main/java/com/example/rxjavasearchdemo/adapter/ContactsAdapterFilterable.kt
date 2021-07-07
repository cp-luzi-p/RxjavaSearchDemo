package com.example.rxjavasearchdemo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.rxjavasearchdemo.databinding.ItemContactRowBinding
import com.example.rxjavasearchdemo.model.Contact

class ContactsAdapterFilterable(var contactList: List<Contact>) :
    RecyclerView.Adapter<ContactsAdapterFilterable.ContactFilterViewHolder>() , Filterable {
    private val listener: ContactsAdapterListener? = null
    private var contactListFiltered: List<Contact> = ArrayList()

    init {
        contactListFiltered = contactList
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ContactsAdapterFilterable.ContactFilterViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemContactRowBinding.inflate(inflater, parent, false)
        return ContactFilterViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ContactsAdapterFilterable.ContactFilterViewHolder,
        position: Int
    ) {
        val itemFilterList = contactListFiltered[position]
        holder.bind(itemFilterList)
    }

    override fun getItemCount(): Int {
        return contactListFiltered.size
    }


    inner class ContactFilterViewHolder(private val binding: ItemContactRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Contact) {
            binding.name.text = item.getName()
            binding.phone.text = item.getPhone()

            Glide.with(itemView.context).load(item.getProfileImage())
                .apply(RequestOptions.circleCropTransform())
                .into(binding.thumbnail)

            itemView.setOnClickListener {
                listener?.onContactSelected(contactListFiltered[adapterPosition])
            }
        }
    }
    interface ContactsAdapterListener {
        fun onContactSelected(contact: Contact?)
    }

    override fun getFilter(): Filter {
        return object : Filter(){
            override fun performFiltering(constraint: CharSequence): FilterResults {
                val charString = constraint.toString()
                contactListFiltered = if (charString.isEmpty()){
                    contactList as ArrayList<Contact>
                } else {
                    val filterList = ArrayList<Contact>()
                    for (row in contactList) {
                        if (row.getName().lowercase().contains(charString.lowercase()) ||
                            row.getPhone().contains(constraint)) {
                            filterList.add(row)
                        }
                    }
                    filterList
                }

                val filterResult = FilterResults()
                filterResult.values = contactListFiltered
                return filterResult
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults) {
                contactListFiltered = results.values as ArrayList<Contact>
                notifyDataSetChanged()
            }

        }
    }

}