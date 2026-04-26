package com.washwise.mobile.feature.profile.presenter

import com.washwise.mobile.feature.profile.data.UserResponse

/**
 * MVP contract for the Profile tab (home screen for the currently signed-in user).
 */
interface ProfileContract {

    interface View {
        fun renderProfile(profile: UserResponse, initials: String)
        fun renderStats(orders: Int, completed: Int, rating: String)
    }

    interface Presenter {
        fun attach(view: View)
        fun detach()
        fun load()
    }
}
