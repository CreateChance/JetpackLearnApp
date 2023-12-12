package com.example.navigation3

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        root.findViewById<Button>(R.id.button).setOnClickListener {
            // 26 及以上需要创建 notification channel
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    requireActivity().packageName,
                    "MyChannel",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                channel.description = "My NotificationChannel"
                val notificationManager =
                    requireActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
            // 创建通知
            val notification =
                NotificationCompat.Builder(requireActivity(), requireActivity().packageName)
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle("Deep Link")
                    .setContentText("点击我试试")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(getPendingIntent())
                    .build()
            val notificationManagerCompat = NotificationManagerCompat.from(requireActivity())
            if (ActivityCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // 固定 id，取代上一个通知，只发布一个通知。
                notificationManagerCompat.notify(0, notification)
            }

        }

        return root;
    }

    private fun getPendingIntent(): PendingIntent {
        // 构建并且传递参数到子 fragment
        val args = DetailFragmentArgs.Builder()
            .setUserName("Jack")
            .setUserAge(20)
            .build()
            .toBundle()
        return Navigation.findNavController(requireActivity(), R.id.button)
            .createDeepLink()
            .setGraph(R.navigation.my_nav_graph)
            .setDestination(R.id.detailFragment)
            .setArguments(args)
            .createPendingIntent()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}