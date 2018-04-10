package com.sub6resources.utilities.sample

import com.sub6resources.utilities.BaseApplication
import com.sub6resources.utilities.logged
import com.sub6resources.utilities.sample.api.LoginApi
import com.sub6resources.utilities.sample.viewmodels.LoginViewModel
import org.koin.android.architecture.ext.viewModel
import org.koin.dsl.module.applicationContext
import retrofit2.Retrofit

class App: BaseApplication(appModule)

val appModule = applicationContext {
    val retrofit = Retrofit.Builder().logged("http://example.com:5432")

    //APIs
    provide { retrofit.create(LoginApi::class.java) }

    //Repositories
    provide { LoginRepository(get()) }

    //ViewModels
    viewModel { LoginViewModel(get()) }
}