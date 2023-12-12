package com.example.room3

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.room3.databinding.ActivityMainBinding

/**
 * 演示 Room + ViewModel + LiveData + DataBinding(单向) 的使用方式，当底层数据库信息更新，上层 UI 自动感知
 */
class MainActivity : AppCompatActivity() {

    private lateinit var recyclerViewAdapter: RecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dataBinding =
            DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        dataBinding.viewModel = ViewModelProvider(this)[StudentViewModel::class.java]
        recyclerViewAdapter = RecyclerViewAdapter(listOf())
        dataBinding.recyclerview.adapter = recyclerViewAdapter
        dataBinding.recyclerview.layoutManager = LinearLayoutManager(this)
        (dataBinding.viewModel as StudentViewModel).queryAll().observe(this) {
            recyclerViewAdapter.updateData(it)
            recyclerViewAdapter.notifyDataSetChanged()
        }
    }
}

class RecyclerViewAdapter(private var students: List<Student>) :
    RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    fun updateData(students: List<Student>) {
        this.students = students
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val student = students[position]
        holder.itemView.findViewById<TextView>(R.id.tv_id).text = student.id.toString()
        holder.itemView.findViewById<TextView>(R.id.tv_name).text = student.name
        holder.itemView.findViewById<TextView>(R.id.tv_age).text = student.age.toString()
    }

    override fun getItemCount(): Int {
        return students.size
    }
}

@Entity(tableName = "student")
class Student(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id", typeAffinity = ColumnInfo.INTEGER)
    val id: Int = 0,

    @ColumnInfo(name = "name", typeAffinity = ColumnInfo.TEXT)
    val name: String = "",

    @ColumnInfo(name = "age", typeAffinity = ColumnInfo.INTEGER)
    val age: Int = 0,

    @ColumnInfo(name = "sex", typeAffinity = ColumnInfo.TEXT)
    val sex: String = "M",

    @ColumnInfo(name = "score", typeAffinity = ColumnInfo.INTEGER)
    val score: Int = 0,
) {
    companion object {
        @JvmStatic
        fun create(id: Int, name: String, age: Int): Student {
            return Student(id = id, name = name, age = age)
        }
    }
}

@Dao
interface StudentDao {

    @Insert
    fun insert(vararg student: Student)

    @Delete
    fun delete(vararg student: Student)

    @Query("DELETE FROM student")
    fun deleteAll()

    @Update
    fun update(vararg student: Student)

    @Query("SELECT * FROM student")
    fun queryAll(): LiveData<List<Student>>

    @Query("SELECT * FROM student WHERE id = :id")
    fun queryById(id: Int): Student
}

@Database(entities = [Student::class], version = 4, exportSchema = true)
abstract class MyDatabase : RoomDatabase() {
    companion object {
        private const val DATABASE_NAME = "my_db.db"

        private lateinit var INSTANCE: MyDatabase

        @Synchronized
        fun getInstance(context: Context): MyDatabase {
            if (!::INSTANCE.isInitialized) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    MyDatabase::class.java,
                    DATABASE_NAME
                )
                    .createFromAsset("my_pre_db.db") // 指定从 assets 中的某一个文件来创建数据库，以达到数据预填充效果
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                    .fallbackToDestructiveMigration() // 当升级数据库时发生的任何异常都会销毁数据库并且重新建立一个空的表，避免崩溃
                    .build()
            }
            return INSTANCE
        }

        /**
         * 演示数据库从版本 1 到版本 2 的迁移
         */
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                Log.d("GAOCHAO", "升级数据库，版本 1 -> 2")
                db.execSQL("ALTER TABLE student ADD COLUMN sex INTEGER NOT NULL DEFAULT 1")
            }
        }

        /**
         * 演示数据库从版本 2 到版本 3 的迁移
         *
         * 注意：如果用户当前版本为 1，直接升级版本 3 的话，Room 会执行如下动作：
         * 1. 首先看有没有直接从 1 到 3 的升级方案，如果有则执行，否则继续
         * 2. 在看有没有逐步升级方案，如果有，次序执行 1 到 2 升级，然后在 2 到 3 升级
         * 3. 如果找不到任何升级方案，那就报错，启动崩溃，升级失败
         */
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                Log.d("GAOCHAO", "升级数据库，版本 2 -> 3")
                db.execSQL("ALTER TABLE student ADD COLUMN score INTEGER NOT NULL DEFAULT 0")
            }
        }

        /**
         * 演示如何修改旧版本表中的 sex 字段类型，从 INTEGER 修改为 TEXT
         * 这里需要创建新表，然后拷贝旧表数据到新表，然后重命名新表。
         */
        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                Log.d("GAOCHAO", "升级数据库，版本 3 -> 4")
                // 首先创建 temp 表
                db.execSQL(
                    "CREATE TABLE temp_student (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                            "name TEXT NOT NULL," +
                            "age INTEGER NOT NULL," +
                            "sex TEXT NOT NULL DEFAULT \"M\"," +
                            "score INTEGER NOT NULL" +
                            ")"
                )
                // 拷贝旧表数据到 temp 表，这里不需要拷贝 sex 字段，因为 sex 字段类型已经修改
                db.execSQL("INSERT INTO temp_student (name,age,sex,score) SELECT name,age,sex,score FROM student")
                // 删除旧表
                db.execSQL("DROP TABLE student")
                // 重命名 temp 表为旧表名称
                db.execSQL("ALTER TABLE temp_student RENAME TO student")
            }
        }
    }

    abstract fun getStudentDao(): StudentDao
}
