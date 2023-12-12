package com.example.room2

import android.app.Application
import android.os.AsyncTask
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData

/**
 * ViewModel 封装数据操作类
 *
 * @author 高超（gaochao.cc）
 * @since 2023/12/7
 */
class StudentViewModel(application: Application) : AndroidViewModel(application) {

    private val studentDao = MyDatabase.getInstance(application).getStudentDao()

    fun insert(vararg student: Student) {
        InsertStudentTask(studentDao).execute(*student)
    }

    fun delete(vararg student: Student) {
        DeleteStudentTask(studentDao).execute(*student)
    }

    fun deleteAll() {
        DeleteAllStudentsTask(studentDao).execute()
    }

    fun modify(vararg student: Student) {
        ModifyStudentTask(studentDao).execute(*student)
    }

    fun queryAll(vararg student: Student): LiveData<List<Student>> {
        return studentDao.queryAll()
    }
}

class InsertStudentTask(private val studentDao: StudentDao) : AsyncTask<Student, Void, Void>() {
    override fun doInBackground(vararg params: Student): Void? {
        studentDao.insert(*params)
        return null
    }
}

class DeleteStudentTask(private val studentDao: StudentDao) : AsyncTask<Student, Void, Void>() {
    override fun doInBackground(vararg params: Student): Void? {
        studentDao.delete(*params)
        return null
    }
}

class DeleteAllStudentsTask(private val studentDao: StudentDao) : AsyncTask<Void, Void, Void>() {
    override fun doInBackground(vararg params: Void): Void? {
        studentDao.deleteAll()
        return null
    }
}

class ModifyStudentTask(private val studentDao: StudentDao) : AsyncTask<Student, Void, Void>() {
    override fun doInBackground(vararg params: Student): Void? {
        studentDao.update(*params)
        return null
    }
}
