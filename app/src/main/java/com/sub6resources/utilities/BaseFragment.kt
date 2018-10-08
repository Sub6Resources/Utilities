package com.sub6resources.utilities

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.koin.androidx.viewmodel.ext.android.getViewModel


abstract class BaseFragment : Fragment() {
    abstract val fragLayout: Int

    open val menu: Int? = null
    open val toolbar: Int? = null

    val baseActivity by lazy { activity!! as BaseActivity }

    open fun setUp() {}

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(fragLayout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar?.let {
            baseActivity.setSupportActionBar(view.findViewById(it))
            if (baseActivity.parentActivityIntent != null) {
                baseActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            }
        }

        setUp()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (menu != null) {
            setHasOptionsMenu(true)
        }
    }

    open fun onBackPressed() {
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
        if (menu != null) {
            inflater?.inflate(menu!!, m)
        }
        super.onCreateOptionsMenu(m, inflater)
    }

    fun addFragment(fragment: BaseFragment) {
        FragmentTransaction(fragment, fragmentManager!!)
                .into((context as BaseActivity).fragmentTargets)
                .addFragment()
    }

    fun switchFragment(fragment: BaseFragment) {
        FragmentTransaction(fragment, fragmentManager!!)
                .into((context as BaseActivity).fragmentTargets)
                .switchFragment()
    }

    fun popFragment() {
        fragmentManager?.popBackStack()
    }

    inline fun <reified T : ViewModel> getViewModel(): Lazy<T> = lazy { (this as Fragment).getViewModel<T>() }

    inline fun <reified T : ViewModel> Fragment.getSharedViewModel(): Lazy<T> = lazy { ViewModelProvider(baseActivity, ViewModelProvider.NewInstanceFactory()).get(T::class.java) }

    inline fun <reified T : ViewModel> getGlobalViewModel(): Lazy<T> = lazy { ViewModelProvider(baseActivity.app, ViewModelProvider.NewInstanceFactory()).get(T::class.java) }
}