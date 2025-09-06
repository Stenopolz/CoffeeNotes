package di

import addcoffeescreen.AddCoffeeScreenModel
import addnewrecipescreen.AddNewRecipeScreenModel
import androidx.room.RoomDatabase
import coffeedetailsscreen.CoffeeDetailsScreenModel
import database.CoffeeDao
import database.CoffeeDatabase
import database.CoffeeDatabase.Companion.getCoffeeDatabase
import database.RecipeDao
import domain.CoffeeRepository
import domain.CoffeeRepositoryImpl
import homescreen.HomeScreenModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

expect fun platformModule(): Module

fun initKoin(config: KoinAppDeclaration? = null) =
    startKoin {
        config?.invoke(this)
        modules(
            platformModule(),
            repositoryModule,
            databaseModule,
            screenViewModels,
        )
    }

val repositoryModule = module {
    factory<CoffeeRepository> {
        CoffeeRepositoryImpl(
            coffeeDao = get(),
            recipeDao = get()
        )
    }
}

val databaseModule = module {
    single<CoffeeDatabase> { getCoffeeDatabase(get<RoomDatabase.Builder<CoffeeDatabase>>()) }
    single<CoffeeDao> {
        get<CoffeeDatabase>().getCoffeeDao()
    }
    single<RecipeDao> {
        get<CoffeeDatabase>().getRecipeDao()
    }
}

val screenViewModels = module {
    factory { HomeScreenModel(get()) }
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
}
