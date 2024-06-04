package anubhav.github.finder;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.elevation.SurfaceColors;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import anubhav.github.finder.databinding.ActivityMainBinding;
import anubhav.github.finder.global.MyApp;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupNavBar();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            askForPermissions();
    }

    private void setupNavBar() {
        // https://stackoverflow.com/questions/50502269/illegalstateexception-link-does-not-have-a-navcontroller-set
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);

        //AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        //WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setNavigationBarColor(SurfaceColors.SURFACE_2.getColor(this));
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private void askForPermissions() {
        if (ActivityCompat.checkSelfPermission(MyApp.getAppContext(), android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[] { Manifest.permission.POST_NOTIFICATIONS },
                    0);
        }
    }
}