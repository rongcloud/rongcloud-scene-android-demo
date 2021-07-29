/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import cn.rongcloud.voiceroomdemo.R

private const val TAG = "MessageListActivity"

class MessageListActivity : AppCompatActivity() {
    companion object{
        fun startActivity(context:Context){
            val intent = Intent(context,MessageListActivity::class.java)
            context.startActivity(intent)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_list)
    }
}