package com.github.fziraki.makemyday.onboarding

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

sealed interface OnboardingPage {

    data object Welcome : OnboardingPage

    data object Setup : OnboardingPage
}
@Composable
fun OnboardingScreen(
    onFinish: () -> Unit,
    onNavigateToLocationSearch: () -> Unit
) {

    val pages = remember {
        listOf(
            OnboardingPage.Welcome,
            OnboardingPage.Setup
        )
    }
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    BackHandler(enabled = pagerState.currentPage > 0) {
        scope.launch {
            pagerState.animateScrollToPage(pagerState.currentPage - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                WindowInsets.systemBars
                    .only(WindowInsetsSides.Top + WindowInsetsSides.Bottom)
                    .asPaddingValues()
            )
            .padding(horizontal = 24.dp)
    ) {

        Spacer(Modifier.height(40.dp))

        DotsIndicator(
            totalDots = pages.size,
            selectedIndex = pagerState.currentPage,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(Modifier.height(32.dp))

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) { page ->

            when (val page = pages[page]) {

                is OnboardingPage.Welcome -> {
                    WelcomePage()
                }

                is OnboardingPage.Setup -> {
                    SetupPage(
                        onNavigateToLocationSearch = onNavigateToLocationSearch
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                if (pagerState.currentPage == pages.lastIndex) {
                    onFinish()
                } else {
                    scope.launch {
                        pagerState.animateScrollToPage(
                            pagerState.currentPage + 1
                        )
                    }
                }
            },
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text(
                text = if (pagerState.currentPage == pages.lastIndex)
                    "Continue"
                else
                    "Get Started"
            )
        }

        Spacer(Modifier.height(12.dp))
    }
}


@Composable
private fun DotsIndicator(
    totalDots: Int,
    selectedIndex: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(totalDots) { index ->

            val selected = index == selectedIndex

            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .size(if (selected) 10.dp else 8.dp)
                    .aspectRatio(1f)
            ) {
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = if (selected)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                    modifier = Modifier.fillMaxSize()
                ) {}
            }
        }
    }
}



