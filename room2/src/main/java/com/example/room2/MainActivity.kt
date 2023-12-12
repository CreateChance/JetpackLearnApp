package com.example.room2

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
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

/**
 * 演示 Room + ViewModel + LiveData 的使用方式，当底层数据库信息更新，上层 UI 自动感知
 */
class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: StudentViewModel
    private lateinit var recyclerViewAdapter: RecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        recyclerViewAdapter = RecyclerViewAdapter(listOf())
        recyclerView.adapter = recyclerViewAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        viewModel = ViewModelProvider(this)[StudentViewModel::class.java]
        viewModel.queryAll().observe(this) {
            recyclerViewAdapter.updateData(it)
            recyclerViewAdapter.notifyDataSetChanged()
        }

    }

    fun insert(view: View) {
        viewModel.insert(
            Student(name = "Jack", age = 20),
            Student(name = "Jim", age = 21),
            Student(name = "Tom", age = 22),
        )
    }

    fun delete(view: View) {
        viewModel.delete(
            Student(id = 2)
        )
    }

    fun modify(view: View) {
        viewModel.modify(
            Student(id = 1, name = "ModifiedName", age = 99)
        )
    }

    fun deleteAll(view: View) {
        viewModel.deleteAll()
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
    val age: Int = 0
)

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

@Database(entities = [Student::class], version = 1, exportSchema = false)
abstract class MyDatabase : RoomDatabase() {
    companion object {
        private const val DATABASE_NAME = "my_db"

        private lateinit var INSTANCE: MyDatabase

        @Synchronized
        fun getInstance(context: Context): MyDatabase {
            if (!::INSTANCE.isInitialized) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    MyDatabase::class.java,
                    DATABASE_NAME
                ).build()
            }
            return INSTANCE
        }
    }

    abstract fun getStudentDao(): StudentDao
}

