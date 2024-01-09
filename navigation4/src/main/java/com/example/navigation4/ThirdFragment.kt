package com.example.navigation4

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.navigation4.databinding.FragmentThirdBinding

class ThirdFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("GAOCHAO", "[${this.hashCode()}] ${this::class.java.simpleName} onCreate")
        lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                Log.d(
                    "GAOCHAO",
                    "[${this@ThirdFragment.hashCode()}] ${ThirdFragment::class.java.simpleName} lifecycle changed: $event"
                )
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        Log.d("GAOCHAO", "[${this.hashCode()}] ${this::class.java.simpleName} onCreateView")
        val viewBinding = FragmentThirdBinding.inflate(inflater, container, false)
        viewBinding.btnJump.setOnClickListener {
            onJump(it)
        }
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("GAOCHAO", "[${this.hashCode()}] ${this::class.java.simpleName} onViewCreated")
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        Log.d("GAOCHAO", "[${this.hashCode()}] ${this::class.java.simpleName} onViewStateRestored")
    }

    override fun onStart() {
        super.onStart()
        Log.d("GAOCHAO", "[${this.hashCode()}] ${this::class.java.simpleName} onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("GAOCHAO", "[${this.hashCode()}] ${this::class.java.simpleName} onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d("GAOCHAO", "[${this.hashCode()}] ${this::class.java.simpleName} onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("GAOCHAO", "[${this.hashCode()}] ${this::class.java.simpleName} onStop")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d("GAOCHAO", "[${this.hashCode()}] ${this::class.java.simpleName} onSaveInstanceState")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("GAOCHAO", "[${this.hashCode()}] ${this::class.java.simpleName} onDestroyView")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("GAOCHAO", "[${this.hashCode()}] ${this::class.java.simpleName} onDestroy")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d("GAOCHAO", "[${this.hashCode()}] ${this::class.java.simpleName} onDetach")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d("GAOCHAO", "[${this.hashCode()}] ${this::class.java.simpleName} onAttach")
    }

    fun onJump(view: View) {
//        findNavController().popBackStack(R.id.homeFragment, false)
        findNavController().navigate(
            ThirdFragmentDirections.actionThirdFragmentToHomeFragment(),
            NavOptions.Builder()
                .setPopUpTo(
                    destinationId = R.id.homeFragment,
                    inclusive = true
                ) // 把 homefragment 也弹出栈
                .setEnterAnim(R.anim.slide_in_left)
                .setExitAnim(R.anim.slide_out_left)
                .build()
        )
    }
}