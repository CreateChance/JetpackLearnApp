package com.example.coroutine

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * 数据库操作模块
 *
 * @author 高超（gaochao.cc）
 * @since 2023/12/14
 */

@Entity(tableName = "title")
data class Title(

    // 主键不自增，每次都替换为最新的，为了简单测试
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id", typeAffinity = ColumnInfo.INTEGER)
    val id: Int = 0,

    @ColumnInfo(name = "title", typeAffinity = ColumnInfo.TEXT)
    val title: String,
)

@Dao
interface TitleDao {

    // 因为主键不会自动生成，总是为 0，因此这里设置冲突策略为替换，即每次都使用新的值来替换旧版本的
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTitle(title: Title)

    // 总是查询 id 为 0 的那个，也就是唯一的那个
    @get:Query("select * from title where id = 0")
    val titleLiveData: LiveData<Title>
}

@Database(entities = [Title::class], version = 1, exportSchema = true)
abstract class TitleDatabase : RoomDatabase() {

    abstract val titleDao: TitleDao
}

private lateinit var titleDatabase: TitleDatabase

fun getTitleDatabase(context: Context): TitleDatabase {
    synchronized(TitleDatabase::class.java) {
        if (!::titleDatabase.isInitialized) {
            titleDatabase = Room.databaseBuilder(
                context.applicationContext,
                TitleDatabase::class.java,
                "my_title.db"
            )
                .fallbackToDestructiveMigration()
                .build()
        }
    }
    return titleDatabase
}
