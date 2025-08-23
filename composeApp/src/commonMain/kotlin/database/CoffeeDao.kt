package database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface CoffeeDao {
     @Insert
     suspend fun insertCoffee(coffee: CoffeeEntity)

     @Query("SELECT * FROM coffee WHERE id = :id")
     suspend fun getCoffeeById(id: Int): CoffeeEntity?

    @Query("SELECT * FROM coffee")
     suspend fun getAllCoffee(): List<CoffeeEntity>

     @Delete
     suspend fun deleteCoffee(coffee: CoffeeEntity)

     @Update
     suspend fun updateCoffee(coffee: CoffeeEntity)
}
