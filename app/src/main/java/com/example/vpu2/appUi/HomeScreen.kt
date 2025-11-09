package com.example.vpu2.appUi

import androidx.compose.material3.MaterialTheme

import androidx.compose.runtime.Composable

import androidx.compose.runtime.collectAsState

import androidx.compose.runtime.getValue

import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.navigation.NavController

@Composable

fun HomeScreen(navController: NavController, viewModel: MainViewModel = viewModel()) {

    val filteredUArchData by viewModel.filteredUArchData.collectAsState()

    val selectedArch by viewModel.selectedArch.collectAsState()

    val status by viewModel.status.collectAsState()

    val uArchData by viewModel.uArchData.collectAsState()

    HomeScreenContent(

        allUArchData = uArchData,

        filteredUArchData = filteredUArchData,

        selectedArch = selectedArch,

        status = status,

        onArchSelect = { arch -> viewModel.setSelectedArch(arch) },

        onArticleClick = { article -> navController.navigate("detail/${article.name}") },

        onFoundationCardClick = { foundationName -> navController.navigate("detail/$foundationName") }

    )

}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)

@Composable

fun HomeScreenPreview() {

    val uArchData = listOf(

        UArch(

            name = "Branch-prediction",

            description = "The branch predictor predicts branches.",

            arch = "foundation",

            date = null,

            images = listOf("")

        ),

        UArch(

            name = "instruction cache",

            description = "The instructions are stored here",

            arch = "foundation",

            date = null,

            images = listOf("")

        ),

        UArch(

            name = "Pipeline",

            description = "scalar or superscalar",

            arch = "foundation",

            date = null,

            images = listOf("")

        ),

        UArch(

            name = "Zen 4",

            description = "Zen 4 is a CPU microarchitecture by AMD featuring significant performance improvements and efficiency gains over previous generations.",

            arch = "x86",

            date = 2022,

            images = listOf("")

        ),

        UArch(

            name = "Intel Alder Lake",

            description = "Intel's 12th generation architecture with hybrid performance design.",

            arch = "x86",

            date = 2021,

            images = listOf("")

        ),

        UArch(

            name = "AMD Zen 3",

            description = "AMD's 3rd generation Zen architecture with significant IPC improvements.",

            arch = "x86",

            date = 2020,

            images = listOf("")

        ),

        UArch(

            name = "Apple M1",

            description = "The Apple M1 is an ARM-based system on a chip designed by Apple for the Mac product line.",

            arch = "arm",

            date = 2020,

            images = listOf("")

        ),

        UArch(

            name = "Apple A15 Bionic",

            description = "Apple's 15th generation mobile processor with enhanced performance and efficiency.",

            arch = "arm",

            date = 2021,

            images = listOf("")

        ),

        UArch(

            name = "ARM Cortex-A78",

            description = "High-performance ARM CPU core designed for mobile devices.",

            arch = "arm",

            date = 2020,

            images = listOf("")

        ),

        UArch(

            name = "RISC-V RVV",

            description = "RISC-V Vector Extension (RVV) is a modern vector processing architecture for RISC-V.",

            arch = "riscv",

            date = 2021,

            images = listOf("")

        ),

        UArch(

            name = "SiFive U74",

            description = "Commercial RISC-V application processor core.",

            arch = "riscv",

            date = 2020,

            images = listOf("")

        )

    )

    MaterialTheme {

        HomeScreenContent(

            allUArchData = uArchData,

            filteredUArchData = uArchData.filter { it.arch == "x86" }, // Show only x86 for preview

            selectedArch = "x86",

            status = "Fetch successful!",

            onArchSelect = {},

            onArticleClick = {},

            onFoundationCardClick = {}

        )

    }

}