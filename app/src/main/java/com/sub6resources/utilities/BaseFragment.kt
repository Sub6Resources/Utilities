package com.sub6resources.utilities

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.arch.lifecycle.ViewModelStores
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.Toolbar
import android.view.*
import org.koin.android.architecture.ext.KoinFactory
import org.koin.android.architecture.ext.getViewModel


abstract class BaseFragment: Fragment() {
    abstract val fragLayout: Int

    open val menu: Int? = null
    open val toolbar: Int? = null

    val baseActivity by lazy { activity!! as BaseActivity }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(fragLayout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar?.let {
            baseActivity.setSupportActionBar(view.findViewById(it))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(menu != null) {
            setHasOptionsMenu(true)
        }
    }

    open fun onBackPressed(){
        popFragment()
    }

    open fun onRefresh() {}

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            onRefresh()
        }
    }

    override fun onCreateOptionsMenu(m: Menu?, inflater: MenuInflater?) {
        if(menu != null) {
            inflater?.inflate(menu!!, m)
        }
        super.onCreateOptionsMenu(m, inflater)
    }

    fun addFragment(fragment: BaseFragment){
        FragmentTransaction(fragment, fragmentManager!!)
                .into((context as BaseActivity).fragmentTargets!!)
                .addFragment()
    }

    fun switchFragment(fragment: BaseFragment){
        FragmentTransaction(fragment, fragmentManager!!)
                .into((context as BaseActivity).fragmentTargets!!)
                .switchFragment()
    }

    fun popFragment(){
        fragmentManager?.popBackStack()
    }

    fun openSideNav(){
        (context as BaseActivity).openSideNav()
    }

    fun <T: ViewModel> getViewModel(javaClass: Class<T>): Lazy<T> = lazy { ViewModelProviders.of(this).get(javaClass) }
    fun <T: ViewModel> getSharedViewModel(javaClass: Class<T>): Lazy<T> = lazy { ViewModelProviders.of(activity!!).get(javaClass) }
    inline fun <reified T : ViewModel> Fragment.getSharedViewModel(): T {
        val vm = ViewModelProvider(ViewModelStores.of(this.activity!!), KoinFactory)
        return vm.get(T::class.java)
    }
}