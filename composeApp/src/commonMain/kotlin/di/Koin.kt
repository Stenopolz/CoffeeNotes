package di

import androidx.room.RoomDatabase
import database.CoffeeDatabase
import database.CoffeeDatabase.Companion.getCoffeeDatabase
import database.DatabaseBackupManager
import domain.CoffeeRepository
import domain.CoffeeRepositoryImpl
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import ui.addcoffeescreen.AddCoffeeScreenModel
import ui.addnewrecipescreen.AddNewRecipeScreenModel
import ui.coffeedetailsscreen.CoffeeDetailsScreenModel
import ui.editcoffeescreen.EditCoffeeScreenModel
import ui.editrecipescreen.EditRecipeScreenModel
import ui.homescreen.HomeScreenModel

expect fun platformModule(): Module

fun initKoin(config: KoinAppDeclaration? = null) =
    startKoin {
        config?.invoke(this)
        modules(
            platformModule(),
            repositoryModule,
            screenViewModels,
        )
    }

val repositoryModule = module {
    single<CoffeeRepository> {
        CoffeeRepositoryImpl(
            getDatabase = { getCoffeeDatabase(get<RoomDatabase.Builder<CoffeeDatabase>>()) }
        )
    }
}

val screenViewModels = module {
    factory {
        HomeScreenModel(
            repository = get(),
            databaseBackupManager = get<DatabaseBackupManager>()
        )
    }
    factory { params ->
        CoffeeDetailsScreenModel(
            coffee = params.get(),
            repository = get(),
            navigateBack = params.get(),
        )
    }
    factory { params ->
        AddNewRecipeScreenModel(
            coffee = params.get(),
            repository = get(),
            navigateBack = params.get(),
        )
    }
    factory { params ->
        AddCoffeeScreenModel(
            repository = get(),
            navigateBack = params.get(),
        )
    }
    factory { params ->
        EditRecipeScreenModel(
            recipe = params.get(),
            repository = get(),
            navigateBack = params.get(),
            navigateForward = params.get()
        )
    }
    factory { params ->
        EditCoffeeScreenModel(
            coffee = params.get(),
            repository = get(),
            navigateBack = params.get(),
        )
    }
}
