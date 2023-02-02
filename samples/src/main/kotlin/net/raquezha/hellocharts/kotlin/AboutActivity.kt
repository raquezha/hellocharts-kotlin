package net.raquezha.hellocharts.kotlin

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.TIRAMISU
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import net.raquezha.hellocharts.kotlin.databinding.FragmentAboutBinding

class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                add(R.id.container, PlaceholderFragment())
            }
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    class PlaceholderFragment : Fragment() {

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            val binding = FragmentAboutBinding.inflate(layoutInflater)
            binding.version.text = getVersionName(requireContext())
            binding.goToGithub.setOnClickListener {
                showLinkToBrowser(requireContext(), GITHUB_URL)
            }
            return binding.root
        }
    }

    companion object {
        private val TAG: String = AboutActivity::class.java.simpleName
        const val GITHUB_URL = "https://github.com/raquezha/hellocharts"
        fun getVersionName(context: Context): String {
            return try {
                if (SDK_INT >= TIRAMISU) {
                    val pInfo = context.packageManager.getPackageInfo(
                        context.packageName,
                        PackageManager.PackageInfoFlags.of(0)
                    )
                    pInfo.versionName
                } else {
                    @Suppress("DEPRECATION")
                    context.packageManager.getPackageInfo(context.packageName, 0).versionName
                }
            } catch (e: Exception) {
                Log.e(TAG, "Could not get version number")
                ""
            }
        }

        fun showLinkToBrowser(context: Context, url: String) {
            try {
                Intent(Intent.ACTION_VIEW, Uri.parse(url)).run(context::startActivity)
            } catch (exception: Exception) {
                Toast.makeText(
                    /* context = */ context,
                    /* text = */ "Can't open link. Please install Google Chrome browser.",
                    /* duration = */ Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}