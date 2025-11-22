package com.kaankilic.discoverybox.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kaankilic.discoverybox.datasource.DiscoveryBoxDataSource
import com.kaankilic.discoverybox.repo.DiscoveryBoxRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun provideDiscoveryBoxRepository(dbds : DiscoveryBoxDataSource) : DiscoveryBoxRepository {
        return DiscoveryBoxRepository(dbds)

    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore() : FirebaseFirestore{
        return FirebaseFirestore.getInstance()
    }
    @Provides
    @Singleton
    fun provideFirebaseAuth() : FirebaseAuth{
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun  provideDiscoveryBoxDataSource(
        firestore : FirebaseFirestore, 
        auth: FirebaseAuth,
        @ApplicationContext context: Context
    ): DiscoveryBoxDataSource{
        return DiscoveryBoxDataSource(firestore, auth, context)
    }










}