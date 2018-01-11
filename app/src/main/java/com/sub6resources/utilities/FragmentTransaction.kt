package com.sub6resources.utilities

import android.support.annotation.AnimRes
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import kotlin.properties.Delegates

class FragmentTransaction(private val fragment: Fragment, private val manager: FragmentManager) {
    private var twoWayAnimation: TwoWayFragmentAnimation? = null
    private var oneWayAnimation: OneWayFragmentAnimation? = null
    private var transitionType: Int? = null
    private var transitionResource: Int? = null
    private var backstack_tag: String? = null
    private var tag: String? = null
    private var container: Int by Delegates.notNull()
    private var extraLogic: (android.support.v4.app.FragmentTransaction) -> android.support.v4.app.FragmentTransaction = { it }

    fun into(container: Int): FragmentTransaction {
        this.container = container
        return this
    }

    fun extraLogic(extraLogic: (android.support.v4.app.FragmentTransaction) -> android.support.v4.app.FragmentTransaction): FragmentTransaction {
        this.extraLogic = extraLogic
        return this
    }

    fun withAnimation(animation: TwoWayFragmentAnimation): FragmentTransaction {
        oneWayAnimation = null
        twoWayAnimation = animation
        return this
    }

    fun withTransition(transition: Int): FragmentTransaction {
        transitionResource = null
        transitionType = transition
        return this
    }

    fun withTransitionStyle(transition: Int): FragmentTransaction {
        transitionResource = transition
        transitionType = null
        return this
    }

    fun withAnimation(animation: OneWayFragmentAnimation): FragmentTransaction {
        twoWayAnimation = null
        oneWayAnimation = animation
        return this
    }

    fun android.support.v4.app.FragmentTransaction.extraLogic(): android.support.v4.app.FragmentTransaction = extraLogic(this)

    fun addFragment() {
        manager.beginTransaction()
                .extraLogic()
                .hide(manager.findFragmentById(container)).apply {
            if (tag != null) { add(container, fragment, tag) } else { add(container, fragment) }
            twoWayAnimation?.let { setCustomAnimations(it.enter, it.exit, it.popEnter, it.popExit) }
            oneWayAnimation?.let { setCustomAnimations(it.enter, it.exit) }
            transitionType?.let { setTransition(it) }
            transitionResource?.let { setTransitionStyle(it) }
        }.addToBackStack(backstack_tag).commitAllowingStateLoss()

        manager.executePendingTransactions()
    }

    fun switchFragment() {
        manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        manager.beginTransaction().replace(container, fragment).apply {
            if (tag != null) replace(container, fragment, tag) else replace(container, fragment)
            twoWayAnimation?.let { setCustomAnimations(it.enter, it.exit, it.popEnter, it.popExit) }
            oneWayAnimation?.let { setCustomAnimations(it.enter, it.exit) }
            transitionType?.let { setTransition(it) }
            transitionResource?.let { setTransitionStyle(it) }
        }.commit()
        manager.executePendingTransactions()
    }

    fun showFragment() {
        manager.beginTransaction()
                .extraLogic()
                .show(manager.findFragmentById(container)).apply {
            if (tag != null) { add(container, fragment, tag) } else { add(container, fragment) }
            twoWayAnimation?.let { setCustomAnimations(it.enter, it.exit, it.popEnter, it.popExit) }
            oneWayAnimation?.let { setCustomAnimations(it.enter, it.exit) }
            transitionType?.let { setTransition(it) }
            transitionResource?.let { setTransitionStyle(it) }
        }.commit()
        manager.executePendingTransactions()
    }

    fun hideFragment() {
        manager.beginTransaction()
                .extraLogic()
                .hide(manager.findFragmentById(container)).apply {
            if (tag != null) { add(container, fragment, tag) } else { add(container, fragment) }
            twoWayAnimation?.let { setCustomAnimations(it.enter, it.exit, it.popEnter, it.popExit) }
            oneWayAnimation?.let { setCustomAnimations(it.enter, it.exit) }
            transitionType?.let { setTransition(it) }
            transitionResource?.let { setTransitionStyle(it) }
        }.commit()
        manager.executePendingTransactions()
    }
}

data class TwoWayFragmentAnimation(@AnimRes val enter: Int, @AnimRes val exit: Int, @AnimRes val popEnter: Int, @AnimRes val popExit: Int)
data class OneWayFragmentAnimation(@AnimRes val enter: Int, @AnimRes val exit: Int)