package com.rongcloud.common.utils

import androidx.annotation.AnimRes
import androidx.annotation.AnimatorRes
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import cn.rong.combusis.R

object FragmentUtils {

    fun getOrNullUseTagAddedFragmentFromManager(
        fragmentManager: FragmentManager,
        clazz: Class<out Fragment>
    ): Fragment? {
        return fragmentManager.findFragmentByTag(clazz.name)
    }

    fun switchFragment(
        fragmentManager: FragmentManager,
        @IdRes containerResId: Int,
        to: Fragment,
        vararg from: Fragment = arrayOf(),
        @AnimatorRes @AnimRes enter: Int = R.anim.fade_in,
        @AnimatorRes @AnimRes exit: Int = R.anim.fade_out
    ) {
        val fragmentTransaction = fragmentManager.beginTransaction()

        if (!to.isAdded) {
            fragmentTransaction.add(containerResId, to, to::class.java.name)
        }

        if (from.isNotEmpty()) {
            for (fragment in from) {
                if (fragment.isAdded) {
                    fragmentTransaction.hide(fragment)
                }
            }
        } else {
            val fragments = fragmentManager.fragments
            for (fragment in fragments) {
                if (fragment.isAdded) {
                    fragmentTransaction.hide(fragment)
                }
            }
        }
        if (enter != 0 && exit != 0) {
            fragmentTransaction.setCustomAnimations(enter, exit)
        }
        fragmentTransaction.show(to).commitNowAllowingStateLoss()
    }

    fun addAndHideToFragmentManager(
        fragmentManager: FragmentManager,
        @IdRes containerResId: Int,
        vararg fragments: Fragment = arrayOf()
    ) {
        val transaction = fragmentManager.beginTransaction()
        for (fragment in fragments) {
            if (!fragment.isAdded) {
                transaction.add(containerResId, fragment, fragment::class.java.name).hide(fragment)
            }
        }
        transaction.commitNowAllowingStateLoss()
    }

}