package com.teamdefine.farmapp.app.onboarding

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.teamdefine.farmapp.MainActivity
import com.teamdefine.farmapp.MainActivityVM
import com.teamdefine.farmapp.R
import com.teamdefine.farmapp.app.utils.Utility.toast
import com.teamdefine.farmapp.buyer.MainBuyerActivity
import com.teamdefine.farmapp.databinding.FragmentOnBoardingBinding
import com.teamdefine.farmapp.farmer.main.MainFarmerActivity


class OnBoardingFragment : Fragment() {
    private lateinit var binding: FragmentOnBoardingBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var googleSignInClient: GoogleSignInClient
    var mainActivityVM: MainActivityVM? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = FragmentOnBoardingBinding.inflate(inflater, container, false).also {
        binding = it
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()
        initializeGoogleAuthentication()
        mainActivityVM = (activity as MainActivity).mainActivityVM
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObservers()
        setupComposeView()
    }

    private fun initObservers() {
        mainActivityVM?.userIsFarmer?.observe(requireActivity(), Observer {
            it?.let { isFarmer ->
                if (isFarmer) {
                    startFarmerActivity(true)
                } else {
                    startBuyerActivity(true)
                }
            }
        })

        mainActivityVM?.progressLoad?.observe(requireActivity(), Observer {
            it?.let { loadProgressBar ->
                if (!loadProgressBar) {
                    binding.progressBar.visibility = View.GONE
                }
            }
        })
    }

    private fun setupComposeView() {
        binding.composeView.setContent {
            GoogleAuthenticationButton()
        }
    }

    private fun initializeGoogleAuthentication() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Preview
    @Composable
    fun GoogleAuthenticationButton() {
        var clicked by remember {
            mutableStateOf(false)
        }

        Surface(
            onClick = {
                clicked = !clicked
                authenticateWithGoogle()
            },
            shape = RoundedCornerShape(4.dp),
            border = BorderStroke(width = 1.dp, color = Color.LightGray),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.animateContentSize(
                animationSpec = tween(
                    300,
                    easing = LinearOutSlowInEasing
                )
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(
                    start = 12.dp,
                    end = 16.dp,
                    top = 16.dp,
                    bottom = 16.dp
                )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_google),
                    contentDescription = "",
                    tint = Color.Unspecified
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = getString(R.string.google_authenticate_button_text),
                    color = Color(0xFF808080),
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(R.font.noto_devnagri))
                )
            }
        }
    }

    private fun authenticateWithGoogle() {
        binding.progressBar.visibility = View.VISIBLE
        launcher.launch(googleSignInClient.signInIntent)
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            Log.i("OnBoarding Auth", "${result.resultCode}, ${Activity.RESULT_OK}")
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                firebaseAuth.fetchSignInMethodsForEmail(task.result.email.toString())
                    .addOnCompleteListener { work ->
                        if (work.isSuccessful) {
                            val result = work.result?.signInMethods
                            if (result.isNullOrEmpty())
                                handleResults(task, true)
                            else
                                handleResults(task, false)
                        } else {
                            binding.progressBar.visibility = View.GONE
                            toast("oops!")
                        }
                    }
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }

    private fun handleResults(task: Task<GoogleSignInAccount>, saveToDB: Boolean) {
        if (task.isSuccessful) {
            val account: GoogleSignInAccount? = task.result
            account?.let {
                updateUI(it, saveToDB)
            }
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun updateUI(account: GoogleSignInAccount, saveToDB: Boolean) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                if (saveToDB) {
                    showUserTypeAlertDialog()
                } else {
                    checkIfUserIsFarmerOrBuyer(firebaseAuth, firebaseFirestore)
                }
            } else {
                toast("Some error occurred")
            }
        }
    }

    private fun checkIfUserIsFarmerOrBuyer(
        firebaseAuth: FirebaseAuth,
        firebaseFirestore: FirebaseFirestore
    ) {
        mainActivityVM?.checkIfUserIsFarmerOrBuyer(firebaseAuth, firebaseFirestore)
    }

    private fun startBuyerActivity(isRegistered: Boolean) {
        startActivity(
            Intent(
                requireActivity(),
                MainBuyerActivity::class.java
            ).putExtra("isRegistered", isRegistered)
        )
        activity?.finish()
    }

    private fun startFarmerActivity(isRegistered: Boolean) {
        startActivity(
            Intent(
                requireActivity(),
                MainFarmerActivity::class.java
            ).putExtra("isRegistered", isRegistered)
        )
        activity?.finish()
    }

    private fun showUserTypeAlertDialog() {
        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        alertDialog.setTitle(getString(R.string.who_are_you))
        val selections = arrayOf(getString(R.string.farmer), getString(R.string.buyer))
        val checkedItem = 0
        alertDialog.setSingleChoiceItems(
            selections, checkedItem
        ) { dialog, option ->
            when (option) {
                0 -> {
                    startFarmerActivity(false)
                }
                1 -> {
                    startBuyerActivity(false)
                }
            }
        }
        val alert: AlertDialog = alertDialog.create()
        alert.setCanceledOnTouchOutside(false)
        alert.show()
    }
}