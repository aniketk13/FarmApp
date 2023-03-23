package com.teamdefine.farmapp.authentication

import android.app.Activity
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
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.teamdefine.farmapp.R
import com.teamdefine.farmapp.databinding.FragmentUserAuthenticationBinding
import com.teamdefine.farmapp.utils.Utility.toast

class UserAuthentication : Fragment() {
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var binding: FragmentUserAuthenticationBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentUserAuthenticationBinding.inflate(inflater, container, false).also {
        binding = it

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        firebaseAuth = FirebaseAuth.getInstance()
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupComposeView()
    }

    private fun setupComposeView() {
        binding.composeView.setContent {
            GoogleAuthenticationButton()
        }
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
                signInWithGoogle()
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
                    text = "Authenticate to continue",
                    color = Color(0xFF808080),
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(R.font.noto_devnagri))
                )
            }
        }
    }

    private fun signInWithGoogle() {
        binding.progressBar.visibility = View.VISIBLE
        launcher.launch(googleSignInClient.signInIntent)
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            Log.i("UserAuthenticateResultCode", "${result.resultCode}, ${Activity.RESULT_OK}")
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                firebaseAuth.fetchSignInMethodsForEmail(task.result.email.toString())
                    .addOnCompleteListener { work ->
                        if (work.isSuccessful) {
                            val res = work.result?.signInMethods
                            if (res.isNullOrEmpty())
                                handleResults(task, true)
                            else
                                handleResults(task, false)
                        } else {
                            binding.progressBar.visibility = View.GONE
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
                if (saveToDB)
                    findNavController().navigate(UserAuthenticationDirections.actionUserAuthenticationToUserSelection())
                else {
                    val currentUser = firebaseAuth.currentUser?.uid
                    val user = FirebaseFirestore.getInstance().collection("Farmers")
                        .document(currentUser.toString())
                    user.get().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val doc = task.result
                            if (doc != null) {
                                if (doc.exists()) {
                                    Log.d("UserAuthenticateExists", "Document already exists.")
                                    findNavController().navigate(UserAuthenticationDirections.actionUserAuthenticationToFarmerHomeScreen())
                                } else {
                                    findNavController().navigate(UserAuthenticationDirections.actionUserAuthenticationToBuyerHomeScreen())
                                }
                            } else {
                                Log.e("UserAuthenticateError", "Error: ", task.exception)
                            }
                        }
                    }
                }
            } else {
                toast("Some error occurred")
            }
        }
    }
}