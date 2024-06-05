package anubhav.github.finder

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import anubhav.github.finder.data.SearchOrder
import anubhav.github.finder.data.searchOrderName
import anubhav.github.finder.databinding.ActivityMainBinding
import anubhav.github.finder.delegates.RxJavaHandler
import anubhav.github.finder.delegates.RxJavaHandlerImpl
import anubhav.github.finder.global.MyApp
import anubhav.github.finder.global.MyApp.Companion.appContext
import anubhav.github.finder.ui.GitHubViewModel
import anubhav.github.finder.utils.device.Huawei
import anubhav.github.finder.utils.getMutatedIcon
import anubhav.github.finder.utils.sdkAbove
import anubhav.github.finder.widget.FocusSearchView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.elevation.SurfaceColors
import io.reactivex.rxjava3.subjects.PublishSubject
import anubhav.github.finder.R as CommonR
import anubhav.github.finder.R.string as stringRes

class MainActivity : AppCompatActivity(),
    RxJavaHandler by RxJavaHandlerImpl()
{

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private val toolbar: MaterialToolbar get() = binding.toolbar


    private val viewModel by viewModels<GitHubViewModel>()
    private val searchQuery: PublishSubject<String> = PublishSubject.create()

    private var favouritesItem: MenuItem? = null
    private var searchMenuItem: MenuItem? = null
    private var searchOrderMenu: Pair<MenuItem, List<MenuItem>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupNavBar()
        searchQuery.debounceOnMain(500) {
            viewModel.updateSearchQuery(it, MyApp.preference.searchOrderType())
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            askForPermissions()
    }

    override fun onDestroy() {
        super.onDestroy()
        favouritesItem = null
        searchMenuItem = null
        searchOrderMenu = null
        _binding = null
    }

    private fun setupToolbar() {
        toolbar.title = getString(stringRes.app_name)
        // Move focus from SearchView to Toolbar
        toolbar.isFocusable = true

        val searchView = FocusSearchView(toolbar.context).apply {
            maxWidth = Int.MAX_VALUE
            queryHint = getString(stringRes.search)
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    searchQuery.onNext(query.orEmpty().trim())
                    clearFocus()
                    return true
                }
                override fun onQueryTextChange(newText: String?) : Boolean {
                    //if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) { }
                    searchQuery.onNext(newText.orEmpty().trim())
                    return true
                }
            })
        }

        toolbar.menu.apply {
            if (!Huawei.isHuaweiEmui) {
                sdkAbove(Build.VERSION_CODES.P) {
                    setGroupDividerEnabled(true)
                }
            }

            searchMenuItem = add(0, CommonR.id.toolbar_search, 0, stringRes.search)
                .setIcon(toolbar.context.getMutatedIcon(CommonR.drawable.ic_search))
                .setActionView(searchView)
                .setShowAsActionFlags(
                    MenuItem.SHOW_AS_ACTION_ALWAYS or MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW
                )

            searchOrderMenu = addSubMenu(0, 0, 0, stringRes.searching_order)
                .setIcon(toolbar.context.getMutatedIcon(CommonR.drawable.ic_sort))
                .let { menu ->
                    menu.item.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
                    val menuItems = SearchOrder.entries.map { searchOrder ->
                        menu.add(applicationContext.searchOrderName(searchOrder))
                            .setOnMenuItemClickListener {
                                updateSearchOrder(searchOrder)
                                MyApp.preference.searchOrder(searchOrder.name)
                                true
                            }
                    }
                    menu.setGroupCheckable(0, true, true)
                    Pair(menu.item, menuItems)
                }

            /*favouritesItem = add(1, 0, 0, stringRes.favourites)
                .setIcon(toolbar.context.getMutatedIcon(CommonR.drawable.ic_favourite_checked))
                .setOnMenuItemClickListener { true }
            add(1, 0, 0, stringRes.settings).setOnMenuItemClickListener { true }*/

            updateSearchOrder(MyApp.preference.searchOrderType())
        }
    }

    private fun setupNavBar() {
        // https://stackoverflow.com/questions/50502269/illegalstateexception-link-does-not-have-a-navcontroller-set
        val navController = Navigation.findNavController(this, CommonR.id.nav_host_fragment_activity_main)

        //AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController)

        //WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        window.navigationBarColor = SurfaceColors.SURFACE_2.getColor(this)
    }

    private fun updateSearchOrder(searchOrder: SearchOrder) {
        searchOrderMenu!!.second[searchOrder.ordinal].isChecked = true
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private fun askForPermissions() {
        if (ActivityCompat.checkSelfPermission(appContext, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 0)
        }
    }

}