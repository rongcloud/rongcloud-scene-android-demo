/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.init

import android.app.Application
import android.content.Context
import cn.rongcloud.annotation.AutoInit
import com.rongcloud.common.init.ModuleInit
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.EmojiProvider
import com.vanniktech.emoji.emoji.EmojiCategory
import com.vanniktech.emoji.ios.category.*
import javax.inject.Inject

/**
 * @author gusd
 * @Date 2021/08/03
 */
@AutoInit
class EmojiInit @Inject constructor() : ModuleInit {
    override fun getPriority(): Int {
        return Int.MAX_VALUE
    }

    override fun getName(context: Context): String = "emoji init"

    override fun onInit(application: Application) {
        EmojiManager.install(MyEmojiProvider())
    }

    internal class MyEmojiProvider : EmojiProvider {
        override fun getCategories(): Array<EmojiCategory> {
            return arrayOf(
                SmileysAndPeopleCategory(),
                AnimalsAndNatureCategory(),
                FoodAndDrinkCategory(),
                ActivitiesCategory(),
                TravelAndPlacesCategory(),
                ObjectsCategory(),
                SymbolsCategory()
            )
        }

    }
}