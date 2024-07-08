package com.farhanadi.moodbotapp.view.home

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.farhanadi.moodbotapp.R

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvNama: TextView = view.findViewById(R.id.tv_nama)

        val firstPart = "Halo, "
        val secondPart = "Adi Satrio"

        val spannable = SpannableStringBuilder().apply {
            append(firstPart)
            append(secondPart)

            setSpan(
                ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.biru4)),
                0, firstPart.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            setSpan(
                ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.black)),
                firstPart.length, firstPart.length + secondPart.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        tvNama.text = spannable
    }
}
