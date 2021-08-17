/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.fragment.voiceroom.musicsetting

import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import cn.rongcloud.annotation.HiltBinding
import cn.rongcloud.mvoiceroom.utils.MUSIC_ATMOSPHERE_CHEER
import cn.rongcloud.mvoiceroom.utils.MUSIC_ATMOSPHERE_CLAP
import cn.rongcloud.mvoiceroom.utils.MUSIC_ATMOSPHERE_ENTER
import cn.rongcloud.voiceroomdemo.R
import com.rongcloud.common.base.BaseBottomSheetDialogFragment
import cn.rongcloud.mvoiceroom.ui.uimodel.UiMusicModel
import com.rongcloud.common.ui.widget.ActionSnackBar
import com.google.android.material.snackbar.BaseTransientBottomBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_music_setting.*
import kotlinx.android.synthetic.main.layout_music_atmosphere.view.*
import javax.inject.Inject

/**
 * @author gusd
 * @Date 2021/07/05
 */

private const val TAG = "MusicSettingFragment"

@HiltBinding(value = IMusicSettingView::class)
@AndroidEntryPoint
class MusicSettingFragment(view: IMusicSettingView) :
    BaseBottomSheetDialogFragment(R.layout.fragment_music_setting),
    IMusicSettingView by view, IMusicAddView, IMusicListView, IMusicControlView {

    private val fragmentList by lazy {
        arrayListOf<Fragment>(
            MusicListFragment(this),
            MusicAddFragment(this),
            MusicControlFragment(this)
        )
    }

    @Inject
    lateinit var presenter: MusicSettingPresenter

    private val buttons by lazy {
        arrayListOf<View>(iv_music_list, iv_add_music, iv_music_control)
    }


    private val actionSnackBar: ActionSnackBar by lazy {
        ActionSnackBar.make(cl_top, R.layout.layout_music_atmosphere).apply {


            getView().setBackgroundColor(resources.getColor(R.color.transparent))

            addCallback(object : BaseTransientBottomBar.BaseCallback<ActionSnackBar>() {
                override fun onDismissed(transientBottomBar: ActionSnackBar?, event: Int) {
                    super.onDismissed(transientBottomBar, event)

                }

                override fun onShown(transientBottomBar: ActionSnackBar?) {
                    super.onShown(transientBottomBar)

                }
            })

            with(getView()) {
                val atmosphereList = arrayListOf<View>(
                    this.tv_music_atmosphere_enter,
                    this.tv_music_atmosphere_clap,
                    this.tv_music_atmosphere_cheer
                )
                this.tv_music_atmosphere_enter.setOnClickListener {
                    selectMusicAtmosphere(atmosphereList, MUSIC_ATMOSPHERE_ENTER)
                }
                this.tv_music_atmosphere_clap.setOnClickListener {
                    selectMusicAtmosphere(atmosphereList, MUSIC_ATMOSPHERE_CLAP)
                }
                this.tv_music_atmosphere_cheer.setOnClickListener {
                    selectMusicAtmosphere(atmosphereList, MUSIC_ATMOSPHERE_CHEER)
                }
            }
        }
    }

    private fun selectMusicAtmosphere(list: List<View>, name: String) {
        list.forEachIndexed { index, view ->
            view.isSelected = index == presenter.getMusicAtmosphereIndexByName(name)
        }
        presenter.playMusicAtmosphere(name)
    }



    override fun initView() {
        iv_atmosphere_music.setOnClickListener {
            if (actionSnackBar.isShown) {
                iv_atmosphere_music.isSelected = false
                actionSnackBar.dismiss()
            } else {
                iv_atmosphere_music.isSelected = true
                actionSnackBar.show()
            }
        }

        buttons.forEachIndexed { index, view ->
            view.setOnClickListener {
                vp_page.setCurrentItem(index, true)
            }
        }

        vp_page.adapter = object : FragmentStateAdapter(childFragmentManager, lifecycle) {
            override fun getItemCount(): Int {
                return fragmentList.size
            }

            override fun createFragment(position: Int): Fragment {
                return fragmentList[position]
            }

        }
        vp_page.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                buttons.forEachIndexed { index, view ->
                    view.isSelected = index == position
                }
            }
        })


    }

    override fun gotoAddMusicView() {
        super.gotoAddMusicView()
        fragmentList.indexOfLast { it is MusicAddFragment }.let { index ->
            if (index > -1) {
                vp_page.setCurrentItem(index, true)
            }
        }
    }

    override fun showMusicList(list: List<UiMusicModel>) {

    }

}