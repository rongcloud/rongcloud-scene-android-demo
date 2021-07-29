/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.fragment

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.FragmentManager
import cn.rongcloud.voiceroomdemo.R
import cn.rongcloud.voiceroomdemo.common.BaseLifeCyclePresenter
import cn.rongcloud.voiceroomdemo.common.IBaseView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * @author gusd
 * @Date 2021/06/21
 */
abstract class BaseBottomSheetDialogFragment<P : BaseLifeCyclePresenter<V>, V : IBaseView>(@LayoutRes val layoutId: Int) :
    BottomSheetDialogFragment() {
    private lateinit var mBehavior: BottomSheetBehavior<View>
    lateinit var presenter: P

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreate(savedInstanceState)
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        val view = View.inflate(context, layoutId, null)
        dialog.setContentView(view)
        mBehavior = BottomSheetBehavior.from(view.parent as View)
        mBehavior.isHideable = false
        return dialog
    }

    fun show(manager: FragmentManager) {
        super.show(manager, this.javaClass.name)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
        presenter = initPresenter()
        lifecycle.addObserver(presenter)
    }

    abstract fun initPresenter(): P

    override fun onStart() {
        super.onStart()
        mBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        if (isFullScreen()) {
            mBehavior.peekHeight = activity?.windowManager?.defaultDisplay?.height ?: 0
        }
    }

    open fun isFullScreen(): Boolean {
        return false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return LayoutInflater.from(context).inflate(layoutId, container, false)
    }

    override fun onDestroy() {
        lifecycle.removeObserver(presenter)
        super.onDestroy()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initListener()
        initData()
    }

    open fun initData() {

    }

    open fun initListener() {

    }

    abstract fun initView()


}