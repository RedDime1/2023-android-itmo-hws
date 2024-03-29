package ru.ok.itmo.example

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import com.google.android.material.navigation.NavigationBarView

const val SAVED_FRAG_COUNT = "num_of_fr"
const val SAVED_STACK = "stack"
const val SAVED_MENU_SIZE = "menu-size"
var counter = 1

class NavigationFragment : Fragment(R.layout.activity_navigation) {
    private class PairInfo(val name: String, val num: Int)
    private var stack = mutableListOf<PairInfo>()
    private lateinit var navigation: NavigationBarView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        counter = 1
        val randomProvider : BusinessLogicProvider by viewModels(ownerProducer = {this})
        val savedStack = savedInstanceState?.getStringArray(SAVED_STACK)?.map {
            PairInfo(it.split(":")[0], it.split(":")[1].toInt())
        }

        if (savedStack.isNullOrEmpty()) {
            randomProvider.randValue.observe(requireActivity()) { _ ->
                stack += PairInfo("A", 1)
                val p = stack.last()
                requireActivity().supportFragmentManager.beginTransaction().add(R.id.container_for_start,
                        InnerFragment::class.java, bundleOf("name" to "A", "number" to 1), "${p.name}:${p.num}")
                    .commit()
            }
        } else {
            stack += savedStack
            savedStack.map { it to requireActivity().supportFragmentManager.findFragmentByTag("${it.name}:${it.num}")!! }.run {
                requireActivity().supportFragmentManager.beginTransaction().show(first().second).commit()
                zipWithNext().forEach { (left, right) ->
                    requireActivity().supportFragmentManager.beginTransaction().show(right.second)
                        .hide(left.second).addToBackStack("${right.first}").commit()
                }
            }
        }
        navigation = view.findViewById(R.id.bottom_menu)
        navigation.setOnItemSelectedListener {
            val option = when (it.itemId) {
                R.id.menu_item_a -> "A"
                R.id.menu_item_b -> "B"
                R.id.menu_item_c -> "C"
                R.id.menu_item_d -> "D"
                R.id.menu_item_e -> "E"
                else -> ""
            }
            pushNew(option)
            true
        }

        navigation.menu.run {
            val count = savedInstanceState?.getInt(SAVED_MENU_SIZE) ?: (3..5).random()
            if (count < 5) {
                removeItem(R.id.menu_item_e)
            }
            if (count < 4) {
                removeItem(R.id.menu_item_d)
            }
        }

        setFragmentResultListener(SAVED_FRAG_COUNT) { _, bundle ->
            counter += if (bundle.getBoolean(SAVED_FRAG_COUNT)) 1 else -1
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putStringArray(SAVED_STACK, stack.map { "${it.name}:${it.num}" }.toTypedArray())
        outState.putInt(SAVED_MENU_SIZE, navigation.menu.size())
    }

    fun pushNew(name: String, createNew: Boolean = false) {
        if (name in stack.map { it.name } && !createNew) {
            stack.takeLastWhile { it.name != name }.forEach { _ ->
                requireActivity().supportFragmentManager.popBackStack()
            }
            stack = stack.dropLastWhile { it.name != name }.toMutableList()
        } else {
            val num = stack.count { it.name == name }
            val info = PairInfo(name, num + 1)
            val p = stack.last()
            val prev = requireActivity().supportFragmentManager.findFragmentByTag("${p.name}:${p.num}")
            requireActivity().supportFragmentManager.beginTransaction()
                .add(R.id.container_for_start, InnerFragment::class.java,
                    bundleOf("name" to info.name, "number" to num + 1),
                    "${info.name}:${info.num}"
                ).hide(prev!!).addToBackStack("${info.name}:${info.num}").commit()
            stack += info
        }
    }
}