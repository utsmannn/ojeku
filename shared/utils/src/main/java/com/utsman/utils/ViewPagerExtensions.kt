package com.utsman.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager

class AppPagerAdapter(fragmentManager: FragmentManager) :
    FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    var page: List<ViewPagerPage> = mutableListOf()

    override fun getCount(): Int {
        return page.size
    }

    override fun getItem(position: Int): Fragment {
        return page[position].fragment
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return page[position].title
    }
}

data class ViewPagerPage(
    val title: String,
    val fragment: Fragment
)

fun ViewPager.setup(fragmentManager: FragmentManager, vararg pagerPage: ViewPagerPage) {
    val pagerAdapter = AppPagerAdapter(fragmentManager)
    pagerAdapter.page = pagerPage.toList()

    adapter = pagerAdapter
}