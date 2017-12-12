package com.sub6resources.utilities

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


abstract class BaseFragment: Fragment() {
    abstract val fragLayout: Int

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val content = inflater.inflate(fragLayout, container, false)
        return content
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


}