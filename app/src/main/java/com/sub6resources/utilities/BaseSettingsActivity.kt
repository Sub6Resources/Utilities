package com.sub6resources.utilities

import android.graphics.Color
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.afollestad.materialdialogs.list.listItems
import kotlinx.android.synthetic.main.activity_settings.*


abstract class BaseSettingsActivity : BaseActivity(R.layout.activity_settings) {
    abstract val settings: SettingsDsl

    override val toolbar = R.id.toolbar

    override fun onResume() {
        super.onResume()
        val tempToolbar = findViewById<Toolbar>(R.id.toolbar)
        tempToolbar.setTitleTextColor(settings.titleTextColor)
        tempToolbar.title = if(settings.title.isNotEmpty()) settings.title else "Settings"
    }

    override fun setUp() {
        settings.groups.forEach { settingsGroup ->
            var inflater = LayoutInflater.from(this)

            if(settingsGroup.title.isNotEmpty()) {
                val header = inflater.inflate(R.layout.settings_grouptitle, settings_container, false)
                header.findViewById<TextView>(R.id.setting_title).apply {
                    text = settingsGroup.title
                }
                settings_container.addView(header)
            }

            settingsGroup.settings.forEach { setting ->
                when(setting.defaultValue) {
                    is Boolean -> {
                        inflater = LayoutInflater.from(this)
                        val inflatedLayout = inflater.inflate(R.layout.settings_boolean, settings_container, false)
                        inflatedLayout.findViewById<TextView>(R.id.setting_title).apply {
                            text = if(setting.description.isNotEmpty()) setting.description else setting.key
                        }
                        val subtitleView = inflatedLayout.findViewById<TextView>(R.id.setting_subtitle).apply {
                            text = setting.subtitle
                            if(setting.subtitleIfTrue.isNotEmpty() || setting.subtitleIfFalse.isNotEmpty())
                                text = if(sharedPreferences.getBoolean(setting.key, setting.defaultValue as Boolean)) setting.subtitleIfTrue else setting.subtitleIfFalse
                            if(setting.subtitleIfTrue.isNotEmpty() || setting.subtitleIfFalse.isNotEmpty() || setting.subtitle.isNotEmpty())
                                visibility = View.VISIBLE
                        }
                        val checkBox = inflatedLayout.findViewById<SwitchCompat>(R.id.setting_switch).apply {
                            isChecked = sharedPreferences.getBoolean(setting.key, setting.defaultValue as Boolean)
                            setOnCheckedChangeListener { _, isChecked ->
                                sharedPreferences.edit {
                                    putBoolean(setting.key, isChecked)
                                }
                                if(isChecked && setting.subtitleIfTrue.isNotEmpty()) subtitleView.text = setting.subtitleIfTrue
                                else if(setting.subtitleIfFalse.isNotEmpty()) subtitleView.text = setting.subtitleIfFalse
                            }
                        }
                        inflatedLayout.onClick {
                            checkBox.isChecked = !checkBox.isChecked
                        }
                        settings_container.addView(inflatedLayout)
                    }
                    is Int -> {
                        inflater = LayoutInflater.from(this)
                        val inflatedLayout = inflater.inflate(R.layout.settings_item, settings_container, false)
                        inflatedLayout.findViewById<TextView>(R.id.setting_title).apply {
                            text = setting.description
                        }
                        val subtitleText = inflatedLayout.findViewById<TextView>(R.id.setting_subtitle).apply {
                            text = "${sharedPreferences.getInt(setting.key, setting.defaultValue as Int)} ${setting.units}"
                            visibility = View.VISIBLE
                        }
                        inflatedLayout.onClick {
                            dialog {
                                title(text = setting.description)
                                inputType(InputType.TYPE_CLASS_NUMBER)
                                input(setting.description, sharedPreferences.getInt(setting.key, setting.defaultValue as Int).toString()) { _, input ->
                                    if(input.isNotEmpty()) {
                                        sharedPreferences.edit { putInt(setting.key, input.toString().toInt()) }
                                        subtitleText.text = "$input ${setting.units}"
                                    }
                                }
                                positiveButton(text = "Save")
                            }.show()
                        }
                        settings_container.addView(inflatedLayout)
                    }
                    is Float -> {
                        inflater = LayoutInflater.from(this)
                        val inflatedLayout = inflater.inflate(R.layout.settings_item, settings_container, false)
                        inflatedLayout.findViewById<TextView>(R.id.setting_title).apply {
                            text = setting.description
                        }
                        val subtitleText = inflatedLayout.findViewById<TextView>(R.id.setting_subtitle).apply {
                            text = "${sharedPreferences.getFloat(setting.key, setting.defaultValue as Float)} ${setting.units}"
                            visibility = View.VISIBLE
                        }
                        inflatedLayout.onClick {
                            dialog {
                                title(text = setting.description)
                                inputType(InputType.TYPE_NUMBER_FLAG_DECIMAL.or(InputType.TYPE_CLASS_NUMBER))
                                input(setting.description, sharedPreferences.getFloat(setting.key, setting.defaultValue as Float).toString()) { _, input ->
                                    if(input.isNotEmpty()) {
                                        sharedPreferences.edit { putFloat(setting.key, input.toString().toFloat()) }
                                        subtitleText.text = "$input ${setting.units}"
                                    }
                                }
                                positiveButton(text = "Save")
                            }.show()
                        }
                        settings_container.addView(inflatedLayout)
                    }
                    is Long -> {
                        inflater = LayoutInflater.from(this)
                        val inflatedLayout = inflater.inflate(R.layout.settings_item, settings_container, false)
                        inflatedLayout.findViewById<TextView>(R.id.setting_title).apply {
                            text = setting.description
                        }
                        val subtitleText = inflatedLayout.findViewById<TextView>(R.id.setting_subtitle).apply {
                            text = "${sharedPreferences.getLong(setting.key, setting.defaultValue as Long)} ${setting.units}"
                            visibility = View.VISIBLE
                        }
                        inflatedLayout.onClick {
                            dialog {
                                title(text = setting.description)
                                inputType(InputType.TYPE_CLASS_NUMBER)
                                input(setting.description, sharedPreferences.getLong(setting.key, setting.defaultValue as Long).toString()) { _, input ->
                                    if(input.isNotEmpty()) {
                                        sharedPreferences.edit { putLong(setting.key, input.toString().toLong()) }
                                        subtitleText.text = "$input ${setting.units}"
                                    }
                                }
                                positiveButton(text = "Save")
                            }.show()
                        }
                        settings_container.addView(inflatedLayout)
                    }
                    is String -> {
                        if(setting.options.isNotEmpty()) {
                            //Array of options

                            inflater = LayoutInflater.from(this)
                            val inflatedLayout = inflater.inflate(R.layout.settings_item, settings_container, false)
                            inflatedLayout.findViewById<TextView>(R.id.setting_title).apply {
                                text = setting.description
                            }
                            val subtitleText = inflatedLayout.findViewById<TextView>(R.id.setting_subtitle).apply {
                                text = sharedPreferences.getString(setting.key, setting.defaultValue as String)
                                visibility = View.VISIBLE
                            }
                            inflatedLayout.onClick {
                                dialog {
                                    title(text = setting.description)
                                    listItems(items = setting.options.toList())
                                    itemsCallbackSingleChoice(setting.options.indexOf(sharedPreferences.getString(setting.key, setting.defaultValue as String))) { _, _, _, text ->
                                        sharedPreferences.edit { putString(setting.key, text.toString()) }
                                        subtitleText.text = text.toString()
                                        //This allows the item to be selected. Possible customization here in the future.
                                        return@itemsCallbackSingleChoice true
                                    }
                                    positiveButton(text = "Save")
                                }.show()
                            }
                            settings_container.addView(inflatedLayout)
                        } else {
                            //String

                            inflater = LayoutInflater.from(this)
                            val inflatedLayout = inflater.inflate(R.layout.settings_item, settings_container, false)
                            inflatedLayout.findViewById<TextView>(R.id.setting_title).apply {
                                text = setting.description
                            }
                            val subtitleText = inflatedLayout.findViewById<TextView>(R.id.setting_subtitle).apply {
                                text = sharedPreferences.getString(setting.key, setting.defaultValue as String)
                                visibility = View.VISIBLE
                            }
                            inflatedLayout.onClick {
                                dialog {
                                    title(text = setting.description)
                                    input(setting.description, sharedPreferences.getString(setting.key, setting.defaultValue as String)) { dialog, input ->
                                        sharedPreferences.edit { putString(setting.key, input.toString()) }
                                        subtitleText.text = input.toString()
                                    }
                                    positiveButton(text = "Save")
                                }.show()
                            }
                            settings_container.addView(inflatedLayout)
                        }
                    }
                }
            }
        }
    }
}

data class SettingsDsl(var title: String = "") {
    var titleTextColor = Color.WHITE
    var groups = arrayListOf<SettingsGroup>()
}
data class SettingsGroup(var title: String = "") {
    var settings = arrayListOf<Setting>()
}
class Setting(var key: String, var defaultValue: Any) {
    var description: String = ""
    var subtitle: String = ""
    var subtitleIfTrue: String = ""
    var subtitleIfFalse: String = ""
    var units: String = ""
    var options: Array<String> = arrayOf()
}
fun settingsActivity(block: SettingsDsl.() -> Unit) = SettingsDsl().apply {block()}
fun SettingsDsl.group(groupTitle: String = "", block: SettingsGroup.() -> Unit) = this.groups.add(SettingsGroup().apply {title = groupTitle; block()})
fun SettingsGroup.setting(sharedPreferencesKey: String, defaultValue: Any, block: Setting.() -> Unit = {}) = this.settings.add(Setting(sharedPreferencesKey, defaultValue).apply {block()})

