package com.prilepskiy.trenninggpsopenstreetmap.db
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface Dao {
    @Insert
    suspend fun insertTrack(trackItem: TrackItem)

    @Query("SELECT * FROM tRack")
   fun getAllTrack(): Flow<List<TrackItem>>
}