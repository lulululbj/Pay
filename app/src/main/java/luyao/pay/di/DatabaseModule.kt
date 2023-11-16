package luyao.pay.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import luyao.pay.model.AppDatabase
import javax.inject.Singleton

/**
 * Description:
 * Author: luyao
 * Date: 2023/8/26 07:55
 */
@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context) = AppDatabase.getInstance(context)

    @Singleton
    @Provides
    fun providePayDao(appDatabase: AppDatabase) = appDatabase.payDao()

    @Singleton
    @Provides
    fun providePayDetailDao(appDatabase: AppDatabase) = appDatabase.payDetailDao()
}