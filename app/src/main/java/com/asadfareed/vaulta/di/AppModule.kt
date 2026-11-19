package com.asadfareed.vaulta.di

import android.content.Context
import androidx.room.Room
import com.asadfareed.vaulta.data.local.TransactionDao
import com.asadfareed.vaulta.data.local.VaultaDatabase
import com.asadfareed.vaulta.data.repository.WalletRepositoryImpl
import com.asadfareed.vaulta.domain.repository.WalletRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import java.time.Clock
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IoDispatcher

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): VaultaDatabase =
        Room.databaseBuilder(context, VaultaDatabase::class.java, VaultaDatabase.NAME).build()

    @Provides
    fun provideTransactionDao(db: VaultaDatabase): TransactionDao = db.transactionDao()

    @Provides
    @Singleton
    fun provideClock(): Clock = Clock.systemDefaultZone()

    @Provides
    @IoDispatcher
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindWalletRepository(impl: WalletRepositoryImpl): WalletRepository
}
