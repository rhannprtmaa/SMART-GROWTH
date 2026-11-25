package id.co.raihan.smartgrowth.data.model

import id.co.raihan.smartgrowth.R

enum class Article(
    val title: String,
    val image: Int,
    val url: String
){
    ONE(
        title = "Cegah Stunting Sebelum Genting",
        image = R.drawable.a7,
        url = "https://www.kompas.id/artikel/jangan-tunggu-genting-dalam-mencegah-stunting"
    ),
    TWO(
        title = "Program Ini Jadi Andalan Pemerintah Transformasi Layanan Kesehatan",
        image = R.drawable.a2,
        url = "https://www.cnbcindonesia.com/news/20251029210201-4-680524/program-ini-jadi-andalan-pemerintah-transformasi-layanan-kesehatan"
    ),
    THREE(
        title = "Stunting di Indonesia: Penyebab, Dampak, dan Upaya Pencegahan",
        image = R.drawable.a3,
        url = "https://rspp.co.id/artikel-detail-734-Stunting-di-Indonesia-Penyebab,-Dampak,-dan-Upaya-Pencegahan-.html"
    ),
    FOUR(
        title = "Informasi Penting tentang Pemberian MPASI pada Bayi",
        image = R.drawable.a4,
        url = "https://www.alodokter.com/informasi-penting-tentang-pemberian-mpasi-pada-bayi"
    ),
    FIVE(
        title = "12 Pertolongan Pertama pada Bayi yang Terkena Demam Berdarah",
        image = R.drawable.a5,
        url = "https://www.klikdokter.com/ibu-anak/kesehatan-bayi/pertologan-bayi-demam-berdara"
    ),
    SIX(
        title = "MPASI Dapat Cegah Stunting, Begini Penjelasannya!",
        image = R.drawable.a6,
        url = "https://unusa.ac.id/2023/02/01/mpasi-dapat-cegah-stunting-begini-penjelasannya/"
    ),
    SEVEN(
        title = "Pencegahan stunting harus dimulai sejak masa kehamilan dan berlanjut hingga anak berusia dua tahun dengan memastikan pemenuhan gizi yang optimal.",
        image = R.drawable.bubur,
        url = "https://www.halodoc.com/artikel/stunting-adalah-info-lengkap-dan-cara-mencegahnya"
    ),
    EIGHT(
        title = "Cara Mendeteksi Bayi Gagal Tumbuh, Ukur Berat dan Tinggi Badan",
        image = R.drawable.a8,
        url = "https://www.tempo.co/arsip/cara-mendeteksi-bayi-gagal-tumbuh-ukur-berat-dan-tinggi-badan--848388"
    )

}