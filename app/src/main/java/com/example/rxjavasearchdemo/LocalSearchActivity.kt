package com.example.rxjavasearchdemo

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rxjavasearchdemo.adapter.ContactsAdapterFilterable
import com.example.rxjavasearchdemo.databinding.ActivityLocalSearchBinding
import com.example.rxjavasearchdemo.model.Contact
import com.jakewharton.rxbinding2.widget.RxTextView
import com.jakewharton.rxbinding2.widget.TextViewTextChangeEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit


class LocalSearchActivity : AppCompatActivity() {

    private val disposable = CompositeDisposable()
    lateinit var apiService: ApiServices
    private var contactAdapter: ContactsAdapterFilterable? = null
    lateinit var binding: ActivityLocalSearchBinding

    private val contactsList: ArrayList<Contact> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocalSearchBinding.inflate(layoutInflater).also {
            setContentView(it.root)
            initView(it)
        }

    }

    private fun initView(binding: ActivityLocalSearchBinding) {

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        contactAdapter = ContactsAdapterFilterable(contactsList)

        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(applicationContext)
        binding.contentLocalLayout.recyclerView.layoutManager = layoutManager
        binding.contentLocalLayout.recyclerView.itemAnimator = DefaultItemAnimator()
        binding.contentLocalLayout.recyclerView.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
        binding.contentLocalLayout.recyclerView.adapter = contactAdapter
        whiteNotificationBar(binding.contentLocalLayout.recyclerView)

        apiService = ApiClient.getClient().create(ApiServices::class.java)

        disposable.add(
            RxTextView.textChangeEvents(binding.contentLocalLayout.inputSearch)
                .skipInitialValue()
                .debounce(300, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(searchContacts())
        )
        fetchContacts("gmail")
    }

    private fun fetchContacts(source: String) {

        disposable.add(apiService
            .getContacts(source, null)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<List<Contact>>(){
                override fun onError(e: Throwable) {
                    TODO("Not yet implemented")
                }
                override fun onSuccess(contact: List<Contact>) {
                    contactsList.clear()
                    contactsList.addAll(contact)
                    contactAdapter?.notifyDataSetChanged()
                }

            }))
    }


    private fun searchContacts(): DisposableObserver<in TextViewTextChangeEvent> {
        return object : DisposableObserver<TextViewTextChangeEvent>() {
            override fun onNext(t: TextViewTextChangeEvent) {
                Timber.d("Search query:  ${t.text()}")
                contactAdapter?.filter?.filter(t.text())
            }

            override fun onError(e: Throwable) {
                Timber.d("onError: ${e.message}")
            }

            override fun onComplete() {
                TODO("Not yet implemented")
            }

        }

    }


    override fun onDestroy() {
        disposable.clear()
        super.onDestroy()
    }

    private fun whiteNotificationBar(recyclerView: RecyclerView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var flags: Int = recyclerView.getSystemUiVisibility()
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            recyclerView.setSystemUiVisibility(flags)
            window.statusBarColor = Color.WHITE
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }

        return super.onOptionsItemSelected(item)
    }

}
