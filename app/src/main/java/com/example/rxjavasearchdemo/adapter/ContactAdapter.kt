package com.example.rxjavasearchdemo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.rxjavasearchdemo.databinding.ItemContactRowBinding
import com.example.rxjavasearchdemo.model.Contact

class ContactAdapter(val contactList: List<Contact>) :
    RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {
    private val listener: ContactsAdapterListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemContactRowBinding.inflate(inflater, parent, false)
        return ContactViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val itemList = contactList[position]
        holder.bind(itemList)
    }

    override fun getItemCount(): Int {
        return contactList.size
    }

    inner class ContactViewHolder(private val binding: ItemContactRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Contact) {
            binding.name.text = item.getName()
            binding.phone.text = item.getPhone()

            Glide.with(itemView.context).load(item.getProfileImage())
                .apply(RequestOptions.circleCropTransform())
                .into(binding.thumbnail)

            itemView.setOnClickListener {
                listener?.onContactSelected(contactList[adapterPosition])
            }
        }
    }

    interface ContactsAdapterListener {
        fun onContactSelected(contact: Contact?)
    }
}