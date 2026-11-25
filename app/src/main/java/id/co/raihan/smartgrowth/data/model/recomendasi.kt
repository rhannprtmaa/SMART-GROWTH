package id.co.raihan.smartgrowth.data.model


data class insertRekomendasiAi(
    val sex: Int,
    val age: Int,
    val height: Double,
    val weight: Double,
)

data class resultRekomendasiAi(
    val ttAge: String,
    val bbAge: String,
    val bbTt: String,
    val desc: String
)