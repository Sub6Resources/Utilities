package com.sub6resources.utilities.sample

import com.sub6resources.utilities.BaseSettingsActivity
import com.sub6resources.utilities.group
import com.sub6resources.utilities.setting
import com.sub6resources.utilities.settingsActivity

class PreferencesActivity: BaseSettingsActivity() {
    override val settings = settingsActivity {
        title = "Settings"
        group("Common Settings") {
            setting("areNotificationsEnabled", false) {
                description = "Notifications Enabled"
                subtitleIfTrue = "Notifications are enabled"
                subtitleIfFalse = "Notifications not enabled"
            }
            setting("isReallyWorking", true) {
                subtitle = "IDK"
            }
        }
        group("Random Settings") {
            setting("isRandom", true) {
                description = "This is a random app"
                subtitleIfTrue = "Yes"
                subtitleIfFalse = "No"
            }
            setting("name", "John Smith") {
                description = "Name"
            }
            setting("age", 31) {
                description = "Age"
                units = "years"
            }
            setting("gender", "Female") {
                description = "Gender"
                options = arrayOf("Male", "Female")
            }
        }
        group("Facts") {
            setting("address", "") {
                description = "Address"
            }
            setting("answer", 42) {
                description = "The answer to life, the universe, and everything"
            }
            setting("long_answer", 100_000_000L) {
                description = "Long Answer"
            }
            setting("floating_answer", 3.14159265f) {
                description = "Float Answer"
            }
        }
    }
}