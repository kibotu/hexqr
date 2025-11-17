package net.kibotu.hexqrapp.di

import net.kibotu.hexqrapp.domain.parser.QrCodeParserFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideQrCodeParserFactory(): QrCodeParserFactory {
        return QrCodeParserFactory()
    }
}

