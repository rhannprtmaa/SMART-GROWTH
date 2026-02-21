package id.co.mondo.ukhuwah.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.co.mondo.ukhuwah.data.dto.ChildWithMeasureDto
import id.co.mondo.ukhuwah.data.dto.InsertNewChildDto
import id.co.mondo.ukhuwah.data.model.AllChild
import id.co.mondo.ukhuwah.data.model.ChildMeasure
import id.co.mondo.ukhuwah.data.model.ChildWithMeasured
import id.co.mondo.ukhuwah.data.model.CountChildMeasure
import id.co.mondo.ukhuwah.data.model.InsertNewChildren
import id.co.mondo.ukhuwah.data.model.ListMeasure
import id.co.mondo.ukhuwah.data.model.NewMeasure
import id.co.mondo.ukhuwah.data.model.ProfileChild
import id.co.mondo.ukhuwah.data.model.UpdateProfileChild
import id.co.mondo.ukhuwah.data.supabase.ChildService
import id.co.mondo.ukhuwah.data.utils.calculateAgeFromBirthToMeasure
import id.co.mondo.ukhuwah.ui.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChildViewModel(
    private val childService: ChildService = ChildService()
) : ViewModel() {

    private val _allChildState = MutableStateFlow<UiState<List<AllChild>>>(UiState.Empty)
    val allChildState: StateFlow<UiState<List<AllChild>>> = _allChildState

    private val _insertNewMeasureState = MutableStateFlow<UiState<String>>(UiState.Empty)
    val insertNewMeasureState: StateFlow<UiState<String>> = _insertNewMeasureState

    private val _countChildMeasrueState =
        MutableStateFlow<UiState<CountChildMeasure>>(UiState.Empty)
    val countChildMeasureState: StateFlow<UiState<CountChildMeasure>> = _countChildMeasrueState

    private val _allChilWithMeasuredState =
        MutableStateFlow<UiState<List<ListMeasure>>>(UiState.Empty)
    val allListMeasureState: StateFlow<UiState<List<ListMeasure>>> =
        _allChilWithMeasuredState

    private val _insertNewChildState = MutableStateFlow<UiState<String>>(UiState.Empty)
    val insertNewChildState: StateFlow<UiState<String>> = _insertNewChildState

    private val _childWithMeasureState = MutableStateFlow<UiState<ChildWithMeasured>>(UiState.Empty)
    val childWithMeasureState: StateFlow<UiState<ChildWithMeasured>> = _childWithMeasureState

    private val _childId = MutableStateFlow<UiState<ProfileChild>>(UiState.Empty)
    val childId: StateFlow<UiState<ProfileChild>> = _childId

    private val _updateChildId = MutableStateFlow<UiState<String>>(UiState.Empty)
    val updateChildId: StateFlow<UiState<String>> = _updateChildId


    private var rawChild: ChildWithMeasureDto? = null

    init {
        getAllChild()
    }


    fun getAllChild() {
        _allChildState.value = UiState.Loading
        viewModelScope.launch {
            val response = childService.getAllChild()
            response.onSuccess { list ->
                val response = list.sortedByDescending { it.id }.map { child ->
                    AllChild(
                        id = child.id,
                        name = child.name.lowercase().replaceFirstChar { it.uppercase() },
                        nik = child.nik ?: ""
                    )
                }
                if (response.isEmpty()) {
                    Log.d("UserViewModel", "Data children Kosong")
                    _allChildState.value = UiState.Empty
                } else {
                    Log.d("UserViewModel", "Get all children sukses: $response")
                    _allChildState.value = UiState.Success(response)
                }
            }.onFailure {
                Log.d("UserViewModel", "Get all children GAGAL: $it")
                _allChildState.value = UiState.Error("Gagal mendapatkan mengambil data")
            }
        }
    }


    fun getCountChildMeasure() {
        viewModelScope.launch {
            val response = childService.getCountChildWithMeasure()
            response.onSuccess { child ->
                val countChild = child.size
                val countMeasure = child.flatMap { it.measurements ?: emptyList() }.size
                val count = CountChildMeasure(
                    countChild = countChild,
                    countMeasure = countMeasure
                )
                Log.d("UserViewModel", "Get count child & measure sukses: $count")
                _countChildMeasrueState.value = UiState.Success(count)
            }.onFailure {
                Log.d(
                    "UserViewModel-[getCountChildMeasure]",
                    "Get count child & measure GAGAL: $it"
                )
                _countChildMeasrueState.value =
                    UiState.Error("Gagal mendapatkan data Jumlah anak dan pengukuran")
            }
        }
    }

    fun getChildById(id: Int) {
        viewModelScope.launch {
            _childId.value = UiState.Loading
            val response = childService.getChildById(id)
            response.onSuccess { child ->
                val setChild = ProfileChild(
                    id = child.id,
                    name = child.name,
                    nik = child.nik.orEmpty(),
                    gender = child.gender,
                    birth = child.birth,
                    heightCm = child.heightCm ?: 0.0,
                    weightKg = child.weightKg ?: 0.0,
                    headCm = child.headCm ?: 0.0
                )
                Log.d("UserViewModel", "Get child by Id sukses: $setChild")
                _childId.value = UiState.Success(setChild)
            }.onFailure {
                Log.d("UserViewModel", "Get child by Id GAGAL: $it")
                _childId.value = UiState.Error("Gagal mengambil data child")
            }
        }
    }

    fun getChildWithMeasure(id: Int) {
        _childWithMeasureState.value = UiState.Loading
        viewModelScope.launch {
            val response = childService.getChildWithMeasureById(id)
            response.onSuccess { data ->
                rawChild = data

                val measures = data.measurements.orEmpty()

                if (measures.isEmpty()) {
                    _childWithMeasureState.value = UiState.Empty
                    return@onSuccess
                }

                val latestMeasure = measures.maxByOrNull { it.measuredAt }

                if (latestMeasure == null) {
                    _childWithMeasureState.value = UiState.Empty
                    return@onSuccess
                }

                val ageResult = calculateAgeFromBirthToMeasure(
                    birth = data.birth,
                    measuredAt = latestMeasure.measuredAt
                )

                val response = ChildWithMeasured(
                    id = data.id,
                    name = data.name,
                    gender = data.gender,
                    ageResult = ageResult,
                    measurement = ChildMeasure(
                        id = latestMeasure.id,
                        measuredAt = latestMeasure.measuredAt,
                        heightCm = latestMeasure.heightCm ?: 0.0,
                        weightKg = latestMeasure.weightKg ?: 0.0,
                        armCm = latestMeasure.armCm ?: 0.0,
                        headCm = latestMeasure.headCm ?: 0.0
                    )
                )
                Log.d("UserViewModel", "Get child with measure sukses: $data")
                _childWithMeasureState.value = UiState.Success(response)
            }.onFailure {
                Log.d("UserViewModel", "Get child with measure GAGAL: $it")
                _childWithMeasureState.value = UiState.Error("Gagal mendapatkan data")
            }
        }
    }

    fun getAllChildMeasure() {
        _allChilWithMeasuredState.value = UiState.Loading
        viewModelScope.launch {
            val response = childService.getListMeasure()
            response.onSuccess {
                val allMeasure = it.flatMap { children ->
                    children.measurements?.map { measure ->
                        val ageResult = calculateAgeFromBirthToMeasure(
                            birth = children.birth,
                            measuredAt = measure.measuredAt
                        )
                        ListMeasure(
                            childrenId = children.id,
                            name = children.name.lowercase().replaceFirstChar { it.uppercase() },
                            measureId = measure.id,
                            measuredAt = measure.measuredAt,
                            ageResult = ageResult,
                            heightCm = measure.heightCm,
                            weightKg = measure.weightKg,
                            armCm = measure.armCm,
                            headCm = measure.headCm
                        )
                    } ?: emptyList()
                }.sortedByDescending { it.measuredAt }
                Log.d("UserViewModel", "Get All Children with measure Sukses: $allMeasure")
                if (allMeasure.isEmpty()) {
                    _allChilWithMeasuredState.value = UiState.Empty
                } else {
                    _allChilWithMeasuredState.value = UiState.Success(allMeasure)
                }
            }.onFailure {
                Log.d("UserViewModel", "Get All Children with measure Gagal: $it")
                _allChilWithMeasuredState.value = UiState.Error("Gagal mendapatkan data")
            }
        }
    }

    fun insertNewMeasure(measure: NewMeasure) {
        if (
            measure.heightCm == null ||
            measure.weightKg == null ||
            measure.armCm == null ||
            measure.headCm == null
        ) {
            _insertNewMeasureState.value =
                UiState.Error("Data pengukuran tidak lengkap")
            return
        }
        Log.d("ChildViewModel", "Insert new measure: $measure")
        _insertNewMeasureState.value = UiState.Loading
        viewModelScope.launch {
            val response = childService.insertNewMeasureChild(measure)
            response.onSuccess {
                Log.d("ChildViewModel", "Insert new measure sukses: $it")
                _insertNewMeasureState.value = UiState.Success("Insert new measure sukses")
            }.onFailure {
                Log.d("ChildViewModel", "Insert new measure GAGAL: $it")
                _insertNewMeasureState.value = UiState.Error("Gagal insert data")
            }
        }
    }

    fun insertNewChildUser(child: InsertNewChildren) {
        _insertNewChildState.value = UiState.Loading
        viewModelScope.launch {
            val setChild = InsertNewChildDto(
                userId = child.userId,
                name = child.name,
                childNo = child.childNo,
                nik = child.nik,
                gender = child.gender,
                birth = child.birth,
                heightCm = child.heightCm,
                weightKg = child.weightKg,
                headCm = child.headCm
            )
            val response = childService.insertNewChildUser(setChild)
            response.onSuccess {
                Log.d("ChildViewModel", "Insert new child sukses: $it")
                _insertNewChildState.value = UiState.Success("Insert new child sukses")
            }.onFailure {
                Log.d("ChildViewModel", "Insert new child GAGAL: $it")
                _insertNewChildState.value = UiState.Error("Gagal insert data")
            }
        }
    }


    fun updateChild(child: UpdateProfileChild) {
        _updateChildId.value = UiState.Loading
        viewModelScope.launch {
            val response = childService.updateChild(child)
            response.onSuccess { child ->
                Log.d("ChildViewModel", "Update profile children sukses: $child")
                getChildById(child.id)
                _updateChildId.value = UiState.Success("Update profile children sukses")
            }.onFailure {
                Log.d("ChildViewModel", "Update profil children GAGAL: $it")
                _updateChildId.value = UiState.Error("Gagal update profil")
            }
        }
    }


}


//    filter bulan tahun next

//    fun childMeasureMonthYearFilter(month: String, year: String) {
//        val child = rawChild
//        if (child == null) {
//            _childWithMeasureState.value = UiState.Empty
//            return
//        }
//
//        val filteredMeasures = child.measurements
//            ?.filter { measure ->
//                val date = measure.measuredAt ?: return@filter false
//
//                val measureYear = date.substring(0, 4)
//                val measureMonth = date.substring(5, 7)
//
//                measureYear == year && measureMonth == month
//            }
//            ?: emptyList()
//
//        if (filteredMeasures.isEmpty()) {
//            _childWithMeasureState.value = UiState.Empty
//            return
//        }
//
//        val latestMeasure = filteredMeasures
//            .maxByOrNull { it.measuredAt }
//
//        if (latestMeasure == null) {
//            _childWithMeasureState.value = UiState.Empty
//            return
//        }
//
//        val ageResult = calculateAgeFromBirthToMeasure(
//            birth = child.birth,
//            measuredAt = latestMeasure.measuredAt
//        )
//
//        _childWithMeasureState.value = UiState.Success(
//            ChildWithMeasured(
//                id = child.id,
//                name = child.name,
//                ageResult = ageResult,
//                measurement = ChildMeasure(
//                    id = latestMeasure.id,
//                    measuredAt = latestMeasure.measuredAt,
//                    heightCm = latestMeasure.heightCm ?: 0.0,
//                    weightKg = latestMeasure.weightKg ?: 0.0,
//                    armCm = latestMeasure.armCm ?: 0.0,
//                    headCm = latestMeasure.headCm ?: 0.0
//                )
//            )
//        )
//
//    }
