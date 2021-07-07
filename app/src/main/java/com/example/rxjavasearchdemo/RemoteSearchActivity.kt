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
import com.example.rxjavasearchdemo.adapter.ContactAdapter
import com.example.rxjavasearchdemo.databinding.ActivityRemoteSearchBinding
import com.example.rxjavasearchdemo.model.Contact
import com.jakewharton.rxbinding2.widget.RxTextView
import com.jakewharton.rxbinding2.widget.TextViewTextChangeEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import java.util.concurrent.TimeUnit


class RemoteSearchActivity : AppCompatActivity() {
    private val disposable = CompositeDisposable()
    private val publishSubject = PublishSubject.create<String>()
    private var apiServices: ApiServices? = null
    private var contactAdapter: ContactAdapter? = null
    private val contactsList: ArrayList<Contact> = ArrayList<Contact>()
    lateinit var binding: ActivityRemoteSearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRemoteSearchBinding.inflate(layoutInflater).also {
            setContentView(it.root)
            initView(it)
        }

    }

    private fun initView(binding: ActivityRemoteSearchBinding) {
        setSupportActionBar(binding.toolbarRemote)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        contactAdapter = ContactAdapter(contactsList)

        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(applicationContext)
        binding.RemoteSearchLayout.recyclerView.layoutManager = mLayoutManager
        binding.RemoteSearchLayout.recyclerView.itemAnimator = DefaultItemAnimator()
        binding.RemoteSearchLayout.recyclerView.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
        binding.RemoteSearchLayout.recyclerView.adapter = contactAdapter

        whiteNotificationBar(binding.RemoteSearchLayout.recyclerView)

        apiServices = ApiClient.getClient().create(ApiServices::class.java)

        val observer: DisposableObserver<List<Contact>> = searchObserver()
        disposable.add(
            publishSubject
                .debounce(300, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .flatMapSingle {
                    apiServices?.getContacts(null, it)
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(observer)
        )

        disposable.add(
            RxTextView.textChangeEvents(binding.RemoteSearchLayout.inputSearch)
                .skipInitialValue()
                .debounce(300, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(searchContactsTextWatcher())
        )
        disposable.add(observer)

        publishSubject.onNext("")

    }

    private fun searchObserver(): DisposableObserver<List<Contact>>{
        return object : DisposableObserver<List<Contact>>() {
            override fun onNext(contacts: List<Contact>) {
                contactsList.clear()
                contactsList.addAll(contacts)
                contactAdapter?.notifyDataSetChanged()
            }

            override fun onError(e: Throwable) {
                Timber.e("onError: ${e.message}")
            }

            override fun onComplete() {
            }
        }
    }

    private fun searchContactsTextWatcher(): DisposableObserver<TextViewTextChangeEvent> {
        return object : DisposableObserver<TextViewTextChangeEvent>() {
            override fun onError(e: Throwable) {
                Timber.e("onError: ${e.message}")
            }

            override fun onNext(t: TextViewTextChangeEvent) {
                Timber.d("Search query:  ${t.text()}")
                publishSubject.onNext(t.text().toString())
            }

            override fun onComplete() {
                TODO("Not yet implemented")
            }

        }
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
