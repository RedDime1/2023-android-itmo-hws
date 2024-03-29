package ru.ok.itmo.example

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels

class InnerFragment : Fragment() {
    private lateinit var name: String
    private var number: Int = -239
    private var blogic_val: Int? = null
    private lateinit var counterView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        name = requireArguments().getString("name") ?: "Frame"
        number = requireArguments().getInt("number")
        blogic_val = savedInstanceState?.getInt("blogic_val")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.chosen_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val randomProvider : BusinessLogicProvider by viewModels(ownerProducer = {this})
        counterView = view.findViewById(R.id.number_of_fr);
        counterView.text = counter.toString()
        view.findViewById<TextView>(R.id.fragment_name).text = "$name with number: $number"
        randomProvider.randValue.observe(requireActivity()) {
            blogic_val = blogic_val ?: it
            view.findViewById<TextView>(R.id.random_number).text = "$blogic_val"
        }
        view.findViewById<Button>(R.id.next).setOnClickListener {
            (requireActivity().supportFragmentManager.findFragmentByTag(NAVIGATION) as NavigationFragment).pushNew(name, true)
        }
        setFragmentResult(SAVED_FRAG_COUNT, bundleOf(SAVED_FRAG_COUNT to true))
    }

    override fun onDestroyView() {
        setFragmentResult(SAVED_FRAG_COUNT, bundleOf(SAVED_FRAG_COUNT to false))
        super.onDestroyView()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("blogic_val", blogic_val ?: 0)
    }
}