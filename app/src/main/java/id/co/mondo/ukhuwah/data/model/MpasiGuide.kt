package id.co.mondo.ukhuwah.data.model

import id.co.mondo.ukhuwah.R

data class MpasiGuide(
    val id: Int,
    val url: Int,
    val title: String,
    val fase: String,
    val desc: String,
    val need: String?,
    val step: Guide,
)

data class Guide(
    val stepOne: String,
    val stepTwo: String,
    val stepThree: String,
    val stepFour: String,
)


val MpasiGuideList = listOf(
    MpasiGuide(
        id = 1,
        url = R.drawable.disaring,
        title = "Bayi 6 - 8 Bulan",
        fase = "FASE DISARING",
        desc = "Makanan dibuat dengan disaring Tekstur makanan lumat dan kental",
        need = "800 ml/hari (±3 gelas belimbing)",
        step = Guide(
            stepOne = "Lanjutkan menyusui.",
            stepTwo = "Berikan 2 - 3 sdm bertahap hingga 1/2 mangkok berukuran 250 ml (125 ml) setiap kali makan. (125 ml).",
            stepThree = "Berikan 2 - 3 kali makanan utama dan 1 - 2 kali makanan selingan setiap hari",
            stepFour = "Jumlah energi dari MPASI yang dibutuhkan per hari 200 kkal."
        )
    ),
    MpasiGuide(
        id = 2,
        url = R.drawable.dicincang,
        title = "Bayi 9 - 11 Bulan",
        fase = "FASE DICINCANG",
        desc = "Bahan makanan sama dengan untuk orang dewasa. Tekstur makanan dicincang. Perhatikan respons anak saat makan",
        need = "",
        step = Guide(
            stepOne = "Lanjutkan menyusui.",
            stepTwo = "Berikan 1⁄2 - 3⁄4 mangkok ukuran 250 ml (125 - 200 ml) setiap kali makan",
            stepThree = "Berikan 3 - 4 kali makanan utama dan 1 - 2 kali makanan selingan setiap hari.",
            stepFour = "Jumlah energi dari MPASI yang dibutuhkan per hari 300 kkal."
        )
    ),
    MpasiGuide(
        id = 3,
        url = R.drawable.biasa,
        title = "Bayi 12 - 23 Bulan",
        fase = "MASAKAN BIASA",
        desc = "Bahan makanan sama dengan untuk orang dewasa. Tekstur makanan yang diiris-iris. Perhatikan respons anak saat makan.",
        need = "1.300 ml/hari (±5 gelas)",
        step = Guide(
            stepOne = "Lanjutkan menyusui hingga 2 tahun atau lebih.",
            stepTwo = "3⁄4 - 1 mangkok ukuran 250 ml setiap kali makan",
            stepThree = "Berikan 3 - 4 kali makanan utama dan 1 - 2 kali makanan selingan setiap har",
            stepFour = "Jumlah energi dari MPASI yang dibutuhkan per hari 550 kkal."
        )
    )
)
