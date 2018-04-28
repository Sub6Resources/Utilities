package com.sub6resources.utilities.sample

import com.sub6resources.utilities.*

class LegalActivity: BaseLegalActivity() {
    override val legal = legalActivity {
        title = "Legal"
        group {
            copyright = "Copyright (c) 2018 Sub 6 Resources"
        }
        group {
            title = "Terms and Privacy"

            terms {
                title = "Terms of Service"
                lastUpdatedText = "Updated 4/23/18"
                text = "You may use this app if you can.\nSub 6 Resources is not responsible for any Unicorns attempting to exist in (or near) Alaska."
            }

            privacy {
                title = "Privacy Policy"
                lastUpdatedText = "Does Not Exist :("
            }
        }
        group {
            title = "3rd Party Libraries"

            acknowledgement {
                title = "MyLibrary"
                copyright = "Copyright 2048 Nobody"
                license = License.MIT(copyright)
            }

            acknowledgement {
                title = "BoringLibrary"
                license = License.MIT_GENERIC
            }

            acknowledgement {
                title = "SomeOtherLibrary"
                copyright = "Copyright (c) Some Dude, 2016"
                license {
                    title = "My Favorite License"
                    text = "You can do whatever you can. You may do nothing."
                }
            }

            acknowledgement {
                title = "LuckyLibrary"
                copyright = "(c) 2017 The Three Amigos"
                license = License.APACHE(copyright)
            }

            acknowledgement {
                title = "Meow"
                license = License.APACHE_GENERIC
            }
        }
    }
}