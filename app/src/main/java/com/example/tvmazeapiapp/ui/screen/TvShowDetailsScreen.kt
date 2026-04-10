package com.example.tvmazeapiapp.ui.screen

import android.text.Html
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tvmazeapiapp.viewmodel.TvShowDetailsViewModel
import androidx.compose.runtime.getValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tvmazeapiapp.data.repository.TvShowRepository
import com.example.tvmazeapiapp.viewmodel.TvShowDetailsViewModelFactory

@Composable
fun LabeledText(label: String, value: String) {
    Row {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        Text(
            text = (": "),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        Text(
            text = value,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TvShowDetailsScreen(
    id: Int,
    repository: TvShowRepository,
    onBackClick: () -> Unit,
    viewModel: TvShowDetailsViewModel = viewModel(
        factory = TvShowDetailsViewModelFactory(repository)
    )
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadShow(id)
    }

    state?.let { show ->
        Scaffold(
            topBar = {
                TopAppBar( title = { Text(text = show.name) })
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
            ) {
                Button(
                    onClick = onBackClick
                ) {
                    Text("Back")
                }

                Spacer(modifier = Modifier.height(5.dp))

                if (!show.network?.country?.name.isNullOrEmpty()) {
                    LabeledText("Country", show.network?.country?.name ?: "" )
                } else {
                    LabeledText("Country", "Not specified")
                }

                Spacer(modifier = Modifier.height(5.dp))

                if (!show.network?.holder.isNullOrEmpty()) {
                    LabeledText("TV company", show.network?.holder ?: "" )
                } else {
                    LabeledText("TV company", "Not specified")
                }

                Spacer(modifier = Modifier.height(5.dp))

                if (!show.officialSite.isNullOrEmpty()) {
                    LabeledText("Website", show.officialSite ?: "" )
                } else {
                    LabeledText("Website", "Not specified")
                }

                Spacer(modifier = Modifier.height(5.dp))

                LabeledText("Status", show.status)

                Spacer(modifier = Modifier.height(5.dp))

                LabeledText("Premiered", show.premiered)

                Spacer(modifier = Modifier.height(5.dp))

                if (!show.ended.isNullOrEmpty()) {
                    LabeledText("Ended", show.ended)
                } else {
                    LabeledText("Ended", "Still running")
                }

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = "Description:",
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(5.dp))

                val cleanSummary = remember(show.summary) {
                    if (show.summary != null) {
                        Html.fromHtml(show.summary, Html.FROM_HTML_MODE_COMPACT).toString()
                    } else {
                        "No summary available"
                    }
                }

                Text(text = cleanSummary)
            }
        }
    }
}